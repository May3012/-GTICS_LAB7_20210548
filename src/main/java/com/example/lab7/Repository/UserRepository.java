package com.example.lab7.Repository;

import com.example.lab7.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM users where authorizedResource=?1;")
    List<User> listar(int id);

}
