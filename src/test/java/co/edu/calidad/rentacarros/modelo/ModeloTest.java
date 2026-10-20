package co.edu.calidad.rentacarros.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModeloTest {

    @Test
    void guardaLosDatosDelCarroYDelCliente() {
        Carro carro = new Carro("ABC123", "Renault", "Logan", 2022, TipoCarro.ECONOMICO, 100_000L, 40_000);
        Cliente cliente = new Cliente("123", "Ana Pérez", 30, true, 5, false);

        assertEquals("ABC123", carro.getPlaca());
        assertEquals("Renault", carro.getMarca());
        assertEquals("Logan", carro.getModelo());
        assertEquals(2022, carro.getAnio());
        assertEquals(TipoCarro.ECONOMICO, carro.getTipo());
        assertEquals(100_000L, carro.getTarifaDiaria());
        assertEquals(40_000, carro.getKilometraje());
        assertEquals(EstadoCarro.DISPONIBLE, carro.getEstado());
        assertEquals("123", cliente.getDocumento());
        assertEquals("Ana Pérez", cliente.getNombre());
        assertEquals(30, cliente.getEdad());
        assertEquals(true, cliente.isLicenciaVigente());
        assertEquals(5, cliente.getAniosConLicencia());
        assertEquals(false, cliente.isTieneDeudas());
    }

    @Test
    void rechazaDatosObligatoriosInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Carro("  ", "Renault", "Logan", 2022, TipoCarro.ECONOMICO, 100_000L, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Carro("ABC123", " ", "Logan", 2022, TipoCarro.ECONOMICO, 100_000L, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Carro("ABC123", "Renault", " ", 2022, TipoCarro.ECONOMICO, 100_000L, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Carro("ABC123", "Renault", "Logan", 2022, null, 100_000L, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Carro("ABC123", "Renault", "Logan", 2022, TipoCarro.ECONOMICO, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Carro("ABC123", "Renault", "Logan", 2022, TipoCarro.ECONOMICO, 100_000L, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente(" ", "Ana", 30, true, 1, false));
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente("123", " ", 30, true, 1, false));
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente("123", "Ana", -1, true, 1, false));
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente("123", "Ana", 30, true, -1, false));
    }
}
