package com.giaynhap.quanlynhac.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "pendding_buy_detail")
@Table(name = "pendding_buy_detail")
public class PenddingBuyDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name="music_id", length=100)
    private String musicUuid;

    @Column(name = "p_uuid", length=100)
    private String penddingUUID;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penbuy_uuid")
    private PenddingBuy pendding_buy;

    @Column(name = "title", columnDefinition="TEXT")
    private String title;

    @Column(name = "cost")
    private Double cost;
    
    @Column(name = "time")
    private Long time;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
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

    public String getPenddingUUID() {
        return penddingUUID;
    }

    public void setPenddingUUID(String penddingUUID) {
        this.penddingUUID = penddingUUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
    
    public PenddingBuy getPenddingBuy() {
    	return this.pendding_buy;
    }
    public void setPenddingBuy(PenddingBuy penddingBuy) {
    	this.pendding_buy = penddingBuy;
    }
}
