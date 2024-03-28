package com.giaynhap.quanlynhac.controller;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.ApiResponse;
import com.giaynhap.quanlynhac.service.*;
import com.giaynhap.quanlynhac.util.FileProcess;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.*;
import java.io.File;
import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
@RestController
public class UtilController {
	private String save_data = "src/main/resources/";
	
    @Autowired
    UtilService utilService;
    @Autowired
    AppConstant constant;
    @Autowired
    ImageService imageService;
    @Autowired
    FileService fileService;
    @Autowired
    MusicService musicService;
    @Autowired
    AppConstant appConstant;
    @Autowired
    UserService  userService  ;
    @Autowired
    AmazonClientService amazonClientService;
    @Autowired
    CacheService cacheService;


//    @PostMapping("/util/avatar/upload")
//    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
//        @SuppressWarnings("unused")
//		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String uuid = UUID.randomUUID().toString();
//         byte[] bytes = file.getBytes();
//        String avatarPath = constant.avatarPath ;
//        File dir = new File(avatarPath);
//
//        File serverFile = new File(dir.getAbsolutePath() + File.separator + "avatar."+uuid+".jpg");
//        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
//        stream.write(bytes);
//        stream.close();
//        //
//        String avatarUrl = constant.hostImage+"/util/avatar/"+uuid;
//        System.out.println("Server file: " + serverFile);
//        
//	    try {
//	        amazonClientService.uploadFileToRemote(appConstant.photo, "avatar." + uuid + ".jpg", serverFile);
//	        if (appConstant.disableStream.equals("true")){
//	            avatarUrl = amazonClientService.getResourceURL(appConstant.photo,"avatar." + uuid + ".jpg").toExternalForm();
//	            if (avatarUrl != null){
//	                avatarUrl =  avatarUrl.replace("https://","http://");
//	            }
//	        }
//	    } catch ( Exception e){
//	        System.out.println("amazon upload image error ");
//	        e.printStackTrace();
//	    }
//
//        // serverFile.delete();
//	    cacheService.moveToCache(appConstant.photo+"/avatar."+uuid+".jpg",serverFile);
//        return ResponseEntity.ok(new ApiResponse<String>(0, AppConstant.SUCCESS_MESSAGE,avatarUrl));
//    }
    
    
	@PostMapping("/util/avatar/upload")
    public ResponseEntity<?> saveAvater(@RequestParam("file") MultipartFile file) throws IOException {
		@SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		boolean status_avatar = false;
		String name_avatar = StringUtils.cleanPath(file.getOriginalFilename());	
    	String path_avatar = "/static/avatars/" + UUID.randomUUID().toString() + "_" + name_avatar;  	
		try {
	    	FileProcess obj_avatar = new FileProcess(save_data + path_avatar, file);
	    	status_avatar = obj_avatar.saveFile();
	    	
		} catch (Exception e) {
	        System.out.println("save image error ");
	        e.printStackTrace();
		}
		
    	if (status_avatar) {
    		return ResponseEntity.ok(new ApiResponse<String>(0, AppConstant.SUCCESS_MESSAGE,path_avatar));
    	}
    	else {
    		return ResponseEntity.ok(new ApiResponse<String>(1, AppConstant.BAD_REQUEST_MESSAGE,""));
    	}
    }
    

    @RequestMapping(value = "/util/avatar/{uuid}", method = RequestMethod.GET)
    public  @ResponseBody  StreamingResponseBody downloadAvatar(HttpServletResponse response, @PathVariable("uuid") String uuid, @RequestParam(name = "width",required = false) Integer width, @RequestParam(name = "height",required = false) Integer height) throws Exception {
        String file = appConstant.photo+"/avatar."+uuid+".jpg";
        try{
            Long   fileSize = 0L;
            StreamingResponseBody streamer = null;
            if (cacheService.existCache(file)){
               CacheService.CacheEntry cacheMeta = cacheService.getFileMeta(file);
               fileSize = cacheMeta.getSize();
               streamer = cacheService.getFile(file,fileSize );
            } else {
                fileSize = amazonClientService.fileSize(file);
                S3ObjectInputStream finalObject = amazonClientService.getMusic( file );
                if (cacheService.canSaveCache()){
                    cacheService.saveCache(finalObject,file);
                    streamer = cacheService.getFile(file,fileSize );
                } else {
                    streamer = output -> {
                        int numberOfBytesToWrite = 0;
                        byte[] data = new byte[1024];
                        while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                            output.write(data, 0, numberOfBytesToWrite);
                        }
                        finalObject.close();
                    };
                }
            }
        response.setHeader("Content-Length", fileSize.toString());
        response.setHeader("Content-Disposition", "attachment; filename=\"avatar." + uuid + ".jpg\"");
        return streamer;
	}catch (Exception e){

            BufferedImage img = imageService.avatarLetterImage(uuid);
            response.setHeader("Content-Disposition", "attachment; filename=\"letter.giay.jpg\"");
            StreamingResponseBody streamer = outputStream -> ImageIO.write(img, "jpg", outputStream );
            return streamer ;
	    }
    }

//    @PostMapping("/util/photo/upload")
//    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
//        @SuppressWarnings("unused")
//		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String uuid = UUID.randomUUID().toString();
//        byte[] bytes = file.getBytes();
//        String avatarPath = constant.avatarPath ;
//        File dir = new File(avatarPath);
//        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//        BufferedImage img =  ImageIO.read(bais);
//        File serverFile = new File(dir.getAbsolutePath() + File.separator + "photo."+uuid+".jpg");
//        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
//        ImageIO.write(img, "jpg", stream);
//        stream.close();
//      //  String avatarUrl = constant.hostImage+"/util/photo/"+uuid;
//        String avatarUrl = constant.hostImage + "/util/photo/" + uuid;
//        try {
//            amazonClientService.uploadFileToRemote(appConstant.photo, "photo." + uuid + ".jpg", serverFile);
//            avatarUrl = amazonClientService.getResourceURL(appConstant.photo, "photo." + uuid + ".jpg").toExternalForm();
//            if (avatarUrl != null) {
//                avatarUrl = avatarUrl.replace("https://", "http://");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        //serverFile.delete();
//	    cacheService.moveToCache(appConstant.photo+"/photo."+uuid+".jpg",serverFile);
//        return ResponseEntity.ok(new ApiResponse<String>(0, AppConstant.SUCCESS_MESSAGE,avatarUrl));
//    }

	@PostMapping("/util/photo/upload")
    public ResponseEntity<?> savePhoto(@RequestParam("file") MultipartFile file) throws IOException {
		@SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		boolean status_photo = false;
		String name_photo = StringUtils.cleanPath(file.getOriginalFilename());	
    	String path_photo = "/static/photo/" + UUID.randomUUID().toString() + "_" + name_photo;  	
		try {
	    	FileProcess obj_photo = new FileProcess(save_data + path_photo, file);
	    	status_photo = obj_photo.saveFile();
	    	
		} catch (Exception e) {
	        System.out.println("save image error ");
	        e.printStackTrace();
		}
		
    	if (status_photo) {
    		return ResponseEntity.ok(new ApiResponse<String>(0, AppConstant.SUCCESS_MESSAGE,appConstant.hostImage + path_photo));
    	}
    	else {
    		return ResponseEntity.ok(new ApiResponse<String>(1, AppConstant.BAD_REQUEST_MESSAGE,""));
    	}
    }
    @RequestMapping(value = "/util/photo/{uuid}", method = RequestMethod.GET)
    public   @ResponseBody  StreamingResponseBody  downloadPhoto(HttpServletResponse response, @PathVariable("uuid") String uuid, @RequestParam(name = "width",required = false) Integer width, @RequestParam(name = "height",required = false) Integer height) throws Exception {
            String file = appConstant.photo+"/photo."+uuid+".jpg";
        Long   fileSize = 0L;
        StreamingResponseBody streamer = null;
        if (cacheService.existCache(file)){
            CacheService.CacheEntry cacheMeta = cacheService.getFileMeta(file);
            fileSize = cacheMeta.getSize();
            streamer = cacheService.getFile(file,fileSize );
        } else {
            fileSize = amazonClientService.fileSize(file);
            S3ObjectInputStream finalObject = amazonClientService.getMusic( file );
            if (cacheService.canSaveCache()){
                cacheService.saveCache(finalObject,file);
                streamer = cacheService.getFile(file,fileSize );
   
            } else {
                streamer = output -> {
                    int numberOfBytesToWrite = 0;
                    byte[] data = new byte[1024];
                    while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                        output.write(data, 0, numberOfBytesToWrite);
                    }
                    finalObject.close();
                };
            }
        }
        response.setHeader("Content-Length", fileSize.toString());
        response.setHeader("Content-Disposition", "attachment; filename=\"photo." + uuid + ".jpg\"");
        return streamer;
    }



    @SuppressWarnings({ "removal", "unused" })
	@PostMapping("/util/music/upload/{uuid}")
    public ResponseEntity<?> uploadMusic(@RequestParam("file") MultipartFile file, @PathVariable("uuid") String uuid) throws IOException {
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userUuid = detail.getUsername();
        Music music = musicService.getMusic(uuid);


        byte[] bytes = file.getBytes();
        String musicPath = "";
        String urlMusic = "";
        if (music.getType() == AppConstant.MusicType.DEMO.getValue()){
            musicPath =  fileService.getDemoSong(music.getUUID());
             urlMusic = constant.hostAudio+"/demo/stream/"+music.getUUID()+"/"+music.getSlug()+".mp3";
        } else {
            musicPath =  fileService.getRealSong(music.getUUID());
            urlMusic = constant.hostAudio+"/admin/stream/"+music.getUUID()+"/"+music.getSlug()+".mp3";
        }
        String urlTemp = appConstant.musicPath+ "/temp_"+UUID.randomUUID().toString()+".mp3";
        File fileTemp = new File(urlTemp);

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(urlTemp));
        stream.write(bytes);
        stream.close();

		Long duration = 0L;
        try{
            FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");
            FFmpegProbeResult probeResult = ffprobe.probe(urlTemp);
            FFmpegFormat format = probeResult.getFormat();


            duration = new Double(format.duration).longValue();

            music.setTime( duration );
            musicService.saveMusic(music);

        }catch (Exception e){
            fileTemp.delete();
            return ResponseEntity.ok(new ApiResponse<String>(2,e.toString(),null));
        }
        cacheService.deleteCache(musicPath);

        amazonClientService.uploadFileToRemotePrivate(musicPath, fileTemp );
        cacheService.moveToCache(musicPath,fileTemp);

        return ResponseEntity.ok(new ApiResponse<String>(0, AppConstant.SUCCESS_MESSAGE,urlMusic));
    }

    @RequestMapping(value = "/utils/letter/avatar/{name}", method = RequestMethod.GET)
    public   @ResponseBody  byte[]  downloadPhoto(HttpServletResponse response, @PathVariable("name") String name) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (name == null){
            return stream.toByteArray();
        }
        BufferedImage img = imageService.avatarLetterImage(name);
        ImageIO.write(img, "jpg", stream);
        response.setHeader("Content-Disposition", "attachment; filename=\"letter.giay.jpg\"");
        return stream.toByteArray();
    }
    @RequestMapping(value = "/utils/make_public/{uuid}", method = RequestMethod.GET)
    public   @ResponseBody  String makePublic(HttpServletResponse response, @PathVariable("uuid") String uuid) throws Exception {
        amazonClientService.changeRoles(fileService.getRealSong(uuid));
        return "ok";
    }
    @RequestMapping(value = "/utils/make_demo_public/{uuid}", method = RequestMethod.GET)
    public   @ResponseBody  String makeDemoPublic(HttpServletResponse response, @PathVariable("uuid") String uuid) throws Exception {
        amazonClientService.changeRoles(fileService.getDemoSong(uuid));
        return "ok";
    }
    @RequestMapping(value = "/utils/get_public/{uuid}", method = RequestMethod.GET)
    public   @ResponseBody  String getPublicUrl(HttpServletResponse response, @PathVariable("uuid") String uuid) throws Exception {
        return  amazonClientService.getResourceURL(fileService.getRealSong(uuid)).toExternalForm();
    }

}
