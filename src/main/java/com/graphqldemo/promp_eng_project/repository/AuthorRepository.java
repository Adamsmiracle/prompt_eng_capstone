package com.graphqldemo.promp_eng_project.repository;

import com.graphqldemo.promp_eng_project.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    Optional<Author> findByName(String name);
    
    boolean existsByName(String name);
}
