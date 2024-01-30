package com.giaynhap.quanlynhac.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity(name = "voucher")
@Table(name = "voucher")
public class Voucher {
    @Id
    @Column(name = "uuid", length=100)
    private String UUID;
    @Column(name = "code", length=255)
    private String code;
    @Column(name = "data", columnDefinition="TEXT")
    private String data;
    @Column(name = "type")
    private Integer type;
    @Column(name = "enable")
    private Boolean enable;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "time")
    private Long time;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
