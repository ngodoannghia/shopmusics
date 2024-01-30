package com.giaynhap.quanlynhac.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.giaynhap.quanlynhac.util.LocalDateTimeDeserializer;
import com.giaynhap.quanlynhac.util.LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity(name = "category")
@Table(name = "category")
public class Category {
    @Id
    @Column(name = "uuid", length=100)
    private String UUID;
    @Column(name = "title", length=255)
    private String title;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "creat_at")
    private LocalDateTime creat_at;

    @Column(name = "enable")
    private Boolean enable;
    @Column(name = "thumb")
    private String thumb;


    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreat_at() {
        return creat_at;
    }

    public void setCreat_at(LocalDateTime creat_at) {
        this.creat_at = creat_at;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
