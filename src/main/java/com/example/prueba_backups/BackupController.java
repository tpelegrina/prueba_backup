package com.example.prueba_backups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackupController {
    @Autowired
    private BackupService backupService;

    @GetMapping("/test-backup")
    public String testBackup() {
        backupService.testCrearArchivoDummy();
        return "Prueba ejecutada. Verifica la carpeta ./data/backups";
    }


    @PostMapping("/generar")
    public String generar() {
        backupService.generarDump();
        return "Proceso iniciado.";
    }

}