package supabase.restfull_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API Web Response wrapper.
 *
 * @param <T> Response data type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebResponse<T> {

    @JsonProperty("data")
    private T data;

    @JsonProperty("errors")
    private String errors;
}
