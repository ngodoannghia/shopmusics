package com.giaynhap.quanlynhac.controller;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.ApiResponse;
import com.giaynhap.quanlynhac.dto.BuyModel;
import com.giaynhap.quanlynhac.dto.BuyMulRequest;
import com.giaynhap.quanlynhac.dto.MusicCategoryResult;
import com.giaynhap.quanlynhac.manager.MusicManager;
import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.FileInputStream;

@RestController
public class MusicController {
    private String save_data = "/root/database/";

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
    public ApiResponse<?> getDemoList(@PathVariable("page")  int page,
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
    public ApiResponse<?> getDemoCategoryList(@PathVariable("page")  int page,
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
    public ApiResponse<?> getMusic(@PathVariable("uuid")  String uuid){
        return  new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,musicService.getMusic(uuid));
    }

    @RequestMapping(value = "/music/{page}", method = RequestMethod.GET)
    public ApiResponse<?> getMusicList(@PathVariable("page")  int page,
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
    public ApiResponse<?> getStorage(@PathVariable("page")  int page,
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

        System.out.println(buyMusic.getUUID());

        Music music = musicService.getMusic(buyMusic.getUUID());

        if (music == null){
            return new ApiResponse<>(1,AppConstant.ERROR_MESSAGE,null);
        }

        try{
		    User userBuy = userService.getUser(detail.getUsername());
            PenddingBuy po = musicService.buy(detail.getUsername(),music,buyMusic.getTime().intValue(),  userBuy.getUsername(), buyMusic.getCost());
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
                urlResource = appConstant.hostServer + "demo/stream/" + music.getUUID() + "/" + music.getSlug() + ".mp3";
            } else {
                // urlResource = amazonClientService.getResourceURL(fileService.getDemoSong(music.getUUID())).toExternalForm();
                // if (urlResource != null){
                //     urlResource =  urlResource.replace("https://","http://");
                // }
                urlResource = appConstant.hostAudio + fileService.getDemoSong(music.getUUID());
            }
            return new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,urlResource);
        }
        System.out.println("Vao ==================");
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Done ==============");
        System.out.println(detail);
		if (detail == null){
			return new ApiResponse<>(401,AppConstant.ERROR_MESSAGE,null); 
		}
        UserStore store = userService.getStoreMusic(uuid,detail.getUsername());
		if (store == null ) {
            System.out.println("Store = null");
			 return new ApiResponse<>(1,AppConstant.ERROR_MESSAGE,null);
		}
        if (store.getStatus() == 0){
            System.out.println("status = 0");
            return new ApiResponse<>(2,AppConstant.ERROR_MESSAGE,null);
        }
		 
       
        System.out.println("[Resource] getResource "+store.getFileHash());
        String urlResource = "";

        if (appConstant.disableStream.equals("true")) {
            urlResource = appConstant.hostServer+"stream/"+store.getId()+"/"+store.getFileHash()+"/"+music.getSlug()+".mp3";
        } else {
            // urlResource = amazonClientService.getResourceURL(fileService.getRealSong(music.getUUID())).toExternalForm();
            // if (urlResource != null){
            //     urlResource =  urlResource.replace("https://","http://");
            // }
            urlResource = appConstant.hostAudio + fileService.getRealSong(music.getUUID());
        }    
            
        return new ApiResponse<>(0,AppConstant.SUCCESS_MESSAGE,urlResource);
    }

    @RequestMapping(value = "/music/stream/getResource/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> playAudio(@PathVariable("uuid") String uuid) throws java.io.IOException {
        // Logic to retrieve audio file based on characterId
        Music music = musicService.getMusic(uuid);

        if (music.getType() == AppConstant.MusicType.DEMO.getValue()){
            String filePath = save_data + fileService.getDemoSong(music.getUUID());

            File audioFile = new File(filePath);
            long contentLength = audioFile.length();
    
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(audioFile));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentLength(contentLength);

            return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
        }
        else{
            UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (detail == null){
                System.out.println("detail null");
                // httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "401");
                return new ResponseEntity<>(null, null, HttpStatus.UNAUTHORIZED);
            }
            UserStore store = userService.getStoreMusic(uuid,detail.getUsername());
            if (store == null ) {
                System.out.println("store null");
                return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
            }
            if (store.getStatus() == 0){
                System.out.println("status = 0");
                return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
            }

            String filePath = save_data + fileService.getRealSong(music.getUUID());

            File audioFile = new File(filePath);
            long contentLength = audioFile.length();
    
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(audioFile));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentLength(contentLength);
            
            return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
        }   
    }
}
