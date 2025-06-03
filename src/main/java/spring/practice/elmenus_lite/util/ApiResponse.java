package spring.practice.elmenus_lite.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

@Setter
@Getter
public class ApiResponse<T> {
    private String statusMessage;
    private T data;

    public ApiResponse(int i, String statusMessage, T data) {
        this.statusMessage = statusMessage;
        this.data = data;
    }

    public ApiResponse(String statusMessage) {
        this(200, statusMessage, null);
    }

    /**
     * Creates an error response.
     *
     * @param status HTTP status for the error
     * @param message Error message to include
     * @return ResponseEntity with formatted ApiResponse
     */
    public static ResponseEntity<ApiResponse<?>> error(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ApiResponse<>(status.value(), message, Collections.emptyList()));
    }

}
