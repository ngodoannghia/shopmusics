package com.giaynhap.quanlynhac.manager;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.giaynhap.quanlynhac.service.AmazonClientService;
import com.giaynhap.quanlynhac.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.Queue;
@Component
public class CacheQueue {
    @Autowired
    AmazonClientService amazonClientService;
    @Autowired
    CacheService cacheService;
    public Integer limitTake = 10;
    public static class CacheQueueEntry{
        public String file;
        public Long expire;


        public CacheQueueEntry(String file, Long expire) {
            this.file = file;
            this.expire = expire;
        }

        @Override
        public boolean equals(Object obj) {
           if (!(obj instanceof CacheQueueEntry)){
                return false;
           }
           CacheQueueEntry temp = (CacheQueueEntry)obj;
           if (temp.file.equals(file)){
               return true;
           }
           return false;
        }

    }
    Queue<CacheQueueEntry> queueRequest
            = new LinkedList<>();

    public void add(String file, Long expire){
        CacheQueueEntry entry = new CacheQueueEntry(file, LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + expire);

        if (queueRequest.contains(entry)){
            return;
        }
        System.out.println("add Queue "+entry.file +" ex: "+entry.expire);
        queueRequest.add(entry );
    }

    public void doQueue(){
        if (queueRequest.size()  <= 0){
            return;
        }
        System.out.println("Start queue "+queueRequest.size() );
        Long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ;
        int coundown = limitTake;
        while (coundown > 0){
            coundown--;
            CacheQueueEntry entry = queueRequest.poll();
            if (entry == null  ){
                continue;
            }
            if (entry.expire == null || entry.expire < now){
                continue;
            }
            System.out.println("do Queue "+entry.file +" ex: "+entry.expire);
            doEntry(entry);
        }
    }
    private void doEntry(CacheQueueEntry e){
        try {
            S3ObjectInputStream stream = amazonClientService.getMusic(e.file);
            cacheService.saveCache(stream, e.file);
        }catch ( Exception er){
            er.printStackTrace();
            return;
        }
    }
	public void clear(){
		queueRequest.clear();
	}
}
