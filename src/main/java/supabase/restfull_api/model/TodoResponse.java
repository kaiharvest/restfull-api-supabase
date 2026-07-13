package supabase.restfull_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model data response untuk detail data Todo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String judul;

    @JsonProperty("description")
    private String deskripsi;

    @JsonProperty("status")
    private String status;

    @JsonProperty("deadline")
    private LocalDate tenggatWaktu;

    @JsonProperty("createdAt")
    private LocalDateTime dibuatPada;

    @JsonProperty("updatedAt")
    private LocalDateTime diperbaruiPada;
}
