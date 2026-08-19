package dev.pdrolcs.autenticacao.repository;

import dev.pdrolcs.autenticacao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserDetails> findUserByEmail(String email);

    boolean existsUserByEmail(String email);

    Optional<UserDetails> findUserByPublicId(UUID publicId);
}
