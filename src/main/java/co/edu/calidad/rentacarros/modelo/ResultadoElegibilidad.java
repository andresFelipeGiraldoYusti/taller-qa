package co.edu.calidad.rentacarros.modelo;

public class ResultadoElegibilidad {

    private final boolean aceptada;
    private final String motivo;
    private final long dias;

    private ResultadoElegibilidad(boolean aceptada, String motivo, long dias) {
        this.aceptada = aceptada;
        this.motivo = motivo;
        this.dias = dias;
    }

    public static ResultadoElegibilidad aceptada(long dias) {
        return new ResultadoElegibilidad(true, "Cliente apto para rentar", dias);
    }

    public static ResultadoElegibilidad rechazada(String motivo) {
        return new ResultadoElegibilidad(false, motivo, 0);
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public String getMotivo() {
        return motivo;
    }

    public long getDias() {
        return dias;
    }
}
