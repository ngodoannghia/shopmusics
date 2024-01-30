package com.giaynhap.quanlynhac.dto;

public class DashboardReport implements IDashboardReport {

    private Integer totalMusic;
    private Integer totalNewPenddingBuy;
    private Integer totalUser;
    private Integer totalRejectPenddingBuy;
    private Integer totalAcceptPenddingBuy;
    public DashboardReport(Integer totalMusic, Integer totalNewPenddingBuy, Integer totalUser, Integer totalRejectPenddingBuy) {
        this.totalMusic = totalMusic;
        this.totalNewPenddingBuy = totalNewPenddingBuy;
        this.totalUser = totalUser;
        this.totalRejectPenddingBuy = totalRejectPenddingBuy;
    }
    @Override
    public Integer getTotalMusic() {
        return totalMusic;
    }

    public void setTotalMusic(Integer totalMusic) {
        this.totalMusic = totalMusic;
    }
    @Override
    public Integer getTotalNewPenddingBuy() {
        return totalNewPenddingBuy;
    }

    public void setTotalNewPenddingBuy(Integer totalNewPenddingBuy) {
        this.totalNewPenddingBuy = totalNewPenddingBuy;
    }
    @Override
    public Integer getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(Integer totalUser) {
        this.totalUser = totalUser;
    }
    @Override
    public Integer getTotalRejectPenddingBuy() {
        return totalRejectPenddingBuy;
    }

    @Override
    public Integer getTotalAccept() {
        return totalAcceptPenddingBuy;
    }
    public void setTotalAccept(Integer i) {
          totalAcceptPenddingBuy = i ;
    }


    public void setTotalRejectPenddingBuy(Integer totalRejectPenddingBuy) {
        this.totalRejectPenddingBuy = totalRejectPenddingBuy;
    }
}
