# Demo Spring Boot + SonarQube

Proyecto de práctica de la guía **Análisis de Calidad de Código con SonarQube en Proyectos Spring Boot**.
CRUD de `Producto` (Spring Boot 3.3, Java 17, JPA + H2), con pruebas JUnit 5 / Mockito y cobertura JaCoCo.

## Estructura (separación por capas)

```
controller/  -> recibe HTTP, valida y delega (sin lógica de negocio)
service/     -> reglas de negocio y mapeo a DTO
repository/  -> acceso a datos (Spring Data JPA)
model/       -> entidad JPA
dto/         -> ProductoRequest (entrada validada) y ProductoResponse (salida)
exception/   -> excepción de dominio + manejo global de errores
```

## 1. Levantar SonarQube (ejercicio 1)

```bash
docker run -d --name sonarqube -p 9000:9000 -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:community
```

Esperar el mensaje "SonarQube is operational" (`docker logs -f sonarqube`), entrar a http://localhost:9000
(admin / admin, cambia la contraseña) y:

1. **My Account → Security → Generate Tokens** y guarda el token.
2. **Create Project → Manually**: Project key `com.aprendiz:demo-springboot`, nombre `Demo Spring Boot`, rama `main`, método **Locally**.

## 2. Ejecutar la app (ejercicio 2)

```bash
mvn spring-boot:run
```

| Método | Ruta                     | Descripción                          |
|--------|--------------------------|--------------------------------------|
| GET    | /api/productos           | Lista (opcional `?nombre=`)          |
| GET    | /api/productos/{id}      | Obtiene uno                          |
| POST   | /api/productos           | Crea                                 |
| PUT    | /api/productos/{id}      | Actualiza                            |
| DELETE | /api/productos/{id}      | Elimina                              |

Ejemplo:
```bash
curl -X POST http://localhost:8080/api/productos -H "Content-Type: application/json" \
  -d '{"nombre":"Camiseta","descripcion":"Algodón","precio":35000,"stock":10}'
```

## 3. Pruebas y cobertura (ejercicio 4)

```bash
mvn clean verify
```
El reporte de JaCoCo queda en `target/site/jacoco/index.html`.

## 4. Analizar con SonarQube (ejercicio 2)

El token **no** se escribe en ningún archivo; se pasa por variable de entorno.

Windows (PowerShell):
```powershell
$env:SONAR_TOKEN="TU_TOKEN"
mvn clean verify sonar:sonar "-Dsonar.token=$env:SONAR_TOKEN"
```
Linux / macOS:
```bash
export SONAR_TOKEN=TU_TOKEN
mvn clean verify sonar:sonar -Dsonar.token=$SONAR_TOKEN
```
Al final debe aparecer `ANALYSIS SUCCESSFUL` con el enlace al dashboard.

## 5. Ejercicio 3 (code smells y bugs)

En una **copia** del proyecto, copia `ejercicio3-code-smells/ProblemasIntencionales.java` a
`src/main/java/com/aprendiz/demo/ejercicio3/`, vuelve a analizar y en la pestaña **Issues** anota
severidad, tipo y propuesta de corrección de cada problema. Luego corrígelos y analiza otra vez.

## 6. Ejercicio 5 (Quality Gate)

En SonarQube: **Quality Gates → Create** (`QG-Aprendiz-<tu nombre>`), agrega mínimo 4 condiciones
(cobertura en código nuevo ≥ 80 %, duplicación ≤ 3 %, 0 issues Blocker, Security Rating = A) y asócialo al proyecto en la pestaña **Projects**.

## 7. Ejercicio 6 (GitHub Actions)

1. Sube el proyecto a GitHub.
2. En **Settings → Secrets and variables → Actions** crea `SONAR_TOKEN` y `SONAR_HOST_URL`.
3. Haz push a `main` y revisa la pestaña **Actions**.

> Un runner de GitHub **no puede llegar a `http://localhost:9000`** de tu computador. `SONAR_HOST_URL`
> debe apuntar a un SonarQube accesible desde internet o de la red del laboratorio (con un runner
> self-hosted), o usar SonarCloud.

## Nota sobre cobertura

`pom.xml` excluye de la cobertura `model/`, `dto/` y la clase principal (`sonar.coverage.exclusions`)
porque no tienen lógica. La cobertura se mide sobre controller, service y manejo de excepciones.
