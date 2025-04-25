package io.github.arthoura.rest.Dto;

import jakarta.validation.constraints.NotNull;

public class InitParkingRequest {

    @NotNull
    private Integer size;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
