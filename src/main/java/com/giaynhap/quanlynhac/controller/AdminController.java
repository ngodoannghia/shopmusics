package com.giaynhap.quanlynhac.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.AdminDTO;
import com.giaynhap.quanlynhac.dto.ApiResponse;
import com.giaynhap.quanlynhac.dto.AuthenRequest;
import com.giaynhap.quanlynhac.dto.AuthenResponse;
import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.service.*;
import com.giaynhap.quanlynhac.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class AdminController {
    @Autowired
    private AdminSevice adminSevice;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MusicService musicService;
    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;
    @Autowired
    BCrypt.Hasher bHasher;
    @Autowired
    private AmazonClientService amazonClientService;

    @Autowired
    private AppConstant appConstant;

    @Autowired
    private UtilService utilService;

    @RequestMapping(value = "/admin/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenRequest authenticationRequest) throws Exception {

        final Admin user = authenticate(authenticationRequest.getUsername(),authenticationRequest.getPassword());
        if (user == null){
            return ResponseEntity.ok(new ApiResponse<AuthenResponse<Admin>>(1, AppConstant.ERROR_MESSAGE,null));
        }
        final String token = jwtTokenUtil.generateToken(user);
        System.out.println("jwt token: " + token);
        AuthenResponse<Admin> authenResponse = new AuthenResponse<Admin>(token);
        user.setPassword("");
        authenResponse.setUser(user);
        return ResponseEntity.ok(new ApiResponse<AuthenResponse<Admin>>(0, AppConstant.SUCCESS_MESSAGE,authenResponse));
    }


    private Admin authenticate(String username, String password) throws Exception {
        Admin user = adminSevice.getByUserName(username);

        if (user  == null){
            return null;
        }
        BCrypt.Result  resul = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (resul == null || !resul.verified){
            return null;
        }
        return user;
    }


    @RequestMapping(value = "/admin/update", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> updateAdmin(@RequestBody Admin admin){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 
        Admin current = adminSevice.getByUUID(detail.getUsername());
		
        if (admin.getPassword() == null || admin.getPassword().isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null));
        }
        if (admin.getUsername() == null || admin.getUsername().isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null));
        }
        String hashString =  bHasher.hashToString(12,admin.getPassword().toCharArray());
        current.setPassword(hashString);
        admin = adminSevice.updateAdmin(current);
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,admin));
    }
	
	@RequestMapping(value = "/admin/delete/{uuid}", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> updateAdmin(@PathVariable("uuid") String uuid){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         adminSevice.delete(uuid);

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> addAdmin(@RequestBody Admin admin){
//        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (admin.getAccount() == null || admin.getAccount().isEmpty()){
//            return ResponseEntity.ok(new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null));
//        }
//        if (admin.getPassword() == null || admin.getPassword().isEmpty()){
//            return ResponseEntity.ok(new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null));
//        }
        String hashString =  bHasher.hashToString(12,admin.getPassword().toCharArray());
        admin.setUUID(UUID.randomUUID().toString());
		admin.setPassword(hashString);
		admin.setCreate_at(LocalDateTime.now());
        admin = adminSevice.updateAdmin(admin);
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,admin));
    }
    
    @RequestMapping(value = "/admin/signup", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> signupAdmin(@RequestBody AdminDTO adminDTO){
//        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (admin.getAccount() == null || admin.getAccount().isEmpty()){
//            return ResponseEntity.ok(new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null));
//        }
//        if (admin.getPassword() == null || admin.getPassword().isEmpty()){
//            return ResponseEntity.ok(new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null));
//        }
    	Admin admin = new Admin();
        String hashString =  bHasher.hashToString(12,adminDTO.getPassword().toCharArray());
        admin.setUUID(UUID.randomUUID().toString());
		admin.setPassword(hashString);
		admin.setUsername(adminDTO.getUsername());
		admin.setCreate_at(LocalDateTime.now());
		admin.setEnable(true);
        admin = adminSevice.updateAdmin(admin);
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,admin));
    }

    @RequestMapping(value = "/admin/list", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> getAdmin(){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE, adminSevice.getListAdmin()));
    }

    @RequestMapping(value = "/music/adminAdd", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> addMusic(@RequestBody Music music){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        music.setUUID(UUID.randomUUID().toString());
		music.setCreateAt(LocalDateTime.now());
		if (music.getParent() != null && music.getType() == AppConstant.MusicType.ORIGIN.getValue()){
		    Music parent = musicService.getMusic(music.getParent());
		    music.setCost(parent.getCost());
        }
        if (music.getTitle() == null ){
            music.setTitle("Không có tên");
        }
        music =  musicService.saveMusic(music);

        adminSevice.writeLog("Add music  "+ music.getTitle() +" - "+music.getUUID(), detail.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,music));
    }

    @RequestMapping(value = "/music/adminSaveMusic", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> saveMusic(@RequestBody Music music){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (music.getType() == AppConstant.MusicType.DEMO.getValue() && music.getStatus() == 1){
            Music oldMusic = musicService.getMusic(music.getUUID());
            if (oldMusic.getStatus() == 0){
                    utilService.sendNotiPublish(music.getTitle());
            }
        }
        if (music.getTitle() == null ){
            music.setTitle("Không có tên");
        }
        music =  musicService.saveMusic(music);

        adminSevice.writeLog("Save music  "+ music.getTitle() +" - "+music.getUUID(), detail.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,music));
    }
    @RequestMapping(value = "/music/adminDelete", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> deleteMusic(@RequestBody Music music){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (music.getType() == AppConstant.MusicType.ORIGIN.getValue()){
            amazonClientService.deleteFileRemote(fileService.getDemoSong(music.getUUID()));
        } else if (music.getType() == AppConstant.MusicType.DEMO.getValue()){
            amazonClientService.deleteFileRemote(fileService.getRealSong(music.getUUID()));
        }
        musicService.deleteMusic(music);
        adminSevice.writeLog("Delete music  "+ music.getTitle() +" - "+music.getUUID(), detail.getUsername());

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,music));
    }

    @RequestMapping(value = "/admin/penddingbuy/{page}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> getListPeddingBuy( @PathVariable("page") Integer page, @RequestParam(value = "limit",required = false) Integer limit){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int plimit = 20;
        if (limit != null){
            plimit = limit;
        }
        Page<PenddingBuy> origin = adminSevice.getPagePenddingBuy(page,plimit);
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,origin));
    }

    @RequestMapping(value = "/admin/penddingbuy/{uuid}/detail", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> detailPenddingBuy(@PathVariable("uuid") String uuid){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,adminSevice.detailPenddingBuy(uuid)));
    }

    @RequestMapping(value = "/admin/penddingbuy/{uuid}/accept", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> accpetPenddingBuy(@PathVariable("uuid") String uuid,@RequestBody List<String> uuids){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PenddingBuy detailBuy = adminSevice.detailPenddingBuy(uuid);
        for (String musicUUid : uuids) {
            UserStore userStore = new UserStore();
            userStore.setMusicUuid(musicUUid);
            userStore.setUsing(0);
            userStore.setExpire(0);
            userStore.setStatus(1);
            userStore.setCreate_at(LocalDateTime.now());
            userStore.setUserUuid(detailBuy.getUserUuid());
            userStore.setFileHash(musicUUid);
            userStore.setMusic(musicService.getMusic(musicUUid));
            try {
                userService.updateStore(userStore);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
            adminSevice.acceptPenddingBuy(uuid);
        if (detailBuy != null && detailBuy.getTitle() != null) {
            adminSevice.writeLog("Accept peddingbuy  "+ detailBuy.getTitle() +" - "+detailBuy.getUUID(), detail.getUsername());
        } else {
            adminSevice.writeLog("Accept peddingbuy  "+ uuid, detail.getUsername());
        }
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }
    @RequestMapping(value = "/admin/penddingbuy/{uuid}/reject", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> rejectPenddingBuy(@PathVariable("uuid") String uuid){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        adminSevice.rejectPenddingBuy(uuid);
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }

    @RequestMapping(value = "/admin/users/{page}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> getListUser( @PathVariable("page") Integer page, @RequestParam(value = "limit",required = false) Integer limit){
        int plimit = 20;
        if (limit != null){
            plimit = limit;
        }
        Page<User> origin = userService.getListUser(page , plimit);
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,origin));
    }
    @RequestMapping(value = "/admin/dashboard", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> getReportDashboard(){

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,adminSevice.getReport()));
    }


    @RequestMapping(value = "/admin/updateuser", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> updateUser(@RequestBody UserInfo user){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo info = userService.getUserInfo(user.getUUID());

        info.setGender(user.getGender());
        info.setFullname(info.getFullname());
        info.setBod(info.getBod());
        info.setAvatar(info.getAvatar());

        userService.updateUserInfo(info);
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,adminSevice.getReport()));
    }

    @RequestMapping(value = "/admin/updatestatus/{id}/{status}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> updateUserStatus(@PathVariable("id") String id, @PathVariable("status") Integer status){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUser(id);
        if (status == 1){
            user.setEnable(true);
        } else {
            user.setEnable(false);
        }
        userService.update( user );
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }

    @RequestMapping(value = "/admin/deleteuser/{id}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> updateUserStatus(@PathVariable("id") String id){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUser(id);
        userService.deleteUser(id);
        if (user != null && user.getAccount() != null) {
            adminSevice.writeLog("Delete user "+ user.getAccount(), detail.getUsername());
        } else {
            adminSevice.writeLog("Delete user "+ id, detail.getUsername());
        }

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }
	@RequestMapping(value = "/admin/getuser/{id}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> getUser(@PathVariable("id") String id){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,userService.getUser(id)));
    }



    @RequestMapping(value = "/music/all/{page}", method = RequestMethod.GET)
    public ApiResponse<?> getDemoList2(@PathVariable  int page,
                                       @RequestParam(value = "type",required = false) Optional<Integer> type,
                                       @RequestParam(value = "limit",required = false) Optional<Integer> limit,
                                       @RequestParam(value = "sortType",required = false) Optional<String> sortType
    ){
        int pageLimit = 20;
        boolean pageSortType = false;

        if (limit.isPresent()){
            pageLimit = limit.get();
        }
        if (sortType.isPresent() && sortType.get() == "desc"){
            pageSortType = true;
        }
        if (type.isPresent() && type.get() == 1){
            return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musicService.pageMusicAll(AppConstant.MusicType.ORIGIN,page,pageLimit,pageSortType));
        } else {
            return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musicService.pageMusicAll(AppConstant.MusicType.DEMO,page,pageLimit,pageSortType));
        }
    }



    @RequestMapping(value = "/admin/give_music/{useruuid}", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> giveUserMusic(@PathVariable("useruuid") String userUUID,@RequestBody Music music){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserStore userStore = new UserStore();
        userStore.setMusicUuid(music.getUUID());
        userStore.setUsing(0);
        userStore.setExpire(0);
        userStore.setStatus(1);
        userStore.setCreate_at(LocalDateTime.now());
        userStore.setUserUuid(userUUID);
        userStore.setFileHash(music.getUUID()); 
       
	    userStore.setMusic(musicService.getMusic(music.getUUID()));
        userService.updateStore(userStore);

        if (music != null && music.getTitle() != null) {
            adminSevice.writeLog("Give user "+ userUUID +" music" + music.getTitle(), detail.getUsername());
        } else {
            adminSevice.writeLog("Give user "+ userUUID +" music" + music.getUUID(), detail.getUsername());
        }
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,userStore));
    }

    @RequestMapping(value = "/admin/userstorage/remove/{id}/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> removeUserStorage(  @PathVariable("id") String id,@PathVariable("uuid") String uuid ){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Music music = musicService.getMusic(id);
        userService.delStore(userService.getStoreMusic(id, uuid));
        if (music != null && music.getTitle() != null) {
            adminSevice.writeLog("Remove user music" + music.getTitle(), detail.getUsername());
        } else {
            adminSevice.writeLog("Remove user music" + id, detail.getUsername());
        }

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }

    @RequestMapping(value = "/admin/userstorage/detail/{id}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> getUserStorageDetail( @PathVariable("id") Long id){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,userService.getStoreById(id)));
    }

 @RequestMapping(value = "/admin/user/{username}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> getUserStorageDetail(@PathVariable("username") String username){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	 User user =   userService.getUserName(username);

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,user ));
    }

 @RequestMapping(value = "/admin/user/storage/{uuid}/{page}", method = RequestMethod.GET)
    public ApiResponse<?> getStorage(@PathVariable  String uuid, @PathVariable  int page,
                                      @RequestParam(value = "limit",required = false) Optional<Integer> limit,
                                     @RequestParam(value = "status",required = false) Optional<Integer> status
    ){
        int pageLimit = 20;
       
        Page<Music> musics = null;
        if (limit.isPresent()){
            pageLimit = limit.get();
        }
        if (status.isPresent()){
            musics = userService.getUserStore(uuid,page,pageLimit,status.get());
        } else {
            musics = userService.getUserStore(uuid,page,pageLimit, null);
        }

        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musics);
    }


    @RequestMapping(value = "/admin/category/save", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<?>> saveCategory( @RequestBody Category category){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (category.getUUID() == null){
			category.setUUID(UUID.randomUUID().toString());
		}
        category = adminSevice.updateCategory(category);

        if (category != null && category.getTitle() != null) {
            adminSevice.writeLog("Update category  " + category.getTitle(), detail.getUsername());
        } else {
            adminSevice.writeLog("Update category  " + category.getUUID(), detail.getUsername());
        }
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,category));
    }

    @RequestMapping(value = "/admin/category/delete/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable("uuid")  String uuid){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Category category = adminSevice.getCategory(uuid);
        adminSevice.deleteCategory(uuid);
        if (category != null && category.getTitle() != null) {
            adminSevice.writeLog("Delete category  " + category.getTitle(), detail.getUsername());
        } else {
            adminSevice.writeLog("Delete category  " + uuid, detail.getUsername());
        }
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }

    @RequestMapping(value = "/admin/category/detail/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> detailCategory(@PathVariable("uuid")  String uuid){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,adminSevice.getCategory(uuid)));
    }

    @RequestMapping(value = "/admin/music/list/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> listByParentId(@PathVariable("uuid")  String uuid){
        @SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,adminSevice.listByParent(uuid)));
    }


    @RequestMapping(value = "/admin/resource/{uuid}", method = RequestMethod.GET)
    public ApiResponse<?> getMusicResource(@PathVariable String uuid,final HttpServletResponse httpResponse) throws  java.io.IOException{

        Music music = musicService.getMusic(uuid);
        if (music == null){
            return new ApiResponse<>(3,AppConstant.ERROR_MESSAGE,null);
        }

        if (music.getType() == AppConstant.MusicType.DEMO.getValue()){
            String urlResource = "";
            if (appConstant.disableStream.equals("true")) {
                urlResource = appConstant.hostAudio + "/demo/stream/" + music.getUUID() + "/" + music.getSlug() + ".mp3";
            } else {
                urlResource = amazonClientService.getResourceURL(fileService.getDemoSong(music.getUUID())).toExternalForm();
                if (urlResource != null){
                    urlResource =  urlResource.replace("https://","http://");
                }
            }
            return new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,urlResource);
        }

        String urlResource = "";

        if (appConstant.disableStream.equals("true")) {
            urlResource = appConstant.hostAudio+"/admin/stream/"+uuid+"/"+music.getSlug()+".mp3";
        } else {
            urlResource = amazonClientService.getResourceURL(fileService.getRealSong(music.getUUID())).toExternalForm();
            if (urlResource != null){
                urlResource =  urlResource.replace("https://","http://");
            }
        }
        return new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,urlResource);
    }

    @RequestMapping(value = "/admin/deletepending/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<?>> deletePedding(@PathVariable("uuid")  String uuid){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        adminSevice.deletePendingBuy(uuid);

        adminSevice.writeLog("Delete pedding buy  "+uuid,detail.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null));
    }


}
