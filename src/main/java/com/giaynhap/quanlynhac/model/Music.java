package com.giaynhap.quanlynhac.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.giaynhap.quanlynhac.util.LocalDateTimeDeserializer;
import com.giaynhap.quanlynhac.util.LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;


@Entity(name = "music")
@Table(name = "music")
public class Music {
    @Id
    @Column(name = "uuid", length=100)
    private String UUID;

    @Column(name = "title", length=255)
    private String title;

    @Column(name = "description", columnDefinition="TEXT")
    private String description;

    @Column(name = "slug", columnDefinition="TEXT")
    private String slug;

    @Column(name = "parent", length=255)
    private String parent;

    @Column(name = "category", length=255)
    private String category;

    @Column(name = "author", length=255)
    private String author;

    @Column(name = "file_uuid", length=100)
    private String fileUuid;

    @Column(name = "thumb", length=255)
    private String thumb;

    @Column(name = "time")
    private Long time;

    @Column(name = "status")
    private Long status;

    @Column(name = "type")
    private Integer type;

    @Column(name = "cost")
    private Double cost;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "publish_at")
    private LocalDateTime publishAt;
    @Transient
    private Integer usedTime ;
    @Transient
    private Integer expire;
    @Transient
    public Integer getUsedTime() {
        return usedTime;
    }
    @Transient
    public Integer getExpire() {
        return expire;
    }
    @Transient
    public void setUsedTime(Integer usedTime) {
        this.usedTime = usedTime;
    }
    @Transient
    public void setExpire(Integer expire) {
        this.expire = expire;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(LocalDateTime publishAt) {
        this.publishAt = publishAt;
    }


}
