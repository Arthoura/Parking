package io.github.arthoura.rest.Dto;

import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;

public class ResponseListUsersInfos {

    private Long userId;
    private String userName;
    private Vehicle vehicle;
    private String entry_time;

    public ResponseListUsersInfos(Vehicle vehicle, Long userId, String userName, String entry_time) {
        this.vehicle = vehicle;
        this.userName = userName;
        this.userId = userId;
        this.entry_time = entry_time;
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

    public String getEntry_time() {
        return entry_time;
    }

    public void setEntry_time(String entry_time) {
        this.entry_time = entry_time;
    }
}
