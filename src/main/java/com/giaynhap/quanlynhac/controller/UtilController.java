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
import org.apache.commons.io.FilenameUtils;

@RestController
public class UtilController {
	private String save_data = "/root/database/";
	
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
    
    
	@PostMapping("/util/avatar/upload")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
		@SuppressWarnings("unused")
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		boolean status_avatar = false;
		String name_avatar = StringUtils.cleanPath(file.getOriginalFilename());	
        String ext = FilenameUtils.getExtension(name_avatar);
    	String path_avatar = "static/avatars/" + UUID.randomUUID().toString() + '.' + ext;  	
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
        String file = appConstant.hostImage + "static/avatar/"+uuid+".jpg";
      
        Long   fileSize = 0L;
        StreamingResponseBody streamer = null;

        if (cacheService.existCache(file)){
            CacheService.CacheEntry cacheMeta = cacheService.getFileMeta(file);
            fileSize = cacheMeta.getSize();
            streamer = cacheService.getFile(file,fileSize );

            response.setHeader("Content-Length", fileSize.toString());
            response.setHeader("Content-Disposition", "attachment; filename=\"avatar." + uuid + ".jpg\"");
            return streamer;
        }
        else{
            BufferedImage img = imageService.avatarLetterImage(uuid);
            response.setHeader("Content-Disposition", "attachment; filename=\"letter.giay.jpg\"");
            streamer = outputStream -> ImageIO.write(img, "jpg", outputStream );
            return streamer ;
        }

    }

	@PostMapping("/util/photo/upload")
    public ResponseEntity<?> savePhoto(@RequestParam("file") MultipartFile file) throws IOException {
        @SuppressWarnings("unused")
        UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		boolean status_photo = false;
		String name_photo = StringUtils.cleanPath(file.getOriginalFilename());
		String ext = FilenameUtils.getExtension(name_photo);
    	String path_photo = "static/photo/" + UUID.randomUUID().toString() + '.' + ext;  	
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
        String file = appConstant.hostImage + "/photo." + uuid + ".jpg";
        Long   fileSize = 0L;
        StreamingResponseBody streamer = null;
        if (cacheService.existCache(file)){
            CacheService.CacheEntry cacheMeta = cacheService.getFileMeta(file);
            fileSize = cacheMeta.getSize();
            streamer = cacheService.getFile(file,fileSize );

            response.setHeader("Content-Length", fileSize.toString());
            response.setHeader("Content-Disposition", "attachment; filename=\"photo." + uuid + ".jpg\"");
            return streamer;
        } 
        else{
            BufferedImage img = imageService.avatarLetterImage(uuid);
            response.setHeader("Content-Disposition", "attachment; filename=\"letter.giay.jpg\"");
            streamer = outputStream -> ImageIO.write(img, "jpg", outputStream );
            return streamer ;
        }

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
            urlMusic = constant.hostAudio + musicPath;
        } else {
            musicPath =  fileService.getRealSong(music.getUUID());
            urlMusic = constant.hostAudio + musicPath;
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

        // amazonClientService.uploadFileToRemotePrivate(musicPath, fileTemp );
        FileProcess obj_music = new FileProcess(save_data + musicPath, file);
        boolean status_music = obj_music.saveFile();

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
        // amazonClientService.changeRoles(fileService.getRealSong(uuid));
        return "ok";
    }
    @RequestMapping(value = "/utils/make_demo_public/{uuid}", method = RequestMethod.GET)
    public   @ResponseBody  String makeDemoPublic(HttpServletResponse response, @PathVariable("uuid") String uuid) throws Exception {
        // amazonClientService.changeRoles(fileService.getDemoSong(uuid));
        return "ok";
    }
    @RequestMapping(value = "/utils/get_public/{uuid}", method = RequestMethod.GET)
    public   @ResponseBody  String getPublicUrl(HttpServletResponse response, @PathVariable("uuid") String uuid) throws Exception {
        // return  amazonClientService.getResourceURL(fileService.getRealSong(uuid)).toExternalForm();
        return appConstant.hostAudio + fileService.getRealSong(uuid);
    }

}
