package supabase.restfull_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank(message = "username must not be blank")
    @Size(max = 100)
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "password must not be blank")
    @Size(max = 100)
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "name must not be blank")
    @Size(max = 100)
    @JsonProperty("name")
    private String name;
}
