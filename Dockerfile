# Imagen base con Java 17 y apt para instalar paquetes
FROM eclipse-temurin:17-jdk

# Instalamos el cliente de MySQL para tener acceso a mysqldump
RUN apt-get update && apt-get install -y mysql-client

# Creamos el directorio de trabajo
WORKDIR /app

# Copiamos el JAR generado (ajustalo si tu JAR tiene otro nombre)
COPY build/libs/prueba_backups-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto por si querés testear local
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]