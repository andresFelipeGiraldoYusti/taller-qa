package co.edu.calidad.rentacarros.servicio;

import co.edu.calidad.rentacarros.modelo.Carro;
import co.edu.calidad.rentacarros.modelo.Cliente;
import co.edu.calidad.rentacarros.modelo.Tarifa;
import co.edu.calidad.rentacarros.modelo.TipoCarro;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculadoraTarifaTest {

    private static final LocalDate LUNES = LocalDate.of(2026, 10, 19);

    private final CalculadoraTarifa calculadora = new CalculadoraTarifa();

    @Test
    void cobraLaTarifaBaseEnTresDiasDeSemana() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(3));

        assertEquals(3, tarifa.getDias());
        assertEquals(300_000L, tarifa.getTotal());
        assertFalse(calculadora.incluyeFinDeSemana(LUNES, LUNES.plusDays(3)));
    }

    @Test
    void recargaElFinDeSemana() {
        LocalDate viernes = LocalDate.of(2026, 10, 23);

        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 5), viernes, viernes.plusDays(3));

        assertEquals(324_000L, tarifa.getTotal());
        assertTrue(calculadora.incluyeFinDeSemana(viernes, viernes.plusDays(3)));
    }

    @Test
    void aplicaDescuentoDeSieteDiasYRecargoDeFinDeSemana() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(7));

        assertEquals(680_400L, tarifa.getTotal());
    }

    @Test
    void aplicaDescuentoDeQuinceDias() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(15));

        assertEquals(1_377_000L, tarifa.getTotal());
    }

    @Test
    void aplicaDescuentoDeTreintaDias() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(30));

        assertEquals(2_430_000L, tarifa.getTotal());
    }

    @Test
    void recargaAlConductorMenorDe25() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(22, 2), LUNES, LUNES.plusDays(3));

        assertEquals(360_000L, tarifa.getTotal());
    }

    @Test
    void sumaElSeguroDeLujo() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.LUJO, 200_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(3));

        assertEquals(675_000L, tarifa.getTotal());
    }

    @Test
    void sumaElSeguroDeSuv() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.SUV, 150_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(3));

        assertEquals(486_000L, tarifa.getTotal());
    }

    @Test
    void descuentaElKilometrajeAlto() {
        Tarifa tarifa = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 160_000), cliente(30, 5), LUNES, LUNES.plusDays(3));

        assertEquals(276_000L, tarifa.getTotal());
    }

    @Test
    void descuentaAlConductorConDiezAniosSiLaRentaDuraAlMenosSieteDias() {
        Tarifa conDescuento = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 10), LUNES, LUNES.plusDays(7));
        Tarifa sinDescuento = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 10), LUNES, LUNES.plusDays(3));

        assertEquals(646_380L, conDescuento.getTotal());
        assertEquals(300_000L, sinDescuento.getTotal());
    }

    @Test
    void cobraRecargoCuandoElLujoEsDeUnSoloDia() {
        Tarifa lujo = calculadora.calcular(carro(TipoCarro.LUJO, 200_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(1));
        Tarifa economico = calculadora.calcular(carro(TipoCarro.ECONOMICO, 100_000L, 40_000), cliente(30, 5), LUNES, LUNES.plusDays(1));

        assertEquals(265_000L, lujo.getTotal());
        assertEquals(100_000L, economico.getTotal());
    }

    @Test
    void rechazaDatosIncompletos() {
        Carro carro = carro(TipoCarro.ECONOMICO, 100_000L, 40_000);
        Cliente cliente = cliente(30, 5);

        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(null, cliente, LUNES, LUNES.plusDays(3)));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(carro, null, LUNES, LUNES.plusDays(3)));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(carro, cliente, null, LUNES.plusDays(3)));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(carro, cliente, LUNES, null));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(carro, cliente, LUNES, LUNES));
    }

    private Carro carro(TipoCarro tipo, long tarifaDiaria, int kilometraje) {
        return new Carro("ABC123", "Renault", "Logan", 2022, tipo, tarifaDiaria, kilometraje);
    }

    private Cliente cliente(int edad, int aniosConLicencia) {
        return new Cliente("123", "Ana Pérez", edad, true, aniosConLicencia, false);
    }
}
