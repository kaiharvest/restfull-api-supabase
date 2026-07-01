package supabase.restfull_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import supabase.restfull_api.entity.User;
import supabase.restfull_api.model.*;
import supabase.restfull_api.service.UserService;

/**
 * REST API Controller for managing user operations.
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint for user registration.
     * POST /api/users
     *
     * @param request User registration payload
     * @return WebResponse indicating success
     */
    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        userService.register(request);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    /**
     * Endpoint for user login.
     * POST /api/auth/login
     *
     * @param request User login credentials
     * @return LoginResponse containing auth token and expiration (unwrapped)
     */
    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LoginResponse login(@RequestBody LoginUserRequest request) {
        return userService.login(request);
    }

    /**
     * Endpoint for retrieving the current logged-in user profile details.
     * Supports both GET and POST, and handles specifications typo "/curerent".
     *
     * @param user The authenticated user entity injected by resolver
     * @return WebResponse containing UserResponse profile data
     */
    @RequestMapping(
            path = {"/api/users/current", "/api/users/curerent"},
            method = {RequestMethod.GET, RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user) {
        UserResponse response = userService.get(user);
        return WebResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    /**
     * Endpoint for updating current logged-in user profile.
     * Supports PATCH, and handles specifications typo "/curerent".
     *
     * @param user    The authenticated user entity injected by resolver
     * @param request The update payload
     * @return WebResponse containing updated UserResponse profile data
     */
    @PatchMapping(
            path = {"/api/users/current", "/api/users/curerent"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(user, request);
        return WebResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    /**
     * Endpoint for logging out the current user.
     * Supports DELETE, and handles specifications typo "/curerent".
     *
     * @param user The authenticated user entity injected by resolver
     * @return WebResponse indicating success
     */
    @DeleteMapping(
            path = {"/api/users/current", "/api/users/curerent"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(User user) {
        userService.logout(user);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }
}
