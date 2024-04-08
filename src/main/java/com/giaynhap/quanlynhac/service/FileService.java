package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.config.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    @Autowired
    AppConstant appConstant;

    public String getDemoSong(String uuid){
        return "static/musics/demo/"+uuid+".mp3";
    }
    public String getRealSong(String uuid){
        return "static/musics/song/"+uuid+".mp3";
    }
}
