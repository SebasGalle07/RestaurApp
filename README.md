# RestaurApp

## Variables de entorno

Configura estas variables antes de ejecutar o desplegar:

- `SPRING_DATASOURCE_URL` (por ejemplo, `jdbc:postgresql://<host>:<puerto>/<db>?sslmode=require`)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET` (genera una cadena aleatoria segura)
- `APP_CORS_ALLOWED_ORIGINS` (lista separada por comas con los origenes permitidos)

Opcionales:

- `SPRING_DATASOURCE_MAX_POOL_SIZE` (default `5`)
- `SPRING_JPA_HIBERNATE_DDL_AUTO` (default `validate`)
- `APP_JWT_ACCESS_TTL_SEC`, `APP_JWT_REFRESH_TTL_SEC`
- `SERVER_PORT` / `PORT`

## Comandos locales

```bash
./gradlew bootRun         # levantar servicio
./gradlew test            # ejecutar pruebas
./gradlew bootJar         # generar build/libs/restaurapp.jar
```

## Despliegue en Render (free tier recomendado)

1. Desde https://dashboard.render.com crea un **Blueprint** nuevo y selecciona este repositorio (plan `Free`).
2. Render detectara `render.yaml` y mostrara el servicio `restaurapp-backend`:
   - Tipo: `Web Service`
   - Entorno: `Docker` (usa el `Dockerfile` incluido)
   - Healthcheck: `/actuator/health`
3. Define las variables de entorno requeridas cuando te las pida Render:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `APP_JWT_SECRET`
   - `APP_CORS_ALLOWED_ORIGINS`
4. Haz clic en **Apply** para lanzar el despliegue. El build ejecuta Gradle dentro del contenedor y publica `restaurapp.jar`.
5. Una vez en estado *Live*, prueba `https://<tu-subdominio>.onrender.com/actuator/health` y los endpoints `/api/...`. Si el servicio entra en reposo por inactividad, la primera peticion puede tardar ~1 minuto en reactivarlo.

## Despliegue en Elastic Beanstalk (opcional)

1. Instala y configura AWS CLI (`aws configure`) y EB CLI (`pipx install awsebcli` o `pip install --user awsebcli`).
2. Ejecuta `eb init` en este directorio y elige la plataforma **Java 21 (Corretto)**.
3. Crea el entorno una sola vez:
   ```bash
   eb create restaurapp-env --single --instance_type t3.micro
   ```
4. Carga variables:
   ```bash
   eb setenv SPRING_DATASOURCE_URL="jdbc:postgresql://<host>:<puerto>/<db>?sslmode=require" ^
            SPRING_DATASOURCE_USERNAME="<usuario>" ^
            SPRING_DATASOURCE_PASSWORD="<clave>" ^
            APP_JWT_SECRET="<secreto_fuerte>" ^
            APP_CORS_ALLOWED_ORIGINS="http://localhost:4200"
   ```
   (en bash usa `\` como continuacion de linea).
5. Construye y despliega:
   ```bash
   ./gradlew bootJar
   eb deploy
   ```
6. Abre la URL con `eb open` y valida `/actuator/health`.
