package co.edu.calidad.rentacarros.servicio;

import co.edu.calidad.rentacarros.modelo.Carro;
import co.edu.calidad.rentacarros.modelo.Cliente;
import co.edu.calidad.rentacarros.modelo.Tarifa;
import co.edu.calidad.rentacarros.modelo.TipoCarro;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Precio de una reserva en pesos. Las reglas quedan en un solo método
 * para poder medir su complejidad en el taller.
 */
public class CalculadoraTarifa {

    static final long SEGURO_LUJO_POR_DIA = 25_000L;
    static final long SEGURO_SUV_POR_DIA = 12_000L;
    static final long RECARGO_UN_DIA_LUJO = 40_000L;

    public Tarifa calcular(Carro carro, Cliente cliente, LocalDate inicio, LocalDate fin) {
        if (carro == null || cliente == null) {
            throw new IllegalArgumentException("Carro y cliente son obligatorios");
        }
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new IllegalArgumentException("El rango de fechas es inválido");
        }

        long dias = ChronoUnit.DAYS.between(inicio, fin);
        long total = carro.getTarifaDiaria() * dias;

        if (dias >= 30) {
            total = aplicarPorcentaje(total, 75);
        } else if (dias >= 15) {
            total = aplicarPorcentaje(total, 85);
        } else if (dias >= 7) {
            total = aplicarPorcentaje(total, 90);
        }

        if (cliente.getEdad() < 25) {
            total = aplicarPorcentaje(total, 120);
        } else if (cliente.getEdad() >= 70) {
            total = aplicarPorcentaje(total, 110);
        }

        if (carro.getTipo() == TipoCarro.LUJO) {
            total += dias * SEGURO_LUJO_POR_DIA;
        } else if (carro.getTipo() == TipoCarro.SUV) {
            total += dias * SEGURO_SUV_POR_DIA;
        }

        if (incluyeFinDeSemana(inicio, fin)) {
            total = aplicarPorcentaje(total, 108);
        }

        if (carro.getKilometraje() > 150_000) {
            total = aplicarPorcentaje(total, 92);
        } else if (carro.getKilometraje() < 5_000) {
            total = aplicarPorcentaje(total, 112);
        }

        if (cliente.getAniosConLicencia() >= 10 && dias >= 7) {
            total = aplicarPorcentaje(total, 95);
        }

        if (dias == 1 && carro.getTipo() == TipoCarro.LUJO) {
            total += RECARGO_UN_DIA_LUJO;
        }

        return new Tarifa(dias, total);
    }

    public boolean incluyeFinDeSemana(LocalDate inicio, LocalDate fin) {
        LocalDate dia = inicio;
        while (dia.isBefore(fin)) {
            DayOfWeek diaSemana = dia.getDayOfWeek();
            if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
                return true;
            }
            dia = dia.plusDays(1);
        }
        return false;
    }

    private long aplicarPorcentaje(long valor, int porcentaje) {
        return (valor * porcentaje) / 100;
    }
}
