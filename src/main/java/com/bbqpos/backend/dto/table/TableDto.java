package com.bbqpos.backend.dto.table;

import com.bbqpos.backend.model.RestaurantTable;

import java.time.LocalDateTime;

public class TableDto {

    private Long id;
    private String name;
    private String type;
    private String status;
    private int pax;
    private boolean isQrEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TableDto(RestaurantTable table) {
        this.id = table.getId();
        this.name = table.getName();
        this.type = table.getType().name();
        this.status = table.getStatus().name();
        this.pax = table.getPax();
        this.isQrEnabled = table.isQrEnabled();
        this.createdAt = table.getCreatedAt();
        this.updatedAt = table.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPax() {
        return pax;
    }

    public void setPax(int pax) {
        this.pax = pax;
    }

    public boolean isQrEnabled() {
        return isQrEnabled;
    }

    public void setQrEnabled(boolean qrEnabled) {
        isQrEnabled = qrEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
