package co.edu.calidad.rentacarros.repositorio;

import co.edu.calidad.rentacarros.modelo.Carro;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioCarros {

    private final Map<String, Carro> carros = new LinkedHashMap<>();

    public void guardar(Carro carro) {
        carros.put(carro.getPlaca(), carro);
    }

    public Optional<Carro> buscarPorPlaca(String placa) {
        return Optional.ofNullable(carros.get(placa));
    }

    public List<Carro> listar() {
        return List.copyOf(carros.values());
    }
}
