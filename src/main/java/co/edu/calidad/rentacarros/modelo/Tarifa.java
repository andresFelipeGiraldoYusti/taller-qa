package co.edu.calidad.rentacarros.modelo;

public class Tarifa {

    private final long dias;
    private final long total;

    public Tarifa(long dias, long total) {
        this.dias = dias;
        this.total = total;
    }

    public long getDias() {
        return dias;
    }

    public long getTotal() {
        return total;
    }
}
