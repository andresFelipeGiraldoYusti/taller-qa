package co.edu.calidad.rentacarros.servicio;

import co.edu.calidad.rentacarros.modelo.Carro;
import co.edu.calidad.rentacarros.modelo.Cliente;
import co.edu.calidad.rentacarros.modelo.EstadoCarro;
import co.edu.calidad.rentacarros.modelo.EstadoReserva;
import co.edu.calidad.rentacarros.modelo.Reserva;
import co.edu.calidad.rentacarros.modelo.ResultadoElegibilidad;
import co.edu.calidad.rentacarros.modelo.TipoCarro;
import co.edu.calidad.rentacarros.repositorio.RepositorioCarros;
import co.edu.calidad.rentacarros.repositorio.RepositorioReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioReservaTest {

    private static final LocalDate LUNES = LocalDate.of(2026, 10, 19);

    private RepositorioCarros carros;
    private RepositorioReservas reservas;
    private ServicioReserva servicio;

    @BeforeEach
    void preparar() {
        carros = new RepositorioCarros();
        reservas = new RepositorioReservas();
        servicio = new ServicioReserva(carros, reservas, new CalculadoraTarifa());
    }

    @Test
    void creaUnaReservaYMarcaElCarroComoRentado() {
        registrar("ABC123", TipoCarro.ECONOMICO, EstadoCarro.DISPONIBLE);
        Cliente cliente = cliente(30, true, 5, false);

        Reserva reserva = servicio.crearReserva(cliente, "ABC123", LUNES, LUNES.plusDays(3));

        assertEquals(1L, reserva.getId());
        assertEquals(cliente, reserva.getCliente());
        assertEquals("ABC123", reserva.getCarro().getPlaca());
        assertEquals(LUNES, reserva.getInicio());
        assertEquals(LUNES.plusDays(3), reserva.getFin());
        assertEquals(3, reserva.getDias());
        assertEquals(300_000L, reserva.getTotal());
        assertEquals(EstadoReserva.ACTIVA, reserva.getEstado());
        assertEquals(EstadoCarro.RENTADO, reserva.getCarro().getEstado());
        assertTrue(servicio.listarDisponibles().isEmpty());
        assertEquals(1, reservas.listar().size());
    }

    @Test
    void noReservaCuandoElClienteNoEsAptoYElCarroSigueDisponible() {
        registrar("ABC123", TipoCarro.ECONOMICO, EstadoCarro.DISPONIBLE);

        RentaException error = assertThrows(RentaException.class,
                () -> servicio.crearReserva(cliente(17, true, 0, false), "ABC123", LUNES, LUNES.plusDays(3)));

        assertEquals("El cliente debe ser mayor de edad", error.getMessage());
        assertEquals(EstadoCarro.DISPONIBLE, carros.buscarPorPlaca("ABC123").orElseThrow().getEstado());
        assertEquals(1, servicio.listarDisponibles().size());
        assertTrue(reservas.listar().isEmpty());
    }

    @Test
    void rechazaUnaPlacaQueNoExiste() {
        RentaException error = assertThrows(RentaException.class,
                () -> servicio.crearReserva(cliente(30, true, 5, false), "NOEXISTE", LUNES, LUNES.plusDays(2)));

        assertEquals("No existe un carro con placa NOEXISTE", error.getMessage());
    }

    @Test
    void cierraLaReservaYLiberaElCarro() {
        registrar("ABC123", TipoCarro.ECONOMICO, EstadoCarro.DISPONIBLE);
        Reserva reserva = servicio.crearReserva(cliente(30, true, 5, false), "ABC123", LUNES, LUNES.plusDays(3));

        servicio.cerrarReserva(reserva.getId());

        assertEquals(EstadoReserva.CERRADA, reserva.getEstado());
        assertEquals(EstadoCarro.DISPONIBLE, reserva.getCarro().getEstado());
        assertEquals(1, servicio.listarDisponibles().size());
        RentaException error = assertThrows(RentaException.class, () -> servicio.cerrarReserva(reserva.getId()));
        assertEquals("La reserva 1 no está activa", error.getMessage());
    }

    @Test
    void cancelaLaReservaYNoPermiteCerrarlaDespues() {
        registrar("ABC123", TipoCarro.ECONOMICO, EstadoCarro.DISPONIBLE);
        Reserva reserva = servicio.crearReserva(cliente(30, true, 5, false), "ABC123", LUNES, LUNES.plusDays(3));

        servicio.cancelarReserva(reserva.getId());

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertEquals(EstadoCarro.DISPONIBLE, reserva.getCarro().getEstado());
        assertThrows(RentaException.class, () -> servicio.cerrarReserva(reserva.getId()));
    }

    @Test
    void rechazaCerrarUnaReservaInexistente() {
        RentaException error = assertThrows(RentaException.class, () -> servicio.cerrarReserva(99));

        assertEquals("No existe la reserva 99", error.getMessage());
    }

    @Test
    void evaluaLasReglasDeElegibilidad() {
        Carro economico = carro("ABC123", TipoCarro.ECONOMICO, EstadoCarro.DISPONIBLE);
        Carro lujo = carro("LUX100", TipoCarro.LUJO, EstadoCarro.DISPONIBLE);
        Carro suv = carro("SUV200", TipoCarro.SUV, EstadoCarro.DISPONIBLE);
        Carro enTaller = carro("TALL01", TipoCarro.ECONOMICO, EstadoCarro.MANTENIMIENTO);
        Carro rentado = carro("REN001", TipoCarro.ECONOMICO, EstadoCarro.RENTADO);

        assertRechaza(null, economico, "Datos incompletos");
        assertRechaza(cliente(30, true, 5, false), null, "Datos incompletos");
        assertRechaza(cliente(30, true, 5, false), economico, null, LUNES.plusDays(2), "Fechas inválidas");
        assertRechaza(cliente(30, true, 5, false), economico, LUNES, null, "Fechas inválidas");
        assertRechaza(cliente(30, true, 5, false), economico, LUNES, LUNES, "Fechas inválidas");
        assertRechaza(cliente(17, true, 0, false), economico, "El cliente debe ser mayor de edad");
        assertRechaza(cliente(30, false, 5, false), economico, "La licencia no está vigente");
        assertRechaza(cliente(30, true, 1, true), economico, "Un cliente con deudas necesita al menos 3 años de licencia");
        assertRechaza(cliente(40, true, 5, false), enTaller, "El carro está en mantenimiento");
        assertRechaza(cliente(40, true, 5, false), rentado, "El carro no está disponible");
        assertRechaza(cliente(24, true, 5, false), lujo, "Un carro de lujo exige mínimo 25 años");
        assertRechaza(cliente(30, true, 1, false), lujo, "Un carro de lujo exige mínimo 2 años de licencia");
        assertRechaza(cliente(20, true, 2, false), suv, "Una SUV exige mínimo 21 años");
        assertRechaza(cliente(30, true, 0, false), economico, LUNES, LUNES.plusDays(31),
                "Una renta de más de 30 días en carro económico exige al menos 1 año de licencia");

        ResultadoElegibilidad conDeudasYExperiencia = servicio.evaluarElegibilidad(
                cliente(30, true, 5, true), economico, LUNES, LUNES.plusDays(3));
        ResultadoElegibilidad suvApta = servicio.evaluarElegibilidad(
                cliente(21, true, 2, false), suv, LUNES, LUNES.plusDays(3));
        ResultadoElegibilidad rentaLarga = servicio.evaluarElegibilidad(
                cliente(30, true, 1, false), economico, LUNES, LUNES.plusDays(31));
        ResultadoElegibilidad suvLarga = servicio.evaluarElegibilidad(
                cliente(21, true, 0, false), suv, LUNES, LUNES.plusDays(31));

        assertTrue(conDeudasYExperiencia.isAceptada());
        assertEquals("Cliente apto para rentar", conDeudasYExperiencia.getMotivo());
        assertEquals(3, conDeudasYExperiencia.getDias());
        assertTrue(suvApta.isAceptada());
        assertTrue(rentaLarga.isAceptada());
        assertEquals(31, rentaLarga.getDias());
        assertTrue(suvLarga.isAceptada());
        assertFalse(servicio.evaluarElegibilidad(cliente(17, true, 0, false), economico, LUNES, LUNES.plusDays(1)).isAceptada());
    }

    private void assertRechaza(Cliente cliente, Carro carro, String motivo) {
        assertRechaza(cliente, carro, LUNES, LUNES.plusDays(3), motivo);
    }

    private void assertRechaza(Cliente cliente, Carro carro, LocalDate inicio, LocalDate fin, String motivo) {
        ResultadoElegibilidad resultado = servicio.evaluarElegibilidad(cliente, carro, inicio, fin);
        assertFalse(resultado.isAceptada());
        assertEquals(motivo, resultado.getMotivo());
        assertEquals(0, resultado.getDias());
    }

    private void registrar(String placa, TipoCarro tipo, EstadoCarro estado) {
        Carro carro = carro(placa, tipo, estado);
        servicio.registrarCarro(carro);
    }

    private Carro carro(String placa, TipoCarro tipo, EstadoCarro estado) {
        Carro carro = new Carro(placa, "Renault", "Logan", 2022, tipo, 100_000L, 40_000);
        carro.setEstado(estado);
        return carro;
    }

    private Cliente cliente(int edad, boolean licenciaVigente, int aniosConLicencia, boolean tieneDeudas) {
        return new Cliente("123", "Ana Pérez", edad, licenciaVigente, aniosConLicencia, tieneDeudas);
    }
}
