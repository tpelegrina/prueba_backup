package com.example.prueba_backups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class BackupService {
    @Autowired
    private BackupRepository backupRepository;

    private final BackupProperties backupProperties;

    @Autowired
    public BackupService(BackupProperties backupProperties) {
        this.backupProperties = backupProperties;
    }

    public String generarNombreBackup() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "Backup-" + timestamp + ".sql";
    }

    public void testCrearArchivoDummy() {
        String ruta = backupProperties.getDirectory();

        // Crear el directorio si no existe
        File directorio = new File(ruta);
        if (!directorio.exists()) {
            boolean creada = directorio.mkdirs();
            if (!creada) {
                System.err.println("❌ No se pudo crear el directorio de backups.");
                return;
            }
        }

        // Crear un archivo dummy de prueba
        String nombreArchivo = generarNombreBackup();
        backupRepository.save(new Backup(nombreArchivo));
        File archivo = new File(ruta + "/" + nombreArchivo);


        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write("Este es un archivo de prueba de backup.\n");
            writer.write("Generado el: " + LocalDateTime.now());
            System.out.println("✅ Archivo de prueba creado en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Error al crear el archivo de prueba: " + e.getMessage());
        }
    }

    public void generarDump() {
        String nombreArchivo = generarNombreBackup();
        String rutaCompleta = backupProperties.getDirectory() + "/" + nombreArchivo;

        // Configurar tus credenciales de conexión
        String dbHost = "localhost"; // o tu host real
        String dbPort = "3306";
        String dbName = "turisgo_db";
        String dbUser = "root";
        String dbPassword = "lorax28526787";

        String mysqldumpPath = "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump.exe"; // ajustá la ruta

        List<String> comando = Arrays.asList(
                mysqldumpPath,
                "-h", dbHost,
                "-P", dbPort,
                "-u", dbUser,
                "-p" + dbPassword,
                dbName
        );

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectOutput(new File(rutaCompleta));

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ Backup generado en: " + rutaCompleta);
                backupRepository.save(new Backup(nombreArchivo));
            } else {
                System.err.println("❌ Error al generar backup. Código de salida: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Excepción al ejecutar mysqldump: " + e.getMessage());
        }
    }

    //@Scheduled(cron = "0 0 3 * * *") // Todos los días a las 03:00 AM
    @Scheduled(fixedRate = 30000) // cada 30 segundos
    public void generarBackupAutomatico() {
        System.out.println("🕒 Ejecutando backup programado...");
        generarDump();
    }
}