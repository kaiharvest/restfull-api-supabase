package supabase.restfull_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import supabase.restfull_api.entity.Todo;
import supabase.restfull_api.entity.User;
import supabase.restfull_api.model.CreateTodoRequest;
import supabase.restfull_api.model.TodoResponse;
import supabase.restfull_api.model.UpdateTodoRequest;
import supabase.restfull_api.repository.TodoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class untuk mengelola proses logika bisnis Todo.
 */
@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ValidationService validationService;

    // Helper untuk mengubah entitas Todo menjadi TodoResponse
    private TodoResponse toTodoResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .judul(todo.getJudul())
                .deskripsi(todo.getDeskripsi())
                .status(todo.getStatus())
                .tenggatWaktu(todo.getTenggatWaktu())
                .dibuatPada(todo.getDibuatPada())
                .diperbaruiPada(todo.getDiperbaruiPada())
                .build();
    }

    /**
     * Membuat data Todo baru.
     *
     * @param pengguna Entitas pengguna yang login
     * @param request Data todo baru
     * @return TodoResponse data todo yang berhasil dibuat
     */
    @Transactional
    public TodoResponse create(User pengguna, CreateTodoRequest request) {
        validationService.validate(request);

        String statusAwal = request.getStatus();
        if (statusAwal == null || statusAwal.isBlank()) {
            statusAwal = "TODO";
        }

        Todo todo = Todo.builder()
                .judul(request.getJudul())
                .deskripsi(request.getDeskripsi())
                .status(statusAwal)
                .tenggatWaktu(request.getTenggatWaktu())
                .pengguna(pengguna)
                .dibuatPada(LocalDateTime.now())
                .diperbaruiPada(LocalDateTime.now())
                .build();

        todoRepository.save(todo);

        return toTodoResponse(todo);
    }

    /**
     * Mengambil seluruh daftar Todo milik pengguna tertentu.
     *
     * @param pengguna Entitas pengguna yang login
     * @return List dari TodoResponse
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> list(User pengguna) {
        List<Todo> daftarTodo = todoRepository.findAllByPengguna(pengguna);
        return daftarTodo.stream()
                .map(this::toTodoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mengambil detail Todo berdasarkan ID.
     *
     * @param pengguna Entitas pengguna yang login
     * @param id ID dari todo
     * @return TodoResponse detail data todo
     */
    @Transactional(readOnly = true)
    public TodoResponse get(User pengguna, Long id) {
        Todo todo = todoRepository.findByIdAndPengguna(id, pengguna)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        return toTodoResponse(todo);
    }

    /**
     * Memperbarui data Todo.
     *
     * @param pengguna Entitas pengguna yang login
     * @param id ID dari todo
     * @param request Data pembaruan todo
     * @return TodoResponse data todo yang diperbarui
     */
    @Transactional
    public TodoResponse update(User pengguna, Long id, UpdateTodoRequest request) {
        validationService.validate(request);

        Todo todo = todoRepository.findByIdAndPengguna(id, pengguna)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        if (request.getJudul() != null) {
            todo.setJudul(request.getJudul());
        }
        if (request.getDeskripsi() != null) {
            todo.setDeskripsi(request.getDeskripsi());
        }
        if (request.getStatus() != null) {
            todo.setStatus(request.getStatus());
        }
        if (request.getTenggatWaktu() != null) {
            todo.setTenggatWaktu(request.getTenggatWaktu());
        }
        todo.setDiperbaruiPada(LocalDateTime.now());

        todoRepository.save(todo);

        return toTodoResponse(todo);
    }

    /**
     * Menghapus data Todo.
     *
     * @param pengguna Entitas pengguna yang login
     * @param id ID dari todo
     */
    @Transactional
    public void delete(User pengguna, Long id) {
        Todo todo = todoRepository.findByIdAndPengguna(id, pengguna)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        todoRepository.delete(todo);
    }
}
