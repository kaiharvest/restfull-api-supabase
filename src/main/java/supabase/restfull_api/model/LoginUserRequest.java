package supabase.restfull_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserRequest {

    @NotBlank(message = "username must not be blank")
    @Size(max = 100)
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "password must not be blank")
    @Size(max = 100)
    @JsonProperty("password")
    private String password;
}
