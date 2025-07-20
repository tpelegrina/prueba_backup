package com.example.prueba_backups;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "backups")
public class Backup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public Backup() {}

    public Backup(String filename) {
        this.filename = filename;
        this.createdAt = OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
