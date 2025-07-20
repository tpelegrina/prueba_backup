package com.example.prueba_backups.Backups;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "backups")
public class Backup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Backup() {}

    public Backup(String filename) {
        this.filename = filename;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
