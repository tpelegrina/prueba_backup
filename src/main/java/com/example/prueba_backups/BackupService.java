package com.example.prueba_backups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
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

    public void generarDump() {
        String nombreArchivo = generarNombreBackup();
        String rutaCompleta = backupProperties.getDirectory() + "/" + nombreArchivo;

        // Configurar tus credenciales de conexión
        String dbHost = System.getenv("MYSQLHOST");
        String dbPort = System.getenv("MYSQLPORT");
        String dbUser = System.getenv("MYSQLUSER");
        String dbPassword = System.getenv("MYSQLPASSWORD");
        String dbName = System.getenv("MYSQL_DATABASE");



        List<String> comando = Arrays.asList(
                "mysqldump",
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
    //@Scheduled(fixedRate = 30000) // cada 30 segundos
    @Scheduled(cron = "0 22 20 * * *")
    public void generarBackupAutomatico() {
        System.out.println("🕒 Ejecutando backup programado...");
        generarDump();
    }
}