package com.giaynhap.quanlynhac.controller;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.ApiResponse;
import com.giaynhap.quanlynhac.model.Music;
import com.giaynhap.quanlynhac.model.UserStore;
import com.giaynhap.quanlynhac.model.Voucher;
import com.giaynhap.quanlynhac.service.MusicService;
import com.giaynhap.quanlynhac.service.UserService;
import com.giaynhap.quanlynhac.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*; 
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException; 
 
 
 
@RestController
public class VoucherController {
    @Autowired
    VoucherService voucherService;
    @Autowired
    MusicService musicService;
    @Autowired
    UserService userService;
    @RequestMapping(value = "/voucher/getall", method = RequestMethod.GET)
    public ApiResponse<?> getVouchers(){
        return  new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,voucherService.findAll());
    }
    @RequestMapping(value = "/voucher/save", method = RequestMethod.POST)
    public ApiResponse<?> saveVoucher(@RequestBody Voucher voucher){
        if (voucher.getUUID() == null){
            voucher.setUUID(UUID.randomUUID().toString());
            String code = Long.toString( ByteBuffer.wrap(voucher.getUUID().getBytes()).getLong(), Character.MAX_RADIX).toUpperCase() ;
            voucher.setCode(code);
        }
        if (voucher.getType() == 1){
            String uuid = voucher.getData();
            Music originMusic = musicService.getMusic(uuid);
            if ( originMusic== null){
                return  new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null);
            }
            voucher.setUUID(originMusic.getUUID());
        }
        voucherService.save(voucher);
        return  new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,voucher);
    }
	
    @RequestMapping(value = "/voucher/delete/{uuid}", method = RequestMethod.GET)
    public ApiResponse<?> deleteVoucher(@RequestParam("uuid") String uuid){
        Voucher v = voucherService.get(uuid);
        voucherService.delete(v);
        return  new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null);
    }
    @RequestMapping(value = "/user/voucher/{code}", method = RequestMethod.GET)
    public ApiResponse<?> useVoucher( @PathVariable("code") String code){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Voucher v = voucherService.getVoucher(code);
        if (v == null || v .getEnable() != true){
            return  new ApiResponse<>(1, AppConstant.ERROR_MESSAGE,null);
        }
        v.setEnable(false);
        voucherService.save(v);
        if (v.getType() == 1){
            UserStore userStore = null;
            Music music = musicService.getMusic(v.getData());
            UserStore lateUserStore = userService.getStoreMusic(music.getUUID(),detail.getUsername());
            if (lateUserStore != null) {
                userStore = lateUserStore;
                userStore.setExpire(lateUserStore.getExpire() + v.getTime().intValue());
            } else {
                userStore = new UserStore();
                userStore.setUsing(0);
                userStore.setExpire(v.getTime().intValue());
                userStore.setMusicUuid(music.getUUID());
                userStore.setMusic(music);
                userStore.setUserUuid(detail.getUsername());
                userStore.setCreate_at(LocalDateTime.now());
                userStore.setTimeStart(null);
            }
            userStore.setStatus(1);
            userService.updateStore(userStore);
            music.setUsedTime(userStore.getUsing());
            music.setExpire(userStore.getExpire());
            return  new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,music);
        }
        return  new ApiResponse<>(0, AppConstant.SUCCESS_MESSAGE,null);
    }



}
