package supabase.restfull_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entitas JPA yang merepresentasikan tabel todos di database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Kolom judul menggunakan Bahasa Indonesia sesuai aturan global
    @Column(name = "judul", nullable = false)
    private String judul;

    // Kolom deskripsi untuk menyimpan rincian tugas
    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    // Status tugas (misalnya: TODO, IN_PROGRESS, DONE)
    @Column(name = "status", nullable = false)
    private String status;

    // Batas waktu penyelesaian tugas
    @Column(name = "tenggat_waktu")
    private LocalDate tenggatWaktu;

    // Relasi ke entitas User (banyak todo dimiliki oleh satu pengguna)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User pengguna;

    // Tanggal dan waktu pembuatan data
    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;

    // Tanggal dan waktu pembaruan data terakhir
    @Column(name = "diperbarui_pada")
    private LocalDateTime diperbaruiPada;
}
