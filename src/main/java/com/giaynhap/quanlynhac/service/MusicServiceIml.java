package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.dto.BuyModel;
import com.giaynhap.quanlynhac.dto.BuyMulRequest;
import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import  org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class MusicServiceIml implements  MusicService {
    @Autowired
    MusicRepository  musicRepository;
    @Autowired
    PenddingBuyRepository penddingBuyRepository;
    @Autowired
    PenddingBuyDetailRepository penddingBuyDetailRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private UserStoreRepository userStoreRepository;

    @Override
    public Page<Music> pageMusic(AppConstant.MusicType type, int page, int limit, boolean sort) {
        Page<Music> pageMusics = null;
        Sort typeSort = null;
        if (sort){
            typeSort = Sort.by("create_at").descending();
        } else {
            typeSort = Sort.by("create_at").ascending();
        }
        Pageable pageable =  PageRequest.of(page, limit, typeSort );
        if (type == AppConstant.MusicType.DEMO) {
            pageMusics = musicRepository.pageMusicDemoWithSort(pageable);
        } else {
            pageMusics = musicRepository.pageMusicWithSort(pageable);
        }
        return pageMusics;
    }

    @Override
    public Page<Music> pageMusicByCategory(AppConstant.MusicType type, String category, int page, int limit, boolean sort) {
        Page<Music> pageMusics = null;
        Sort typeSort = null;
        if (sort){
            typeSort = Sort.by("create_at").descending();
        } else {
            typeSort = Sort.by("create_at").ascending();
        }
        Pageable pageable =  PageRequest.of(page, limit, typeSort );
        if (type == AppConstant.MusicType.DEMO) {
            pageMusics = musicRepository.pageMusicDemoWithSort(category,pageable);
        } else {

        }
        return pageMusics;
    }

    @Override
    public Page<Music> pageMusicAll(AppConstant.MusicType type, int page, int limit, boolean sort) {
        Page<Music> pageMusics = null;
        Sort typeSort = null;
        typeSort = Sort.by("create_at").descending();
        Pageable pageable =  PageRequest.of(page, limit, typeSort );
        if (type == AppConstant.MusicType.DEMO) {
            pageMusics = musicRepository.pageMusicDemoAll(pageable);
        } else {
            pageMusics = musicRepository.pageMusicWithSort(pageable);
        }
        return pageMusics;
    }

    @Override
    public Music getMusic(String uuid){
        return   musicRepository.findById(uuid).get();
    }


    @Override
    public Music saveMusic(Music music) {
        if (music.getUUID() == null){
            music.setUUID(UUID.randomUUID().toString());
        }
        music = musicRepository.save(music);
        return music;
    }

    @Override
    public void deleteMusic(Music music) {
        userStoreRepository.deleteByMusicId(music.getUUID());
        musicRepository.delete(music);
    }

    @Override
    public void deleteMusic(String uuid) {
        musicRepository.deleteById(uuid);
    }

    @Override
    public PenddingBuy buy(String userUUID, Music music,int time, String userName, Double cost) {
        PenddingBuy penddingBuy = new PenddingBuy();
        penddingBuy.setCreateAt(LocalDateTime.now());
        penddingBuy.setMusicUuid(music.getUUID());
		if (music.getCost() == null){
			music.setCost(0.0);
		}
        penddingBuy.setCost(time/60 * music.getCost());
        penddingBuy.setNote("");
        penddingBuy.setUUID(UUID.randomUUID().toString());
        penddingBuy.setStatus(0);
        penddingBuy.setUserUuid(userUUID);
        penddingBuy.setUpdateAt(LocalDateTime.now());
        penddingBuy.setTime(time);
        penddingBuy.setUserName(userName);
        penddingBuy.setTitle(music.getTitle());
        String code = Long.toString( ByteBuffer.wrap(penddingBuy.getUUID().getBytes()).getLong(), Character.MAX_RADIX).toUpperCase() ;
        penddingBuy.setCode(code);

        PenddingBuyDetail detail = new PenddingBuyDetail();
        detail.setCost(cost);
        detail.setMusicUuid(music.getUUID());
        detail.setPenddingUUID(penddingBuy.getUUID());
        detail.setTitle(music.getTitle());
        detail.setTime(new Long(time));
        penddingBuyDetailRepository.save(detail);
        return penddingBuyRepository.save(penddingBuy);
    }

    @Override
    public PenddingBuy buy(String userUUID,String userName, BuyMulRequest request) {

        PenddingBuy penddingBuy = new PenddingBuy();
        penddingBuy.setCreateAt(LocalDateTime.now());

        penddingBuy.setCost(request.getTotalCost());
        penddingBuy.setNote("");
        penddingBuy.setUUID(UUID.randomUUID().toString());
        penddingBuy.setStatus(0);
        penddingBuy.setUserUuid(userUUID);
        penddingBuy.setUpdateAt(LocalDateTime.now());
        penddingBuy.setUserName(userName);

        String code = Long.toString( ByteBuffer.wrap(penddingBuy.getUUID().getBytes()).getLong(), Character.MAX_RADIX).toUpperCase() ;
        penddingBuy.setCode(code);

        penddingBuy.setMusicUuid("");
        penddingBuy.setTitle("");
        Double totalCost = 0.0;
        for (BuyModel model : request.getItems()){
            try {
                Music music = musicRepository.getOne(model.getUUID());
                if (music == null) {
                    return null;
                }

                PenddingBuyDetail detail = new PenddingBuyDetail();
				if (music.getCost() == null){
					music.setCost(0.0);
				}
				if (model.getTime() == null){
					model.setTime(0l);
				}
                detail.setCost(model.getTime()/60 * music.getCost() );
                detail.setMusicUuid(music.getUUID());
                detail.setPenddingUUID(penddingBuy.getUUID());
                detail.setTitle(music.getTitle());
                detail.setTime(model.getTime());
                totalCost += detail.getCost();

                penddingBuyDetailRepository.save(detail);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
        penddingBuy.setCost(totalCost);
        return penddingBuyRepository.save(penddingBuy);

    }

    @Override
    public List<Category> listCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategory(String uuid) {
        return categoryRepository.getOne(uuid);
    }
}
