package com.example.lab7.Repository;

import com.example.lab7.Entity.Resource;
import com.example.lab7.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource,Integer> {


}
