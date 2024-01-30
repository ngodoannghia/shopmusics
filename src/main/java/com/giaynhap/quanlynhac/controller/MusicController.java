package com.giaynhap.quanlynhac.controller;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.ApiResponse;
import com.giaynhap.quanlynhac.dto.BuyModel;
import com.giaynhap.quanlynhac.dto.BuyMulRequest;
import com.giaynhap.quanlynhac.dto.MusicCategoryResult;
import com.giaynhap.quanlynhac.manager.MusicManager;
import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.service.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@RestController
public class MusicController {
    @Autowired
    MusicService musicService;
    @Autowired
    UserService userService;
    @Autowired
    AppConstant appConstant;
    @Autowired
    FileService fileService;

    @Autowired
    MusicManager musicManager;
    @Autowired
    AmazonClientService amazonClientService;

    @RequestMapping(value = "/music/demo/{page}", method = RequestMethod.GET)
    public ApiResponse<?> getDemoList(@PathVariable  int page,
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
        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musicService.pageMusic(AppConstant.MusicType.DEMO,page,pageLimit,pageSortType));
    }
    @RequestMapping(value = "/music/category/{uuid}/{page}", method = RequestMethod.GET)
    public ApiResponse<?> getDemoCategoryList(@PathVariable  int page,
                                              @PathVariable("uuid")  String category,
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
        MusicCategoryResult result = new MusicCategoryResult();
        result.setCategory(musicService.getCategory(category));
        result.setContent(musicService.pageMusicByCategory(AppConstant.MusicType.DEMO,category,page,pageLimit,pageSortType));
        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE, result);
    }

    @RequestMapping(value = "/music/categories", method = RequestMethod.GET)
    public ApiResponse<?> getDemoCategoryList(){

        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musicService.listCategory());
    }



    @RequestMapping(value = "/music/detail/{uuid}", method = RequestMethod.GET)
    public ApiResponse<?> getMusic(@PathVariable  String uuid){
        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musicService.getMusic(uuid));
    }

    @RequestMapping(value = "/music/{page}", method = RequestMethod.GET)
    public ApiResponse<?> getMusicList(@PathVariable  int page,
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
        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musicService.pageMusic(AppConstant.MusicType.ORIGIN,page,pageLimit,pageSortType));
    }

    @RequestMapping(value = "/music/storage/{page}", method = RequestMethod.GET)
    public ApiResponse<?> getStorage(@PathVariable  int page,
                                      @RequestParam(value = "limit",required = false) Optional<Integer> limit,
                                     @RequestParam(value = "status",required = false) Optional<Integer> status
    ){
        int pageLimit = 20;
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<Music> musics = null;
        if (limit.isPresent()){
            pageLimit = limit.get();
        }
        if (status.isPresent()){
            musics = userService.getUserStore(detail.getUsername(),page,pageLimit,status.get());
        } else {
            musics = userService.getUserStore(detail.getUsername(),page,pageLimit, null);
        }

        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musics);
    }

    @RequestMapping(value = "/music/buy", method = RequestMethod.POST)
    public ApiResponse<?>  buyMusic(@RequestBody BuyModel buyMusic){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = musicService.getMusic(buyMusic.getUUID());

        if (music == null){
            return new ApiResponse<>(1,AppConstant.ERROR_MESSAGE,null);
        }

        try{
		    User userBuy = userService.getUser(detail.getUsername());
            PenddingBuy po = musicService.buy(detail.getUsername(),music,buyMusic.getTime().intValue(),  userBuy.getInfo().getFullname(),buyMusic.getCost());
            return new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,po.getCode());
		}catch ( org.hibernate.exception.ConstraintViolationException e){
			return new ApiResponse<>(2,AppConstant.ERROR_MESSAGE,null);
		}

    }

    @RequestMapping(value = "/music/buylist", method = RequestMethod.POST)
    public ApiResponse<?>  buyMusics(@RequestBody BuyMulRequest buyMusic){
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (buyMusic.getItems()  == null ){
            return new ApiResponse<>(1,AppConstant.ERROR_MESSAGE,null);
        }
        if (buyMusic.getTotalCost() == 0){
            return new ApiResponse<>(2,AppConstant.ERROR_MESSAGE,null);
        }

        User userBuy = userService.getUser(detail.getUsername());
        PenddingBuy p =  musicService.buy(userBuy.getUUID(),userBuy.getInfo().getFullname(),buyMusic);

        if (p == null){
            return new ApiResponse<>(3,AppConstant.ERROR_MESSAGE,null);
        }
        return new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,p.getCode());
    }
    @RequestMapping(value = "/music/getResource/{uuid}", method = RequestMethod.GET)
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
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (detail == null){
 			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "401");
			 return new ApiResponse<>(401,AppConstant.ERROR_MESSAGE,null);
		}
        UserStore store = userService.getStoreMusic(uuid,detail.getUsername());
		if (store == null ) {
			 return new ApiResponse<>(1,AppConstant.ERROR_MESSAGE,null);
		}
        if (store.getStatus() == 0){
            return new ApiResponse<>(2,AppConstant.ERROR_MESSAGE,null);
        }
		 
       
        System.out.println("[Resource] getResource "+store.getFileHash());
        String urlResource = "";

        if (appConstant.disableStream.equals("true")) {
            urlResource = appConstant.hostAudio+"/stream/"+store.getId()+"/"+store.getFileHash()+"/"+music.getSlug()+".mp3";
        } else {
            urlResource = amazonClientService.getResourceURL(fileService.getRealSong(music.getUUID())).toExternalForm();
            if (urlResource != null){
                urlResource =  urlResource.replace("https://","http://");
            }
        }
        return new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,urlResource);
    }




}
