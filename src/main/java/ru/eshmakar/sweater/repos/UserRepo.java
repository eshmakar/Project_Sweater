package ru.eshmakar.sweater.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eshmakar.sweater.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationCode(String code);
}
