package co.edu.calidad.rentacarros.modelo;

public class Cliente {

    private final String documento;
    private final String nombre;
    private final int edad;
    private final boolean licenciaVigente;
    private final int aniosConLicencia;
    private final boolean tieneDeudas;

    public Cliente(String documento, String nombre, int edad, boolean licenciaVigente, int aniosConLicencia, boolean tieneDeudas) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        if (aniosConLicencia < 0) {
            throw new IllegalArgumentException("Los años de licencia no pueden ser negativos");
        }
        this.documento = documento;
        this.nombre = nombre;
        this.edad = edad;
        this.licenciaVigente = licenciaVigente;
        this.aniosConLicencia = aniosConLicencia;
        this.tieneDeudas = tieneDeudas;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public boolean isLicenciaVigente() {
        return licenciaVigente;
    }

    public int getAniosConLicencia() {
        return aniosConLicencia;
    }

    public boolean isTieneDeudas() {
        return tieneDeudas;
    }
}
