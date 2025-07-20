package com.example.prueba_backups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "backups")
public class BackupController {
    @Autowired
    private BackupService backupService;

    @PostMapping("/generar")
    public String generar() {
        backupService.generarDump();
        return "Proceso iniciado.";
    }

    @GetMapping("/listar-archivos")
    public ResponseEntity<List<String>> listarArchivos() {
        List<String> archivos = backupService.listarArchivosBackup();

        if (archivos.isEmpty()) {
            return ResponseEntity.ok(List.of("⚠️ No hay archivos de backup."));
        }

        return ResponseEntity.ok(archivos);
    }

    @PostMapping("/restaurar/{filename}")
    public ResponseEntity<String> restaurar(@PathVariable String filename) {
        String resultado = backupService.restaurarBackup(filename);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/eliminar/{filename}")
    public ResponseEntity<String> eliminarBackup(@PathVariable String filename) {
        String resultado = backupService.eliminarBackup(filename);
        return ResponseEntity.ok(resultado);
    }
}