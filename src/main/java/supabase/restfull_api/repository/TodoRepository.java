package supabase.restfull_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supabase.restfull_api.entity.Todo;
import supabase.restfull_api.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface JPA Repository untuk mengelola akses data entitas Todo.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // Mengambil semua daftar todo milik pengguna tertentu
    List<Todo> findAllByPengguna(User pengguna);

    // Mengambil detail todo berdasarkan id dan pengguna tertentu
    Optional<Todo> findByIdAndPengguna(Long id, User pengguna);
}
