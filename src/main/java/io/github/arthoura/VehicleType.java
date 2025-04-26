package io.github.arthoura;

public enum VehicleType {
    CAR {
        @Override
        public long calcularValor(long minutes) {
            // Regra: R$ 5,00 por hora
            return (long) (Math.ceil((double) minutes/60) * 5);
        }
    },

    MOTO {
        @Override
        public long calcularValor(long minutes) {
            // Regra: R$ 3,00 por hora
            return (long) (Math.ceil((double) minutes/60) * 3);
        }
    };

    public abstract long calcularValor(long minutes);
}
