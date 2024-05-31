package com.example.lab7.Repository;

import com.example.lab7.Entity.Resource;
import com.example.lab7.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResourceRepository extends JpaRepository<Resource,Integer> {
    @Query(nativeQuery = true,value = "select * from resources where name=?1")
    Resource buscar(String string);

}
