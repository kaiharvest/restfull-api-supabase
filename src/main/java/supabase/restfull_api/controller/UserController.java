package supabase.restfull_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import supabase.restfull_api.entity.User;
import supabase.restfull_api.model.*;
import supabase.restfull_api.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

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

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LoginResponse login(@RequestBody LoginUserRequest request) {
        return userService.login(request);
    }

    @RequestMapping(
            path = {"/api/users/current"},
            method = {RequestMethod.GET, RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user) {
        UserResponse response = userService.get(user);
        return WebResponse.<UserResponse>builder()
                .data(response)
                .build();
    }


    @PatchMapping(
            path = {"/api/users/current"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(user, request);
        return WebResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping(
            path = {"/api/users/current"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(User user) {
        userService.logout(user);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @GetMapping(
            path = "/api/users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<java.util.List<UserResponse>> getAll() {
        java.util.List<UserResponse> responses = userService.getAll();
        return WebResponse.<java.util.List<UserResponse>>builder()
                .data(responses)
                .build();
    }
}
