package supabase.restfull_api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;
import supabase.restfull_api.entity.Todo;
import supabase.restfull_api.entity.User;
import supabase.restfull_api.model.CreateTodoRequest;
import supabase.restfull_api.model.UpdateTodoRequest;
import supabase.restfull_api.repository.TodoRepository;
import supabase.restfull_api.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test class untuk memverifikasi fungsionalitas TodoController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User penggunaTes;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        userRepository.deleteAll();

        // Menyiapkan data pengguna penguji
        penggunaTes = User.builder()
                .username("indra123")
                .password(BCrypt.hashpw("rahasia123", BCrypt.gensalt()))
                .name("Indra Wijaya")
                .token("TOKEN_UJI_COBA")
                .tokenExpiredAt(System.currentTimeMillis() + 600000L) // Berlaku 10 menit
                .build();

        userRepository.save(penggunaTes);
    }

    @Test
    void testCreateTodoSuccess() throws Exception {
        CreateTodoRequest request = CreateTodoRequest.builder()
                .judul("Belajar Docker")
                .deskripsi("Membuat Docker Compose")
                .status("TODO")
                .tenggatWaktu(LocalDate.of(2026, 7, 25))
                .build();

        mockMvc.perform(
                post("/api/todos/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "TOKEN_UJI_COBA")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data.id").exists(),
                jsonPath("$.data.title").value("Belajar Docker"),
                jsonPath("$.data.description").value("Membuat Docker Compose"),
                jsonPath("$.data.status").value("TODO"),
                jsonPath("$.data.deadline").value("2026-07-25"),
                jsonPath("$.errors").isEmpty()
        );

        assertFalse(todoRepository.findAll().isEmpty());
    }

    @Test
    void testCreateTodoFailedUnauthorized() throws Exception {
        CreateTodoRequest request = CreateTodoRequest.builder()
                .judul("Belajar Kubernetes")
                .build();

        mockMvc.perform(
                post("/api/todos/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized(),
                jsonPath("$.errors").value("Unauthorized")
        );
    }

    @Test
    void testCreateTodoFailedValidation() throws Exception {
        CreateTodoRequest request = CreateTodoRequest.builder()
                .judul("") // Judul kosong (gagal validasi NotBlank)
                .build();

        mockMvc.perform(
                post("/api/todos/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "TOKEN_UJI_COBA")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.errors").isNotEmpty()
        );
    }

    @Test
    void testListTodoSuccess() throws Exception {
        Todo todo1 = Todo.builder()
                .judul("Tugas ke-1")
                .status("TODO")
                .pengguna(penggunaTes)
                .build();
        Todo todo2 = Todo.builder()
                .judul("Tugas ke-2")
                .status("IN_PROGRESS")
                .pengguna(penggunaTes)
                .build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        mockMvc.perform(
                get("/api/todos/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "TOKEN_UJI_COBA")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data.length()").value(2),
                jsonPath("$.data[0].title").value("Tugas ke-1"),
                jsonPath("$.data[1].title").value("Tugas ke-2"),
                jsonPath("$.errors").isEmpty()
        );
    }

    @Test
    void testGetTodoByIdSuccess() throws Exception {
        Todo todo = Todo.builder()
                .judul("Membaca Dokumentasi")
                .status("TODO")
                .pengguna(penggunaTes)
                .build();

        todoRepository.save(todo);

        mockMvc.perform(
                get("/api/todos/detail/" + todo.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "TOKEN_UJI_COBA")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data.title").value("Membaca Dokumentasi"),
                jsonPath("$.errors").isEmpty()
        );
    }

    @Test
    void testGetTodoByIdNotFound() throws Exception {
        mockMvc.perform(
                get("/api/todos/detail/99999")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "TOKEN_UJI_COBA")
        ).andExpectAll(
                status().isNotFound(),
                jsonPath("$.errors").value("Todo not found")
        );
    }

    @Test
    void testUpdateTodoSuccess() throws Exception {
        Todo todo = Todo.builder()
                .judul("Judul Awal")
                .deskripsi("Deskripsi Awal")
                .status("TODO")
                .pengguna(penggunaTes)
                .build();

        todoRepository.save(todo);

        UpdateTodoRequest request = UpdateTodoRequest.builder()
                .judul("Judul Baru")
                .deskripsi("Deskripsi Baru")
                .status("DONE")
                .tenggatWaktu(LocalDate.of(2026, 7, 28))
                .build();

        mockMvc.perform(
                patch("/api/todos/update/" + todo.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "TOKEN_UJI_COBA")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data.title").value("Judul Baru"),
                jsonPath("$.data.description").value("Deskripsi Baru"),
                jsonPath("$.data.status").value("DONE"),
                jsonPath("$.data.deadline").value("2026-07-28"),
                jsonPath("$.errors").isEmpty()
        );
    }

    @Test
    void testDeleteTodoSuccess() throws Exception {
        Todo todo = Todo.builder()
                .judul("Akan Dihapus")
                .status("TODO")
                .pengguna(penggunaTes)
                .build();

        todoRepository.save(todo);

        mockMvc.perform(
                delete("/api/todos/delete/" + todo.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "TOKEN_UJI_COBA")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.data").value("OK"),
                jsonPath("$.errors").isEmpty()
        );

        assertTrue(todoRepository.findById(todo.getId()).isEmpty());
    }
}
