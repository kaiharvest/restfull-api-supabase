package supabase.restfull_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supabase.restfull_api.entity.User;

import java.util.Optional;

/**
 * Repository interface for database operations on User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Finds the first user by active session token.
     *
     * @param token Session token to look up
     * @return Optional containing the User if found
     */
    Optional<User> findFirstByToken(String token);
}
