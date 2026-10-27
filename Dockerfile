# ---------- Etapa 1: compilación ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B package -DskipTests

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

ENV DATA_DIR=/app/datos

RUN mkdir -p /app/datos

COPY --from=build /build/target/biblioteca.jar /app/biblioteca.jar

# Punto de montaje del volumen: los datos viven FUERA del ciclo de vida del contenedor
VOLUME ["/app/datos"]

ENTRYPOINT ["java", "-Dstdout.encoding=UTF-8", "-Dfile.encoding=UTF-8", "-jar", "/app/biblioteca.jar"]
