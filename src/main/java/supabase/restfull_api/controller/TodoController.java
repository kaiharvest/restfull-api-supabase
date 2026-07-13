package supabase.restfull_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import supabase.restfull_api.entity.User;
import supabase.restfull_api.model.CreateTodoRequest;
import supabase.restfull_api.model.TodoResponse;
import supabase.restfull_api.model.UpdateTodoRequest;
import supabase.restfull_api.model.WebResponse;
import supabase.restfull_api.service.TodoService;

import java.util.List;

/**
 * REST Controller untuk menangani HTTP request endpoint Todo.
 */
@RestController
public class TodoController {

    @Autowired
    private TodoService todoService;

    // Endpoint untuk membuat Todo baru
    @PostMapping(
            path = "/api/todos/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TodoResponse> create(User user, @RequestBody CreateTodoRequest request) {
        TodoResponse response = todoService.create(user, request);
        return WebResponse.<TodoResponse>builder()
                .data(response)
                .build();
    }

    // Endpoint untuk melihat semua Todo milik pengguna yang login
    @GetMapping(
            path = "/api/todos/list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TodoResponse>> list(User user) {
        List<TodoResponse> response = todoService.list(user);
        return WebResponse.<List<TodoResponse>>builder()
                .data(response)
                .build();
    }

    // Endpoint untuk melihat detail Todo berdasarkan ID
    @GetMapping(
            path = "/api/todos/detail/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TodoResponse> get(User user, @PathVariable("id") Long id) {
        TodoResponse response = todoService.get(user, id);
        return WebResponse.<TodoResponse>builder()
                .data(response)
                .build();
    }

    // Endpoint untuk memperbarui data Todo
    @PatchMapping(
            path = "/api/todos/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TodoResponse> update(User user, @PathVariable("id") Long id, @RequestBody UpdateTodoRequest request) {
        TodoResponse response = todoService.update(user, id, request);
        return WebResponse.<TodoResponse>builder()
                .data(response)
                .build();
    }

    // Endpoint untuk menghapus data Todo
    @DeleteMapping(
            path = "/api/todos/delete/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable("id") Long id) {
        todoService.delete(user, id);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }
}
