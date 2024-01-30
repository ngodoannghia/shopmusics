package com.giaynhap.quanlynhac.controller;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.manager.CacheQueue;
import com.giaynhap.quanlynhac.manager.MusicManager;
import com.giaynhap.quanlynhac.model.FileStore;
import com.giaynhap.quanlynhac.model.Music;
import com.giaynhap.quanlynhac.model.User;
import com.giaynhap.quanlynhac.model.UserStore;
import com.giaynhap.quanlynhac.service.*;
import org.apache.catalina.connector.Response;
import org.apache.poi.openxml4j.opc.internal.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RestController
public class StreamController {
    final int chunk_size = 1024 * 1024 * 5;

    @Autowired
    UserService userService;

    @Autowired
    AppConstant appConstant;

    @Autowired
    FileService fileService;
    @Autowired
    MusicManager musicManager;
    @Autowired
    CacheService cacheService;
    @Autowired
    CacheQueue cacheQueue;
    @Autowired
    AmazonClientService amazonClientService;
    private StreamingResponseBody errorStream(HttpServletResponse response, String slug){
        response.setStatus(Response.SC_OK);
        response.setContentType("audio/mp3");
        response.setHeader("Content-Length", "0");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("GnMessage", "Audio not set");
        response.setHeader("Content-Disposition", "attachment; filename=" + slug + ".mp3");
        return new MediaStreamer((long) 0, null);
    }
    @RequestMapping(value = "/stream/{id}/{uuid}/{slug}", method = RequestMethod.GET)
    public  @ResponseBody
    StreamingResponseBody getSong(HttpServletResponse response,
                   @PathVariable("id") Long id,
                   @PathVariable("uuid") String uuid,
                   @PathVariable("slug") String slug,
                   @RequestHeader(value = "Range",required = false) String range
    ) throws Exception {

       /* if (userService.getStoreById(id) == null){
            return errorStream(response, slug);
        }*/
        String file = fileService.getRealSong(uuid);
        Long fileSize = 0l;
        StreamingResponseBody streamer;
        Boolean hasCache = false;
        if (cacheService.existCache(file)) {
            fileSize = cacheService.getFileMeta(file).getSize();
            hasCache = true;
        } else {
            //1p
            cacheQueue.add(file,60l*60l*1000l);
            if (amazonClientService.fileExist(file)) {
                fileSize = amazonClientService.fileSize(file);
            } else {
                return errorStream(response, slug);
            }
        }

        response.setStatus(Response.SC_PARTIAL_CONTENT);
        response.setContentType("audio/mpeg"); 
		response.setHeader("Accept-Ranges", "bytes"); 
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
		response.setHeader("Last-Modified", "Mon, 26 Oct 2020 09:58:58 GMT"); 
        response.setHeader("Content-Disposition", "attachment; filename="+slug);

        if (range == null) {
            if (hasCache){
                streamer = cacheService.getFile(file,fileSize );
            } else {
                S3ObjectInputStream finalObject = amazonClientService.getMusic(file);
                streamer = output -> {
                    int numberOfBytesToWrite = 0;
                    byte[] data = new byte[2048];
                    while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                        output.write(data, 0, numberOfBytesToWrite);
                    }
                    finalObject.close();
                };
            }
			response.setHeader("Content-Length", fileSize.toString());
            return streamer;

        }
        String[] ranges = range.split("=")[1].split("-");
        final int from = Integer.parseInt(ranges[0]);
        int to = chunk_size + from;
        if (to >= fileSize) {
            to = (int) (fileSize - 1);
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("bytes %d-%d/%d", from, to, fileSize);

        final int len = to - from + 1;
        if (hasCache){
            streamer = cacheService.getFile(file, new Long(from), new Long(to) );
        } else {
            S3ObjectInputStream finalObject = amazonClientService.getMusic(file, new Long(from), new Long(to));
            streamer = output -> {
                int numberOfBytesToWrite = 0;
                byte[] data = new byte[2048];
                while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                    output.write(data, 0, numberOfBytesToWrite);
                }
                finalObject.close();
            };
        }

        response.setHeader("Content-Range", responseRange);
        response.setHeader("Content-Length", len + "");
        return streamer;
    }



    @RequestMapping(value = "/demo/stream/{uuid}/{slug}", method = RequestMethod.GET)
    public  @ResponseBody
    StreamingResponseBody getDemoSongStream(HttpServletResponse response,
                                            @PathVariable("uuid") String uuid,
                                            @PathVariable("slug") String slug,
                                            @RequestHeader(value = "Range",required = false) String range
    ) throws Exception {

        String file = fileService.getDemoSong(uuid);
        Long fileSize = 0l;
        StreamingResponseBody streamer;
        Boolean hasCache = false;
        if (cacheService.existCache(file)) {
            fileSize = cacheService.getFileMeta(file).getSize();
            hasCache = true;
        } else {
            //3P
            cacheQueue.add(file,60l*60l*1000l*3);
            if (amazonClientService.fileExist(file)) {
                fileSize = amazonClientService.fileSize(file);
            } else {
                return errorStream(response, slug);
            }

        }
        response.setStatus(Response.SC_PARTIAL_CONTENT);
        response.setContentType("audio/mpeg");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Last-Modified", "Mon, 26 Oct 2020 09:58:58 GMT");
        response.setHeader("Content-Disposition", "attachment; filename="+slug);
        if (range == null) {

            if (hasCache){
                streamer = cacheService.getFile(file,fileSize );
            } else {
                S3ObjectInputStream finalObject = amazonClientService.getMusic(file);

                streamer = output -> {
                    int numberOfBytesToWrite = 0;
                    byte[] data = new byte[2048];
                    while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                        output.write(data, 0, numberOfBytesToWrite);
                    }
                    finalObject.close();
                };
            }


            response.setHeader("Content-Length", fileSize.toString());
            return streamer;
        }



        String[] ranges = range.split("=")[1].split("-");
        final int from = Integer.parseInt(ranges[0]);
        int to = chunk_size + from;
        if (to >= fileSize) {
            to = (int) (fileSize - 1);
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("bytes %d-%d/%d", from, to, fileSize);
        final int len = to - from + 1;

        if (hasCache) {
            streamer = cacheService.getFile(file, new Long(from), new Long(to));
        } else {
            S3ObjectInputStream finalObject = amazonClientService.getMusic(file, new Long(from), new Long(to));
            streamer = output -> {
                int numberOfBytesToWrite = 0;
                byte[] data = new byte[2048];
                while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                    output.write(data, 0, numberOfBytesToWrite);
                }
                finalObject.close();
            };
        }

        response.setHeader("Content-Range", responseRange);
        response.setHeader("Content-Length", len + "");
        return streamer;
    }


  @RequestMapping(value = "/admin/stream/{uuid}/{slug}", method = RequestMethod.GET)
    public  @ResponseBody
    StreamingResponseBody getRealSongStream(HttpServletResponse response,
                                  @PathVariable("uuid") String uuid,
                                  @PathVariable("slug") String slug,
                                  @RequestHeader(value = "Range",required = false) String range
    ) throws Exception {

        String file = fileService.getRealSong(uuid);
        Long fileSize = 0l;
        Boolean hasCache = false;
        StreamingResponseBody streamer;
        if (cacheService.existCache(file)) {
              fileSize = cacheService.getFileMeta(file).getSize();
              hasCache = true;
        } else {
              if (amazonClientService.fileExist(file)) {
                  fileSize = amazonClientService.fileSize(file);
              } else {
                  return errorStream(response, slug);
              }
              //3P
              cacheQueue.add(file,60l*60l*1000l*3);
        }
        response.setStatus(Response.SC_PARTIAL_CONTENT);
        response.setContentType("audio/mpeg"); 
		response.setHeader("Accept-Ranges", "bytes"); 
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
		response.setHeader("Last-Modified", "Mon, 26 Oct 2020 09:58:58 GMT"); 
        response.setHeader("Content-Disposition", "attachment; filename="+slug);
        if (range == null) {
            if (hasCache){
                streamer = cacheService.getFile(file,fileSize );
            } else {
                S3ObjectInputStream finalObject = amazonClientService.getMusic(file);
                streamer = output -> {
                    int numberOfBytesToWrite = 0;
                    byte[] data = new byte[2048];
                    while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                        output.write(data, 0, numberOfBytesToWrite);
                    }
                    finalObject.close();
                };
            }

            response.setHeader("Content-Length", fileSize.toString());
            return streamer;
        }
		
        String[] ranges = range.split("=")[1].split("-");
        final int from = Integer.parseInt(ranges[0]);
        int to = chunk_size + from;
        if (to >= fileSize) {
            to = (int) (fileSize - 1);
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("bytes %d-%d/%d", from, to, fileSize);
        final int len = to - from + 1;
      if (hasCache){
          streamer = cacheService.getFile(file, new Long(from), new Long(to) );
      } else {
          S3ObjectInputStream finalObject = amazonClientService.getMusic(file, new Long(from), new Long(to));
          streamer = output -> {
              int numberOfBytesToWrite = 0;
              byte[] data = new byte[2048];
              while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                  output.write(data, 0, numberOfBytesToWrite);
              }
              finalObject.close();
          };
      }

      response.setHeader("Content-Range", responseRange);
      response.setHeader("Content-Length", len + "");
      return streamer;
    }


}
