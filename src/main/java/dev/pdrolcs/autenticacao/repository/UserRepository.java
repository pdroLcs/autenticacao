package dev.pdrolcs.autenticacao.repository;

import dev.pdrolcs.autenticacao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
}
