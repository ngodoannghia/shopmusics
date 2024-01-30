package com.giaynhap.quanlynhac.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.giaynhap.quanlynhac.util.LocalDateTimeDeserializer;
import com.giaynhap.quanlynhac.util.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "user_info")
@Table(name = "user_info")
public class UserInfo {
    @Id
    @Column(name = "uuid", length=100)
    private String UUID;
    @Column(name = "fullname", length=255)
    private String fullname;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "bod")
    private LocalDateTime bod;
    @Column(name = "gender")
    private Integer gender;
    @Column(name = "avatar", length=255)
    private String avatar;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public LocalDateTime getBod() {
        return bod;
    }

    public void setBod(LocalDateTime bod) {
        this.bod = bod;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
