package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.BuyMulRequest;
import com.giaynhap.quanlynhac.model.Category;
import com.giaynhap.quanlynhac.model.Music;
import com.giaynhap.quanlynhac.model.PenddingBuy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicService {
   Page<Music> pageMusic(AppConstant.MusicType type, int page, int limit, boolean sort);

   Page<Music> pageMusicByCategory(AppConstant.MusicType type, String category, int page, int limit, boolean sort);

   Page<Music> pageMusicAll(AppConstant.MusicType type, int page, int limit, boolean sort);
   Music getMusic(String uuid);

   Music saveMusic(Music music);
   void deleteMusic(Music music);
   void deleteMusic(String uuid);
 
   PenddingBuy buy(String userUUID, Music music,int time, String userName, Double cost);
   PenddingBuy buy(String userUUID,String userName, BuyMulRequest request);
   List<Category> listCategory();
   Category getCategory(String uuid);
}
