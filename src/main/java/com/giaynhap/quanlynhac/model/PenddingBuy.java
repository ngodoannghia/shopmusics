package com.giaynhap.quanlynhac.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.giaynhap.quanlynhac.util.LocalDateTimeDeserializer;
import com.giaynhap.quanlynhac.util.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "pendding_buy")
@Table(name= "pendding_buy")
public class PenddingBuy {
    @Id
    @Column(name = "uuid", length=100)
    private String UUID;

    @Column(name = "user_uuid", length=100)
    private String userUuid;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "note", columnDefinition="TEXT")
    private String note;

    @Column(name = "music_id", length=100)
    private String musicUuid;

    @Column(name = "user_name", length=255)
    private String userName;

    @Column(name = "time")
    private int time;

    @Column(name = "music_title", length=255)
    private String title;

    @Column(name="code", length=255)
    private String code;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendding_buy")
    private List<PenddingBuyDetail> details;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "status")
    private Integer status;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMusicUuid() {
        return musicUuid;
    }

    public void setMusicUuid(String musicUuid) {
        this.musicUuid = musicUuid;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<PenddingBuyDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PenddingBuyDetail> details) {
        this.details = details;
    }
}
