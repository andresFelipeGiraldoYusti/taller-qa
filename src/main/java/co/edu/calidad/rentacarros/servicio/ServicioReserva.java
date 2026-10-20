package co.edu.calidad.rentacarros.servicio;

import co.edu.calidad.rentacarros.modelo.Carro;
import co.edu.calidad.rentacarros.modelo.Cliente;
import co.edu.calidad.rentacarros.modelo.EstadoCarro;
import co.edu.calidad.rentacarros.modelo.EstadoReserva;
import co.edu.calidad.rentacarros.modelo.Reserva;
import co.edu.calidad.rentacarros.modelo.ResultadoElegibilidad;
import co.edu.calidad.rentacarros.modelo.Tarifa;
import co.edu.calidad.rentacarros.modelo.TipoCarro;
import co.edu.calidad.rentacarros.repositorio.RepositorioCarros;
import co.edu.calidad.rentacarros.repositorio.RepositorioReservas;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Reglas para aceptar, crear, cerrar y cancelar una reserva.
 * La elegibilidad concentra decisiones anidadas a propósito del taller.
 */
public class ServicioReserva {

    private final RepositorioCarros carros;
    private final RepositorioReservas reservas;
    private final CalculadoraTarifa calculadora;
    private long siguienteId = 1L;

    public ServicioReserva(RepositorioCarros carros, RepositorioReservas reservas, CalculadoraTarifa calculadora) {
        this.carros = carros;
        this.reservas = reservas;
        this.calculadora = calculadora;
    }

    public void registrarCarro(Carro carro) {
        carros.guardar(carro);
    }

    public List<Carro> listarDisponibles() {
        return carros.listar().stream()
                .filter(carro -> carro.getEstado() == EstadoCarro.DISPONIBLE)
                .toList();
    }

    public ResultadoElegibilidad evaluarElegibilidad(Cliente cliente, Carro carro, LocalDate inicio, LocalDate fin) {
        if (cliente == null || carro == null) {
            return ResultadoElegibilidad.rechazada("Datos incompletos");
        }
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            return ResultadoElegibilidad.rechazada("Fechas inválidas");
        }
        if (cliente.getEdad() < 18) {
            return ResultadoElegibilidad.rechazada("El cliente debe ser mayor de edad");
        }
        if (!cliente.isLicenciaVigente()) {
            return ResultadoElegibilidad.rechazada("La licencia no está vigente");
        }
        if (cliente.isTieneDeudas()) {
            if (carro.getTipo() == TipoCarro.LUJO) {
                return ResultadoElegibilidad.rechazada("Un cliente con deudas no puede rentar un carro de lujo");
            } else if (cliente.getAniosConLicencia() < 3) {
                return ResultadoElegibilidad.rechazada("Un cliente con deudas necesita al menos 3 años de licencia");
            }
        }
        if (carro.getEstado() != EstadoCarro.DISPONIBLE) {
            if (carro.getEstado() == EstadoCarro.MANTENIMIENTO) {
                return ResultadoElegibilidad.rechazada("El carro está en mantenimiento");
            }
            return ResultadoElegibilidad.rechazada("El carro no está disponible");
        }
        if (carro.getTipo() == TipoCarro.LUJO) {
            if (cliente.getEdad() < 25) {
                return ResultadoElegibilidad.rechazada("Un carro de lujo exige mínimo 25 años");
            }
            if (cliente.getAniosConLicencia() < 2) {
                return ResultadoElegibilidad.rechazada("Un carro de lujo exige mínimo 2 años de licencia");
            }
        } else if (carro.getTipo() == TipoCarro.SUV) {
            if (cliente.getEdad() < 21) {
                return ResultadoElegibilidad.rechazada("Una SUV exige mínimo 21 años");
            }
        }
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        if (dias > 30) {
            if (carro.getTipo() == TipoCarro.ECONOMICO && cliente.getAniosConLicencia() < 1) {
                return ResultadoElegibilidad.rechazada(
                        "Una renta de más de 30 días en carro económico exige al menos 1 año de licencia");
            }
        }
        return ResultadoElegibilidad.aceptada(dias);
    }

    public Reserva crearReserva(Cliente cliente, String placa, LocalDate inicio, LocalDate fin) {
        Carro carro = carros.buscarPorPlaca(placa)
                .orElseThrow(() -> new RentaException("No existe un carro con placa " + placa));
        ResultadoElegibilidad elegibilidad = evaluarElegibilidad(cliente, carro, inicio, fin);
        if (!elegibilidad.isAceptada()) {
            throw new RentaException(elegibilidad.getMotivo());
        }
        Tarifa tarifa = calculadora.calcular(carro, cliente, inicio, fin);
        Reserva reserva = new Reserva(siguienteId++, cliente, carro, inicio, fin, tarifa.getDias(), tarifa.getTotal());
        carro.setEstado(EstadoCarro.RENTADO);
        reservas.guardar(reserva);
        return reserva;
    }

    public Reserva cerrarReserva(long id) {
        Reserva reserva = buscarActiva(id);
        reserva.setEstado(EstadoReserva.CERRADA);
        reserva.getCarro().setEstado(EstadoCarro.DISPONIBLE);
        return reserva;
    }

    public Reserva cancelarReserva(long id) {
        Reserva reserva = buscarActiva(id);
        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.getCarro().setEstado(EstadoCarro.DISPONIBLE);
        return reserva;
    }

    private Reserva buscarActiva(long id) {
        Reserva reserva = reservas.buscarPorId(id)
                .orElseThrow(() -> new RentaException("No existe la reserva " + id));
        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new RentaException("La reserva " + id + " no está activa");
        }
        return reserva;
    }
}
