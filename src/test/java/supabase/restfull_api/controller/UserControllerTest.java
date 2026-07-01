package supabase.restfull_api.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;
import supabase.restfull_api.entity.User;
import supabase.restfull_api.model.LoginUserRequest;
import supabase.restfull_api.model.UpdateUserRequest;
import supabase.restfull_api.model.RegisterUserRequest;
import supabase.restfull_api.model.LoginResponse;
import supabase.restfull_api.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("budi")
                .password("secret123")
                .name("Budi Sudarsono")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data").value("OK"),
                jsonPath("$.errors").isEmpty()
        );

        assertTrue(userRepository.existsById("budi"));
    }

    @Test
    void testRegisterFailedValidation() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("") // empty (failed validation)
                .password("")
                .name("")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.errors").isNotEmpty()
        );
    }

    @Test
    void testRegisterFailedUsernameAlreadyRegistered() throws Exception {
        User existingUser = User.builder()
                .username("budi")
                .password(BCrypt.hashpw("secret", BCrypt.gensalt()))
                .name("Budi Old")
                .build();
        userRepository.save(existingUser);

        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("budi")
                .password("secret123")
                .name("Budi New")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.errors").value("Username already registered")
        );
    }

    @Test
    void testLoginSuccess() throws Exception {
        User user = User.builder()
                .username("budi")
                .password(BCrypt.hashpw("secret123", BCrypt.gensalt()))
                .name("Budi Sudarsono")
                .build();
        userRepository.save(user);

        LoginUserRequest request = LoginUserRequest.builder()
                .username("budi")
                .password("secret123")
                .build();

        String responseContent = mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isOk()
        ).andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(responseContent, LoginResponse.class);
        assertNotNull(loginResponse.getToken());
        assertNotNull(loginResponse.getExpiredAt());
        assertTrue(loginResponse.getExpiredAt() > System.currentTimeMillis());

        User afterLogin = userRepository.findById("budi").orElseThrow();
        assertEquals(loginResponse.getToken(), afterLogin.getToken());
    }

    @Test
    void testLoginFailedWrongPassword() throws Exception {
        User user = User.builder()
                .username("budi")
                .password(BCrypt.hashpw("secret123", BCrypt.gensalt()))
                .name("Budi Sudarsono")
                .build();
        userRepository.save(user);

        LoginUserRequest request = LoginUserRequest.builder()
                .username("budi")
                .password("wrong_password")
                .build();

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized(),
                jsonPath("$.errors").value("Username or password wrong")
        );
    }

    @Test
    void testGetUserSuccess() throws Exception {
        User user = User.builder()
                .username("budi")
                .password(BCrypt.hashpw("secret", BCrypt.gensalt()))
                .name("Budi Sudarsono")
                .token("ACTIVE_TOKEN")
                .tokenExpiredAt(System.currentTimeMillis() + 600000L) // 10 minutes active
                .build();
        userRepository.save(user);

        // GET current
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "ACTIVE_TOKEN")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data.username").value("budi"),
                jsonPath("$.data.name").value("Budi Sudarsono"),
                jsonPath("$.errors").isEmpty()
        );

        // POST curerent (typo in spec support)
        mockMvc.perform(
                post("/api/users/curerent")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "ACTIVE_TOKEN")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data.username").value("budi"),
                jsonPath("$.data.name").value("Budi Sudarsono")
        );
    }

    @Test
    void testGetUserFailedTokenExpired() throws Exception {
        User user = User.builder()
                .username("budi")
                .password(BCrypt.hashpw("secret", BCrypt.gensalt()))
                .name("Budi Sudarsono")
                .token("EXPIRED_TOKEN")
                .tokenExpiredAt(System.currentTimeMillis() - 1000L) // expired 1s ago
                .build();
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "EXPIRED_TOKEN")
        ).andExpectAll(
                status().isUnauthorized(),
                jsonPath("$.errors").value("Unauthorized")
        );
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        User user = User.builder()
                .username("budi")
                .password(BCrypt.hashpw("secret", BCrypt.gensalt()))
                .name("Budi Sudarsono")
                .token("ACTIVE_TOKEN")
                .tokenExpiredAt(System.currentTimeMillis() + 600000L)
                .build();
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Budi Sudarsono Updated")
                .password("newSecret")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "ACTIVE_TOKEN")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data.username").value("budi"),
                jsonPath("$.data.name").value("Budi Sudarsono Updated"),
                jsonPath("$.errors").isEmpty()
        );

        User updatedUser = userRepository.findById("budi").orElseThrow();
        assertTrue(BCrypt.checkpw("newSecret", updatedUser.getPassword()));
    }

    @Test
    void testLogoutSuccess() throws Exception {
        User user = User.builder()
                .username("budi")
                .password(BCrypt.hashpw("secret", BCrypt.gensalt()))
                .name("Budi Sudarsono")
                .token("LOGOUT_TOKEN")
                .tokenExpiredAt(System.currentTimeMillis() + 600000L)
                .build();
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "LOGOUT_TOKEN")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data").value("OK"),
                jsonPath("$.errors").isEmpty()
        );

        User afterLogout = userRepository.findById("budi").orElseThrow();
        assertNull(afterLogout.getToken());
        assertNull(afterLogout.getTokenExpiredAt());
    }
}
