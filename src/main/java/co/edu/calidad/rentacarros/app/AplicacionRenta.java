package co.edu.calidad.rentacarros.app;

import co.edu.calidad.rentacarros.modelo.Carro;
import co.edu.calidad.rentacarros.modelo.Cliente;
import co.edu.calidad.rentacarros.modelo.Reserva;
import co.edu.calidad.rentacarros.modelo.TipoCarro;
import co.edu.calidad.rentacarros.repositorio.RepositorioCarros;
import co.edu.calidad.rentacarros.repositorio.RepositorioReservas;
import co.edu.calidad.rentacarros.servicio.CalculadoraTarifa;
import co.edu.calidad.rentacarros.servicio.ServicioReserva;

import java.time.LocalDate;

public final class AplicacionRenta {

    private AplicacionRenta() {
    }

    public static void main(String[] args) {
        ServicioReserva servicio = new ServicioReserva(
                new RepositorioCarros(),
                new RepositorioReservas(),
                new CalculadoraTarifa());

        Carro logan = new Carro("ABC123", "Renault", "Logan", 2022, TipoCarro.ECONOMICO, 100_000L, 40_000);
        servicio.registrarCarro(logan);

        Cliente ana = new Cliente("123456", "Ana Pérez", 30, true, 5, false);
        Reserva reserva = servicio.crearReserva(
                ana,
                logan.getPlaca(),
                LocalDate.of(2026, 10, 19),
                LocalDate.of(2026, 10, 22));

        System.out.println("Reserva " + reserva.getId()
                + " de " + reserva.getCliente().getNombre()
                + ": " + reserva.getDias() + " días, "
                + reserva.getTotal() + " COP");
        System.out.println("Disponibles ahora: " + servicio.listarDisponibles().size());
    }
}
