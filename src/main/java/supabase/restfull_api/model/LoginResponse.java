package supabase.restfull_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("expiredAt")
    private Long expiredAt;
}
