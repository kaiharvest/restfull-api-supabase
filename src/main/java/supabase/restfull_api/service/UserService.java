package supabase.restfull_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import supabase.restfull_api.entity.User;
import supabase.restfull_api.model.LoginUserRequest;
import supabase.restfull_api.model.UpdateUserRequest;
import supabase.restfull_api.model.RegisterUserRequest;
import supabase.restfull_api.model.LoginResponse;
import supabase.restfull_api.model.UserResponse;
import supabase.restfull_api.repository.UserRepository;

import java.util.UUID;

/**
 * Service to manage User business logic.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    /**
     * Registers a new user.
     *
     * @param request The user registration request
     */
    @Transactional
    public void register(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .name(request.getName())
                .build();

        userRepository.save(user);
    }

    /**
     * Logins a user and generates a token.
     *
     * @param request The login request
     * @return LoginResponse containing the token and expiration time
     */
    @Transactional
    public LoginResponse login(LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }

        String token = UUID.randomUUID().toString();
        // Token validity: 30 days
        Long tokenExpiredAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);

        user.setToken(token);
        user.setTokenExpiredAt(tokenExpiredAt);
        userRepository.save(user);

        return LoginResponse.builder()
                .token(token)
                .expiredAt(tokenExpiredAt)
                .build();
    }

    /**
     * Gets user profile.
     *
     * @param user The authenticated user entity
     * @return UserResponse containing profile details
     */
    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    /**
     * Updates user profile details.
     *
     * @param user    The authenticated user entity
     * @param request The update request
     * @return UserResponse containing updated profile details
     */
    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        validationService.validate(request);

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPassword() != null) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    /**
     * Logouts user by clearing active token.
     *
     * @param user The user entity to log out
     */
    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }
}
