package io.github.arthoura.rest.Dto;

import jakarta.validation.constraints.NotBlank;

public class EntryUserRequest {

    @NotBlank
    private String plate;

    @NotBlank
    private String type_vehicle;

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getType_vehicle() {
        return type_vehicle;
    }

    public void setType_vehicle(String type_vehicle) {
        this.type_vehicle = type_vehicle;
    }
}
