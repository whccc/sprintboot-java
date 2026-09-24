# ---------- Etapa 1: build ----------
# Multi-stage build: esta primera etapa SOLO existe para compilar -- la
# imagen final (etapa 2) no va a tener Maven, ni el codigo fuente, ni
# ninguna dependencia de compilacion. Es la misma idea que separar
# feature/domain de infrastructure: la imagen de PRODUCCION no necesita
# saber COMO se construyo, solo necesita el resultado (el .jar).
#
# eclipse-temurin: mismo build de OpenJDK que usas en tu maquina (la
# distribucion mas usada para contenedores Java -- no es "otro Java", es
# el mismo binario). Ajusta el tag "25" si al tener Docker instalado no
# existe todavia esa version exacta en Docker Hub.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copiamos el wrapper de Maven ANTES que el codigo fuente, a proposito:
# Docker cachea cada capa (cada instruccion COPY/RUN). Si despues cambias
# un .java pero el pom.xml no cambio, esta capa (descargar dependencias)
# se reusa de cache -- el build siguiente no vuelve a bajar medio
# internet, solo recompila. Si copiaramos TODO junto, cualquier cambio de
# codigo invalidaria tambien el cache de dependencias.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Windows no trackea el bit +x de Unix -- el archivo puede llegar sin
# permiso de ejecucion aunque en tu maquina "funcione" (ahi lo invocas
# como "./mvnw" via Git Bash, que si lo respeta). Sin este chmod, el
# RUN de abajo tira "Permission denied" dentro del contenedor Linux.
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Recien ACA copiamos el codigo -- esta capa SI se invalida en cada
# cambio, pero ya no vuelve a descargar dependencias.
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ---------- Etapa 2: runtime ----------
# "-jre" (no "-jdk"): en produccion no hace falta compilador, solo la
# maquina virtual para EJECUTAR el .jar ya compilado. Imagen mas chica,
# menos superficie de ataque (menos herramientas que un atacante podria
# usar si llega a entrar al contenedor).
FROM eclipse-temurin:25-jre
WORKDIR /app

# Correr como root dentro de un contenedor es la misma mala practica que
# correr como Administrador en Windows -- si alguien compromete la app,
# compromete lo que sea que ese usuario pueda tocar. Usuario dedicado, sin
# privilegios de mas.
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /app/target/crudpruebas-*.jar app.jar

EXPOSE 8080

# El JAR ya tiene Tomcat embebido (spring-boot-starter-web) -- no hace
# falta instalar ningun servidor aparte, "java -jar" alcanza.
ENTRYPOINT ["java", "-jar", "app.jar"]
