package com.giaynhap.quanlynhac.controller;

import com.giaynhap.quanlynhac.manager.CacheQueue;
import com.giaynhap.quanlynhac.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CacheFileProcess {
    @Autowired
    CacheQueue cacheQueue;
    @Autowired
    CacheService cacheService;
    // 30s
    @Scheduled(fixedRate = 30*1000)
    public void processDoQueue() {
        if (cacheService.canSaveCache()) {
            cacheQueue.doQueue();
        } else {
		    cacheQueue.clear();
	    }
    }

    // 3h
    @Scheduled(fixedRate = 3*60*60*1000)
    public void processFreeCache() {
        try {
            cacheService.checkCacheFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
