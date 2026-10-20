package co.edu.calidad.rentacarros.repositorio;

import co.edu.calidad.rentacarros.modelo.Reserva;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioReservas {

    private final Map<Long, Reserva> reservas = new LinkedHashMap<>();

    public void guardar(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
    }

    public Optional<Reserva> buscarPorId(long id) {
        return Optional.ofNullable(reservas.get(id));
    }

    public List<Reserva> listar() {
        return List.copyOf(reservas.values());
    }
}
