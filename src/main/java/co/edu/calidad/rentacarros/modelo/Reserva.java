package co.edu.calidad.rentacarros.modelo;

import java.time.LocalDate;

public class Reserva {

    private final long id;
    private final Cliente cliente;
    private final Carro carro;
    private final LocalDate inicio;
    private final LocalDate fin;
    private final long dias;
    private final long total;
    private EstadoReserva estado;

    public Reserva(long id, Cliente cliente, Carro carro, LocalDate inicio, LocalDate fin, long dias, long total) {
        this.id = id;
        this.cliente = cliente;
        this.carro = carro;
        this.inicio = inicio;
        this.fin = fin;
        this.dias = dias;
        this.total = total;
        this.estado = EstadoReserva.ACTIVA;
    }

    public long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Carro getCarro() {
        return carro;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public long getDias() {
        return dias;
    }

    public long getTotal() {
        return total;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}
