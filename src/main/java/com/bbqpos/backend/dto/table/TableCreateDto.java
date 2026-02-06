package com.bbqpos.backend.dto.table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class TableCreateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotBlank
    private String status;

    @NotNull
    @Positive
    private int pax;

    private boolean isQrEnabled;

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

}
