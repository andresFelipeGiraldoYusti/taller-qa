# Taller de análisis estático: renta de carros

Sistema en Java para el taller de calidad de software. El dominio es una renta de carros. Las pruebas generan cobertura con JaCoCo y el mismo reporte XML es el que SonarCloud importa.

## Cómo ejecutarlo

Requisitos: JDK 17 y Maven. En este equipo ya quedaron instalados: Temurin 17 y Maven 3.9.11. Si una terminal abierta antes de eso no reconoce `java` o `mvn`, ciérrala y abre otra.

```powershell
mvn clean verify
mvn -q exec:java
```

`mvn clean verify` compila, corre las pruebas y escribe el reporte de JaCoCo. `mvn exec:java` simula una reserva de tres días (300.000 COP).

## Ruta local de la cobertura (JaCoCo)

Después de `mvn clean verify`:

| Qué abrir | Ruta |
| --- | --- |
| Reporte en el navegador | `target/site/jacoco/index.html` |
| XML que lee SonarCloud | `target/site/jacoco/jacoco.xml` |

Ruta completa en este equipo:

`C:\Users\matiu\OneDrive\Documentos\2026-2\CalidadDeSoftwate\Taller QA\target\site\jacoco\index.html`

En PowerShell:

```powershell
start target\site\jacoco\index.html
```

En el `pom.xml` esa ruta XML queda declarada para el escáner:

```xml
<sonar.coverage.jacoco.xmlReportPaths>${project.basedir}/target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
```

El HTML muestra líneas, ramas y, en la columna **Complexity**, la complejidad ciclomática. La complejidad cognitiva no sale aquí: esa la calcula SonarCloud.

- verde: línea ejecutada
- amarillo: la línea se ejecutó, pero alguna rama no
- rojo: línea sin ejecutar

Hay huecos dejados a propósito para verlos en el reporte:

- `CalculadoraTarifa.calcular`: el recargo de un conductor de 70 años o más, y el recargo de un carro con menos de 5.000 km. El `else if` se ve amarillo y el cuerpo de la rama, rojo.
- `ServicioReserva.evaluarElegibilidad`: un cliente con deudas que pide un carro de lujo. La línea del `return` queda roja.
- En ese mismo método, la rama falsa de "lujo exige 2 años de licencia" no se visita: el `if` queda amarillo.
- `AplicacionRenta.main` queda rojo porque no tiene pruebas.

## Subir la cobertura a SonarCloud

1. Entra a [sonarcloud.io](https://sonarcloud.io) y crea la organización (o usa la de la clase).
2. Crea un proyecto manual. La llave suele ser `organizacion_renta-carros`.
3. Genera un token.
4. Reemplaza los valores y ejecuta:

```powershell
$env:SONAR_TOKEN = "el-token"
mvn clean verify sonar:sonar "-Dsonar.projectKey=organizacion_renta-carros" "-Dsonar.organization=organizacion" "-Dsonar.token=$env:SONAR_TOKEN"
```

El `verify` tiene que correr antes del análisis. Si el XML no existe, SonarCloud muestra la cobertura en 0 %.

## Complejidad ciclomática

Cuenta los caminos de decisión de un método. Fórmula práctica: **1 + cada decisión**. Suman `if`, `else if`, `while`, `for`, `case`, `catch`, y también cada `&&` y `||`.

No mira qué tan adentro está el `if`. Diez `if` seguidos, al mismo nivel, suben la ciclomática tanto como diez `if` anidados.

Ejemplo pequeño, `CalculadoraTarifa.incluyeFinDeSemana`:

- 1 por el método
- +1 por el `while`
- +1 por el `if`
- +1 por el `||`

Complejidad ciclomática: **4**. El reporte de JaCoCo de este proyecto ya la muestra así.

En el mismo reporte, después de `mvn clean verify`:

- `CalculadoraTarifa.calcular`: **20**. El precio está en secuencia (duración, edad, tipo, fin de semana, kilometraje, experiencia). Supera el límite usual de **10**.
- `ServicioReserva.evaluarElegibilidad`: **21**.

En SonarCloud el mismo número aparece sobre el método o en **Measures → Complexity**. La regla del perfil es `java:S1541` (*Methods should not be too complex*).

## Complejidad cognitiva

Mide qué tan difícil es **entender** el método. También suma por cada quiebre del flujo, pero **castiga el anidamiento**: un `if` dentro de otro suma más que dos `if` seguidos. Una cadena del mismo operador (`a || b || c`) suma una sola vez, no una por cada `||`.

`ServicioReserva.evaluarElegibilidad` es el método para comparar. Las reglas de deudas, estado del carro, lujo y SUV están anidadas. La cognitiva queda más alta que la de `calcular`, aunque la cantidad de decisiones sea parecida.

La regla del perfil es `java:S3776` (*Cognitive Complexity of methods should not be too high*). El límite por defecto es **15**.

SonarCloud escribe el número oficial. El conteo a mano sirve para explicar la diferencia; el valor que se reporta en el taller es el de Sonar.

## Quality Profile

Es el **conjunto de reglas activas** para un lenguaje. El perfil por defecto de Java se llama **Sonar way**.

Ahí viven, entre otras, `java:S1541` (ciclomática) y `java:S3776` (cognitiva), con sus umbrales. Se puede copiar el perfil y cambiar el umbral, activar o apagar reglas. El perfil no dice si el proyecto aprueba: solo define qué se va a buscar.

En SonarCloud: **Project Settings → Quality Profiles**. El de este proyecto, mientras no se cambie, es Sonar way para Java.

El `main` usa `System.out`. Sonar way lo marca con `java:S106`. Es un hallazgo pequeño, distinto de la complejidad.

## Quality Gate

Es la **condición de aprobado o reprobado** del proyecto. No es una regla suelta: es un conjunto de umbrales sobre las métricas, casi siempre mirando el código nuevo.

El Quality Gate por defecto (**Sonar way**) pide, sobre código nuevo, cosas de este estilo:

- cobertura de al menos 80 %
- duplicados por debajo de 3 %
- rating de confiabilidad, seguridad y mantenibilidad en A
- que los security hotspots estén revisados

El resultado es **Passed** o **Failed**. Se ve en el overview del proyecto.

Dónde encaja cada pieza:

- el **Quality Profile** decide qué issues existen (por ejemplo, un método con cognitiva mayor a 15)
- el **Quality Gate** decide si esos issues, la cobertura y el resto de métricas alcanzan para aprobar

La cobertura que usa el gate sale del XML de JaCoCo. Por eso la ruta local y SonarCloud tienen que apuntar al mismo archivo.

## Dónde está cada tema en este proyecto

- Cobertura: `pom.xml` (plugin JaCoCo y `sonar.coverage.jacoco.xmlReportPaths`) y `src/test/java`
- Ciclomática alta, decisiones en secuencia: `CalculadoraTarifa.calcular`
- Cognitiva alta, decisiones anidadas: `ServicioReserva.evaluarElegibilidad`
- Método simple para comparar: `CalculadoraTarifa.incluyeFinDeSemana`
- Quality Profile y Quality Gate: se consultan en SonarCloud después de `mvn sonar:sonar`. No viven en el código.

## Reglas de negocio

Precio, en este orden, en pesos:

1. Base = tarifa diaria × días
2. 30 días o más: paga el 75 %. De 15 a 29: el 85 %. De 7 a 14: el 90 %
3. Menor de 25 años: +20 %. De 70 o más: +10 %
4. Lujo: +25.000 por día. SUV: +12.000 por día
5. Si el rango incluye sábado o domingo: +8 %
6. Más de 150.000 km: −8 %. Menos de 5.000 km: +12 %
7. 10 años de licencia y renta de 7 días o más: −5 %
8. Lujo de un solo día: +40.000

Elegibilidad:

- Mayor de edad y licencia vigente
- Con deudas no puede llevar lujo; si no es lujo, necesita al menos 3 años de licencia
- El carro tiene que estar disponible
- Lujo: mínimo 25 años y 2 años de licencia
- SUV: mínimo 21 años
- Más de 30 días en carro económico: al menos 1 año de licencia
