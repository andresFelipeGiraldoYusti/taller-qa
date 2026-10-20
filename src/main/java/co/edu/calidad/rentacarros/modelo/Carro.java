package co.edu.calidad.rentacarros.modelo;

public class Carro {

    private final String placa;
    private final String marca;
    private final String modelo;
    private final int anio;
    private final TipoCarro tipo;
    private final long tarifaDiaria;
    private final int kilometraje;
    private EstadoCarro estado;

    public Carro(String placa, String marca, String modelo, int anio, TipoCarro tipo, long tarifaDiaria, int kilometraje) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }
        if (marca == null || marca.isBlank() || modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("La marca y el modelo son obligatorios");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de carro es obligatorio");
        }
        if (tarifaDiaria <= 0) {
            throw new IllegalArgumentException("La tarifa diaria debe ser mayor que cero");
        }
        if (kilometraje < 0) {
            throw new IllegalArgumentException("El kilometraje no puede ser negativo");
        }
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.tipo = tipo;
        this.tarifaDiaria = tarifaDiaria;
        this.kilometraje = kilometraje;
        this.estado = EstadoCarro.DISPONIBLE;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnio() {
        return anio;
    }

    public TipoCarro getTipo() {
        return tipo;
    }

    public long getTarifaDiaria() {
        return tarifaDiaria;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public EstadoCarro getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarro estado) {
        this.estado = estado;
    }
}
