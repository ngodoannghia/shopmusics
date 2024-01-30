package com.giaynhap.quanlynhac.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.giaynhap.quanlynhac.model.Category;
import com.giaynhap.quanlynhac.model.Music;

import org.springframework.data.domain.Page;

public class MusicCategoryResult {
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category  category;

    Page<Music> content;

    public Category getCategory() {
        return category;
    }
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public void setCategory(Category category) {
        this.category = category;
    }

    public Page<Music> getContent() {
        return content;
    }

    public void setContent(Page<Music> content) {
        this.content = content;
    }
}
