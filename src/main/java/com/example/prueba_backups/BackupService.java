package com.example.prueba_backups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<String> listarArchivosBackup() {
        File carpeta = new File(backupProperties.getDirectory());

        if (!carpeta.exists() || !carpeta.isDirectory()) return List.of();

        String[] archivos = carpeta.list((dir, name) -> name.toLowerCase().endsWith(".sql"));
        if (archivos == null) return List.of();

        // Ordenar por fecha (alfabéticamente ya funciona con la convención Backup-YYYY-MM-DD...)
        return Arrays.stream(archivos)
                .sorted(Comparator.reverseOrder()) // más reciente primero
                .collect(Collectors.toList());
    }

    public String restaurarBackup(String nombreArchivo) {
        String ruta = backupProperties.getDirectory() + "/" + nombreArchivo;

        String dbHost = System.getenv("MYSQLHOST");
        String dbPort = System.getenv("MYSQLPORT");
        String dbUser = System.getenv("MYSQLUSER");
        String dbPassword = System.getenv("MYSQLPASSWORD");
        String dbName = System.getenv("MYSQL_DATABASE");

        List<String> comando = Arrays.asList(
                "mysql",
                "-h", dbHost,
                "-P", dbPort,
                "-u", dbUser,
                "-p" + dbPassword,
                dbName
        );

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectInput(new File(ruta));

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "✅ Restauración completada desde: " + nombreArchivo;
            } else {
                return "❌ Error durante la restauración. Código de salida: " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            return "❌ Excepción: " + e.getMessage();
        }
    }

    public String eliminarBackup(String filename) {
        // Ruta completa del archivo
        String ruta = backupProperties.getDirectory() + "/" + filename;
        File archivo = new File(ruta);

        // Intentar borrar archivo físico
        boolean archivoEliminado = false;
        if (archivo.exists()) {
            archivoEliminado = archivo.delete();
        }

        // Eliminar de la base de datos
        Optional<Backup> backupOpt = backupRepository.findAll()
                .stream()
                .filter(b -> b.getFilename().equals(filename))
                .findFirst();

        boolean registroEliminado = false;
        if (backupOpt.isPresent()) {
            backupRepository.delete(backupOpt.get());
            registroEliminado = true;
        }

        if (archivoEliminado && registroEliminado) {
            return "✅ Backup eliminado correctamente.";
        } else if (!archivoEliminado && registroEliminado) {
            return "⚠️ Registro borrado, pero no se encontró el archivo físico.";
        } else if (archivoEliminado && !registroEliminado) {
            return "⚠️ Archivo borrado, pero no se encontró el registro en la base de datos.";
        } else {
            return "❌ No se encontró el backup.";
        }
    }

    //@Scheduled(fixedRate = 30000) // cada 30 segundos
    @Scheduled(cron = "0 0 3 * * *") // Todos los días a las 03:00 AM
    public void generarBackupAutomatico() {
        System.out.println("🕒 Ejecutando backup programado...");
        generarDump();
    }

    //@Scheduled(fixedRate = 60000) // ⬅️ Para probar cada 60 seg
    @Scheduled(cron = "0 0 4 * * *") // Todos los días a las 04:00 AM
    public void eliminarBackupsAntiguos() {
        // Testear si anda
        //int diasLimite = 0;
        int diasLimite = 7;
        List<Backup> backups = backupRepository.findAll();

        for (Backup backup : backups) {
            LocalDateTime creado = backup.getCreatedAt();
            long dias = Duration.between(creado, LocalDateTime.now()).toDays();

            if (dias > diasLimite) {
                String path = backupProperties.getDirectory() + "/" + backup.getFilename();
                File archivo = new File(path);

                if (archivo.exists() && archivo.delete()) {
                    System.out.println("🗑️ Archivo eliminado: " + archivo.getName());
                } else {
                    System.out.println("⚠️ No se pudo eliminar archivo: " + archivo.getName());
                }

                backupRepository.delete(backup);
                System.out.println("🗃️ Registro eliminado: " + backup.getFilename());
            }
        }
    }
}