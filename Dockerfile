# Etapa 1: build del proyecto
FROM gradle:8.5-jdk17 AS build

# Copiamos todo el proyecto
COPY --chown=gradle:gradle . /home/gradle/project

WORKDIR /home/gradle/project

# Compilamos el proyecto
RUN gradle build --no-daemon -x test

# Etapa 2: imagen final para correr el app
FROM eclipse-temurin:17-jdk

# Instalamos mysql-client para tener mysqldump
RUN apt-get update && apt-get install -y mysql-client

WORKDIR /app

# Copiamos el jar generado desde la etapa de build
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
