package com.graphqldemo.promp_eng_project.repository;

import com.graphqldemo.promp_eng_project.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByTitle(String title);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);
    
    boolean existsByTitle(String title);
}
