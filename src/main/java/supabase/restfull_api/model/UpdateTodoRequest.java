package supabase.restfull_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Model data request untuk memperbarui data Todo yang ada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTodoRequest {

    @Size(max = 255)
    @JsonProperty("title")
    private String judul;

    @JsonProperty("description")
    private String deskripsi;

    @JsonProperty("status")
    private String status;

    @JsonProperty("deadline")
    private LocalDate tenggatWaktu;
}
