package com.giaynhap.quanlynhac.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.giaynhap.quanlynhac.util.LocalDateTimeDeserializer;
import com.giaynhap.quanlynhac.util.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "user_store")
@Table(name = "user_store")
public class UserStore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "music_id", insertable = false, updatable = false, length=100)
    private String musicUuid;

    @Column(name = "user_id", length=100)
    private String userUuid;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "create_at")
    private LocalDateTime create_at;

    @Column(name = "status")
    private  Integer status;

    @Column(name = "expire")
    private  Integer expire;

    @Column(name = "using_time")
    private  Integer using;

    @Column(name = "file_hash", length=255)
    private String fileHash;

    @Column(name = "time_start")
    private LocalDateTime timeStart;

    @ManyToOne( optional = true,cascade = CascadeType.ALL)
    @JoinColumn(name = "music_id", nullable=true)
    private Music music;

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMusicUuid() {
        return musicUuid;
    }

    public void setMusicUuid(String musicUuid) {
        this.musicUuid = musicUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public Integer getUsing() {
        return using;
    }

    public void setUsing(Integer using) {
        this.using = using;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    @Override
    public boolean equals(Object obj) {
        if ( !( obj instanceof UserStore ) ){
            return  false;
        }
        try {
            UserStore temp = (UserStore) obj;
            if (temp.getId() != null && this.getId() != null && this.getId() == temp.getId()) {
                return true;
            } else {
                return temp.getMusicUuid().equals(this.getMusicUuid()) && this.getUserUuid().equals(temp.getUserUuid());
            }
        }catch (Exception e){
            return super.equals(obj);
        }
    }
}
