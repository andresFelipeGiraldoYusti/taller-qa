package co.edu.calidad.rentacarros.aceptacion;

import co.edu.calidad.rentacarros.modelo.*;
import co.edu.calidad.rentacarros.repositorio.RepositorioCarros;
import co.edu.calidad.rentacarros.repositorio.RepositorioReservas;
import co.edu.calidad.rentacarros.servicio.CalculadoraTarifa;
import co.edu.calidad.rentacarros.servicio.RentaException;
import co.edu.calidad.rentacarros.servicio.ServicioReserva;
import io.cucumber.java.es.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaSteps {

    private final RepositorioCarros repositorioCarros = new RepositorioCarros();
    private final RepositorioReservas repositorioReservas = new RepositorioReservas();
    private final ServicioReserva servicioReserva = new ServicioReserva(repositorioCarros, repositorioReservas, new CalculadoraTarifa());

    private Cliente cliente;
    private Reserva reservaCreada;
    private String mensajeError;

    @Dado("que se tiene un carro registrado con placa {string}, marca {string}, modelo {string}, año {int}, tipo {string} y tarifa diaria de {long}")
    public void registrarCarro(String placa, String marca, String modelo, int anio, String tipo, long tarifaDiaria) {
        Carro carro = new Carro(placa, marca, modelo, anio, TipoCarro.valueOf(tipo), tarifaDiaria, 10_000);
        servicioReserva.registrarCarro(carro);
    }

    @Dado("el carro {string} se encuentra en estado {string}")
    public void cambiarEstadoCarro(String placa, String estado) {
        Carro carro = repositorioCarros.buscarPorPlaca(placa).orElseThrow();
        carro.setEstado(EstadoCarro.valueOf(estado));
    }

    @Dado("un cliente con documento {string}, nombre {string}, edad {int}, licencia vigente, {int} años de licencia y sin deudas")
    public void crearCliente(String doc, String nombre, int edad, int aniosLicencia) {
        this.cliente = new Cliente(doc, nombre, edad, true, aniosLicencia, false);
    }

    @Cuando("el cliente solicita crear una reserva para la placa {string} desde {string} hasta {string}")
    public void crearReserva(String placa, String fechaInicio, String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        this.reservaCreada = servicioReserva.crearReserva(cliente, placa, inicio, fin);
    }

    @Cuando("el cliente intenta reservar el carro con placa {string} desde {string} hasta {string}")
    public void intentarCrearReservaConError(String placa, String fechaInicio, String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        try {
            servicioReserva.crearReserva(cliente, placa, inicio, fin);
        } catch (RentaException e) {
            this.mensajeError = e.getMessage();
        }
    }

    @Entonces("la reserva debe ser generada con éxito")
    public void verificarReservaExitosa() {
        assertNotNull(reservaCreada);
        assertEquals(EstadoReserva.ACTIVA, reservaCreada.getEstado());
    }

    @Entonces("el carro con placa {string} debe quedar en estado {string}")
    public void verificarEstadoCarro(String placa, String estadoEsperado) {
        Carro carro = repositorioCarros.buscarPorPlaca(placa).orElseThrow();
        assertEquals(EstadoCarro.valueOf(estadoEsperado), carro.getEstado());
    }

    @Entonces("la reserva debe ser rechazada con el mensaje {string}")
    public void verificarMensajeError(String mensajeEsperado) {
        assertEquals(mensajeEsperado, mensajeError);
    }
}