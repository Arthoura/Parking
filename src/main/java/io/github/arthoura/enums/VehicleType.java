package io.github.arthoura.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum VehicleType {
    CAR {
        @Override
        public long calcularValor(long minutes) {
            // Regra: R$ 5,00 por hora, com 5 minutos de tolerância
            if(minutes <= 5){
                return (long) 0;
            }
            return (long) (Math.ceil((double) minutes/60) * 5);
        }
    },
    TRUCK {
        @Override
        public long calcularValor(long minutes) {
            // Regra: R$ 10,00 por hora, sem tolerância
            return (long) (Math.ceil((double) minutes/60) * 10);
        }
    },

    MOTO {
        @Override
        public long calcularValor(long minutes) {
            // Regra: R$ 3,00 por hora
            if(minutes <= 5){
                return (long) 0;
            }
            return (long) (Math.ceil((double) minutes/60) * 3);
        }
    };


    public static boolean isValidType(String vehicleType){
        try {
            VehicleType.valueOf(vehicleType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static List<String> getVehiclesAsList() {
        return Arrays.stream(VehicleType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public abstract long calcularValor(long minutes);
}
