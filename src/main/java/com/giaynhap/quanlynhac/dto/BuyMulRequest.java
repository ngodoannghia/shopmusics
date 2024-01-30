package com.giaynhap.quanlynhac.dto;

import java.util.List;

public class BuyMulRequest {
    private Double totalCost;
    private String note;

    private List<BuyModel> items;

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<BuyModel> getItems() {
        return items;
    }

    public void setItems(List<BuyModel> items) {
        this.items = items;
    }
}
