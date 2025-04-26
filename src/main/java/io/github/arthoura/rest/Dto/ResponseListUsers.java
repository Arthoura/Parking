package io.github.arthoura.rest.Dto;

import io.github.arthoura.domain.model.Parking;
import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseListUsers {

    private int vehiclesInsideParking;
    private List<ResponseListUsersInfos> vehiclesInfos;

    public ResponseListUsers(int vehiclesSize, List<ResponseListUsersInfos> responseListUsersInfos) {
        this.vehiclesInsideParking = vehiclesSize;
        this.vehiclesInfos = responseListUsersInfos;
    }

    public static ResponseListUsers listVehicles(List<Parking> completeParking){
        List<ResponseListUsersInfos> collect = completeParking.stream()
                .map(cp -> new ResponseListUsersInfos(cp.getVehicle(), cp.getUser().getId(), cp.getUser().getName()))
                .collect(Collectors.toList());
        return new ResponseListUsers(completeParking.size(), collect);
    }

    public int getVehiclesSize() {
        return vehiclesInsideParking;
    }

    public void setVehiclesSize(int vehiclesSize) {
        this.vehiclesInsideParking = vehiclesSize;
    }

    public List<ResponseListUsersInfos> getVehiclesInfos() {
        return vehiclesInfos;
    }

    public void setVehiclesInfos(List<ResponseListUsersInfos> vehiclesInfos) {
        this.vehiclesInfos = vehiclesInfos;
    }
}
