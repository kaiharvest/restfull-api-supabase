package supabase.restfull_api.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service helper to validate objects using Jakarta Validation.
 */
@Service
public class ValidationService {

    @Autowired
    private Validator validator;

    /**
     * Validates the request object. Throws ConstraintViolationException if invalid.
     *
     * @param request The object to validate
     */
    public void validate(Object request) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
