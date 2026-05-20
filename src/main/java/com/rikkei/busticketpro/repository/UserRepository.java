package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //Dùng cho Spring Security login
    Optional<User> findByUsername(String username);

    //Kiểm tra username đã tồn tại khi đăng ký
    boolean existsByUsername(String username);
}
