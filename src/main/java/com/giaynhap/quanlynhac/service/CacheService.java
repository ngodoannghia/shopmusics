package com.giaynhap.quanlynhac.service;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.giaynhap.quanlynhac.controller.MediaStreamer;
import com.google.gson.Gson;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CacheService {
    @Value("${giaynap.cachefolder}")
    private String cacheFolder;
    // 2 ngay
    private Long expire = 60l * 1000l * 60l * 24l * 2l;
    // 3gb
    private Long limitCacheSize = 1073741824L*3;

    public static class CacheEntry{
        private String realUrl;
        private Long size;
        private Long expire;
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getRealUrl() {
            return realUrl;
        }

        public void setRealUrl(String realUrl) {
            this.realUrl = realUrl;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public Long getExpire() {
            return expire;
        }

        public void setExpire(Long expire) {
            this.expire = expire;
        }
    }

    String genName(String url){

        System.out.println("ge "+url);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(url.getBytes("UTF-8"));
            byte[] digest = md.digest();
            String myHash = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
            return myHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    void CacheManager(){

    }

    public CacheEntry getFileMeta(String url) throws IOException {
        String name = genName(url);
        String headerText = readAllText(cacheFolder+"/meta_"+name);
        try {
            Gson gson = new Gson();
            CacheEntry data = gson.fromJson(headerText, CacheEntry.class);
            return data;
        } catch (Exception e){
            return null;
        }
    }

    public MediaStreamer getFile(String url, Long offset, Long len) throws IOException {
        String name = genName(url);
        String fileName = cacheFolder+"/"+name;
        RandomAccessFile fileAccess = new RandomAccessFile(fileName,"r");
        fileAccess.seek(offset);
        return new MediaStreamer(len, fileAccess);
    }
    public  MediaStreamer getFile(String url) throws IOException {
        String name = genName(url);
        String fileName = cacheFolder+"/"+name;
        RandomAccessFile fileAccess = new RandomAccessFile(fileName,"r");
        return new MediaStreamer(fileAccess.length(), fileAccess);
    }
    public  MediaStreamer getFile(String url, Long len) throws IOException {
        String name = genName(url);
        String fileName = cacheFolder+"/"+name;
        RandomAccessFile fileAccess = new RandomAccessFile(fileName,"r");
        return new MediaStreamer(len, fileAccess);
    }

    public void deleteCache(String url){
        String name = genName(url);
        String fileName = cacheFolder+"/"+name;
        File file  = new File(fileName);
        if (file.exists()){
            file.delete();
        }
    }
    public void moveToCache(String url, File file) throws IOException {
        String name = genName(url);
        System.out.println("save "+name);
        String fileName = cacheFolder+"/"+name;
        String fileHeaderName = cacheFolder+"/meta_"+name;
        File tmpFile = new File(fileName);
        if (tmpFile.exists()){
            tmpFile.delete();
        }
        file.renameTo(tmpFile);
        CacheEntry entry = new CacheEntry();

        entry.expire = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + expire ;
        entry.size = tmpFile.length();
        entry.realUrl = url;
        entry.fileName = name;
        Gson gson = new Gson();

        String meta = gson.toJson(entry);
        FileUtil.writeAsString(new File(fileHeaderName),meta);
    }

    public boolean existCache(String url){

        try {
            Long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            CacheEntry cacheEntry =  getFileMeta(url);
            if (cacheEntry.expire <= now){
                System.out.println("time expire");
                return false;
            }
            return  true;
        } catch (IOException e) {
            e.printStackTrace();
           return false;
        }

    }


    public void saveCache(S3ObjectInputStream stream,String url) throws IOException, InterruptedException {
        String name = genName(url);
        System.out.println("save "+name);
        String fileName = cacheFolder+"/"+name;
        String fileHeaderName = cacheFolder+"/meta_"+name;

        byte[] buf = new byte[1024];
        OutputStream out = new FileOutputStream(fileName);
        int count = 0;
        while( (count = stream.read(buf)) != -1)
        {
            if( Thread.interrupted() )
            {
                throw new InterruptedException();
            }
            out.write(buf, 0, count);
        }
        out.close();
        stream.close();

        CacheEntry entry = new CacheEntry();
        File tmpFile = new File(fileName);
        entry.expire = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + expire ;
        entry.size = tmpFile.length();
        entry.realUrl = url;
        entry.fileName = name;
        Gson gson = new Gson();

        String meta = gson.toJson(entry);
        FileUtil.writeAsString(new File(fileHeaderName),meta);
    }

    String readAllText(String path) throws IOException {

        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");
        return str;
    }
    public boolean canSaveCache(){
        if (folderSize(new File(cacheFolder)) < limitCacheSize){
            return  true;
        }
        return false;
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }


    public  void checkCacheFile() throws IOException {
        File dir = new File(cacheFolder);
        File [] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("meta_");
            }
        });
        Long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ;

        for (File file : files) {

            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String headerText = new String(data, "UTF-8");

            try {
                Gson gson = new Gson();
                CacheEntry cacheMeta = gson.fromJson(headerText, CacheEntry.class);
                if (cacheMeta.expire > now){
                    continue;
                }
                file.delete();
                File dataFile = new File(cacheFolder+"/"+cacheMeta.fileName);
                dataFile.delete();

            } catch (Exception e){
              continue;
            }
        }

    }
}
