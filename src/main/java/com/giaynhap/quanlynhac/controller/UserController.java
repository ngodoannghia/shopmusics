package com.giaynhap.quanlynhac.controller;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.ApiResponse;
import com.giaynhap.quanlynhac.dto.AuthenRequest;
import com.giaynhap.quanlynhac.dto.AuthenResponse;
import com.giaynhap.quanlynhac.dto.PasswordChangeRequest;
import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.service.SessionTokenService;
import com.giaynhap.quanlynhac.service.UserServiceIml;
import com.giaynhap.quanlynhac.service.UtilService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserServiceIml userService;
    @Autowired

    private AppConstant constant;
    @Autowired
    BCrypt.Hasher bHasher;

    @Autowired
    private UtilService utilService;

    @Autowired
    SessionTokenService sessionTokenService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String test(){
        return "test";
    }

    @RequestMapping(value = "/makepass/{password}", method = RequestMethod.GET)
    public String  testEncryptPassword(@PathVariable("password") String password) throws Exception {
        String hashString =  bHasher.hashToString(12,password.toCharArray());
        return hashString ;
    }

    @RequestMapping(value = "/user/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenRequest authenticationRequest) throws Exception {
		try {
			final User user = authenticate(authenticationRequest.getUsername(),authenticationRequest.getPassword());
			if (user == null || user.isEnable() != true){
				return ResponseEntity.ok(new ApiResponse<AuthenResponse<User>>(1, AppConstant.ERROR_MESSAGE,null));
			}
			final String token = sessionTokenService.makeToken(user.getAccount(),user.getUUID());
			sessionTokenService.addToken(user.getUUID(), token );
			AuthenResponse<User> authenResponse = new AuthenResponse<User>(token);
			user.setPassword("");
			authenResponse.setUser(user);
			return ResponseEntity.ok(new ApiResponse<AuthenResponse<User>>(0, AppConstant.SUCCESS_MESSAGE,authenResponse));
		} catch (Exception e){
			return ResponseEntity.ok(new ApiResponse<AuthenResponse<User>>(2, e.getMessage(),null));
		}
    }

    @RequestMapping(value = "/user/info", method = RequestMethod.GET)
     public ResponseEntity<?>  getInfo(){
        UserDetails detail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userUUID =  detail.getUsername(); 
        User user =  userService.getUser(userUUID);
        UserInfo userInfo = user.getInfo();
        return ResponseEntity.ok(new ApiResponse<UserInfo>(0, AppConstant.SUCCESS_MESSAGE,userInfo));
    }

    @RequestMapping(value = "/user/update-password", method = RequestMethod.POST)
    public ResponseEntity<?>  updateInfo(@RequestBody PasswordChangeRequest requestModifyPassword){
        UserDetails detail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userService.getUser(detail.getUsername());

        BCrypt.Result  resul = BCrypt.verifyer().verify(requestModifyPassword.getOldPassword().toCharArray(), user.getPassword());
        if (resul == null || !resul.verified){
            return ResponseEntity.ok(new ApiResponse<String>(1, AppConstant.ERROR_MESSAGE,null));
        }

        String hashString =  bHasher.hashToString(12,requestModifyPassword.getPassword().toCharArray());
        user.setPassword(hashString);
        userService.update(user);
        user.setPassword("");
        return ResponseEntity.ok(new ApiResponse<User>(0, AppConstant.SUCCESS_MESSAGE,user));
    }

    @RequestMapping(value = "/user/update-info", method = RequestMethod.POST)
    public ResponseEntity<?>  updateInfo(@RequestBody UserInfo modifyInfo){
        UserDetails detail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserInfo info = userService.getUserInfo(detail.getUsername());
        info.setBod(modifyInfo.getBod());
        info.setFullname(modifyInfo.getFullname());
        info.setGender( modifyInfo.getGender() );
		info.setAvatar(modifyInfo.getAvatar());
        userService.updateUserInfo(info);
        return ResponseEntity.ok(new ApiResponse<User>(0, AppConstant.SUCCESS_MESSAGE,null));
    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public ResponseEntity<?>  register(@RequestBody User user){
        UserInfo userInfo = user.getInfo();
        if (user.getInfo() == null || user.getInfo().getFullname() == null || user.getAccount() == null || user.getPassword() == null ){
            return ResponseEntity.ok(new ApiResponse<User>(1, AppConstant.ERROR_MESSAGE,null));
        }
        user.setUUID(UUID.randomUUID().toString());
        userInfo.setUUID(user.getUUID());
        if (userInfo.getAvatar() == null || userInfo.getAvatar().isEmpty()) {
            userInfo.setAvatar(constant.hostImage+"/utils/letter/avatar/"+user.getInfo().getFullname());
        }

        String hashString =  bHasher.hashToString(12,user.getPassword().toCharArray());
        user.setPassword(hashString);
        user.setCreate_at(LocalDateTime.now());
        user.setEnable(false);
        utilService.sendVeryEmail(sessionTokenService.makeToken(user.getAccount(),userInfo.getUUID(),false,"register"),user.getAccount());
        try {
            userService.update(user);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok(new ApiResponse<User>(2, AppConstant.ERROR_MESSAGE,null));
        }
        user.setPassword("");
        return ResponseEntity.ok(new ApiResponse<User>(0, AppConstant.SUCCESS_MESSAGE,user));
    }
    private User authenticate(String username, String password) throws Exception {

        User user =   userService.getUserName(username);
        if (user  == null){
			throw new Exception ("Không tồn tại tài khoản này");
            
        }
        BCrypt.Result  resul = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (resul == null || !resul.verified){
			throw new Exception ("Mật khẩu không chính xác");
      
        }
        return user;
    }

     @RequestMapping(value = "/user/info/{uuid}", method = RequestMethod.GET)
     public ResponseEntity<?>  getInfoUser(@PathVariable("uuid") String uuid){
       
        User user =  userService.getUser(uuid);
        UserInfo userInfo = user.getInfo();
        return ResponseEntity.ok(new ApiResponse<UserInfo>(0, AppConstant.SUCCESS_MESSAGE,userInfo));
    }

    @RequestMapping(value = "/user/forget/sendemail/{email}", method = RequestMethod.GET)
    public ResponseEntity<?>  sendEmailForgetPassword(@PathVariable("email") String email){
        String toEmail = email;
        User user =   userService.getUserName(toEmail);

        if (user  == null){
            return ResponseEntity.ok(new ApiResponse<UserInfo>(1, AppConstant.ERROR_MESSAGE,null));
        }

        utilService.sendVeryEmail(sessionTokenService.makeToken(user.getAccount(),user.getUUID(),false,"forget"),user.getAccount());
        return ResponseEntity.ok(new ApiResponse<UserInfo>(0, AppConstant.SUCCESS_MESSAGE,null));
    }
    @RequestMapping(value = "/very/email", method = RequestMethod.GET)
    public String  veryEmailRegister(@RequestParam("token") String token, HttpServletResponse response) throws IOException {

        SessionTokenService.ObjectToken objToken = sessionTokenService.validateDisable(token);
        if (objToken == null){
            return "<code>Đường dẫn không hợp lệ hoặc đã hết hiệu lực thực hiện!</code>";
        } else {
            if (objToken.action != null && objToken.action.equals("forget")){
                User user = userService.getUser(objToken.uuid);

                String newPass = Long.toString( objToken.expire, Character.MAX_RADIX).toUpperCase();
				
				BCrypt.Result  resul = BCrypt.verifyer().verify(newPass.toCharArray(), user.getPassword());
				if (resul == null || !resul.verified){
					 
					String hashString =  bHasher.hashToString(12,newPass.toCharArray());
					user.setPassword(hashString);
					userService.update(user);
					utilService.sendVeryLostPassword(newPass,user.getAccount());
					response.sendRedirect(constant.hostImage + "/very/success");

					return "<h2>Xác thực thành công</h2><br><div>Mật khẩu đã được gửi tới email của bạn</div>";
				}
                return "<code>Đường dẫn không hợp lệ hoặc đã hết hiệu lực thực hiện!</code>";
				
            } else {
                User user = userService.getUser(objToken.uuid);
				if (user.isEnable() == true){
					return "<code>Đường dẫn không hợp lệ!</code>";
				} else {
					user.setEnable(true);
					userService.update(user);
					return "<h2>Xác thực thành công</h2><br><div>Tiếp tục đăng nhập</div>";
				}
            }
        }
    }

    @RequestMapping(value = "/very/success", method = RequestMethod.GET)
    public String  veryEmailRegister() throws IOException {
        return "<h2>Xác thực thành công</h2><br><div>Mật khẩu đã được gửi tới email của bạn</div>";
    }


}
