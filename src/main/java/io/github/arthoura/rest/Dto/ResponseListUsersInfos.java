package io.github.arthoura.rest.Dto;

import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;

public class ResponseListUsersInfos {

    private Long userId;
    private String userName;
    private Vehicle vehicle;

    public ResponseListUsersInfos(Vehicle vehicle, Long userId, String userName) {
        this.vehicle = vehicle;
        this.userName = userName;
        this.userId = userId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
