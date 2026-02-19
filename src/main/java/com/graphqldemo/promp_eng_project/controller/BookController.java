package com.graphqldemo.promp_eng_project.controller;

import com.graphqldemo.promp_eng_project.dto.BookDTO;
import com.graphqldemo.promp_eng_project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@RequestParam String title) {
        List<BookDTO> books = bookService.findBooksByTitleContaining(title);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/title/{title}")
    public ResponseEntity<BookDTO> getBookByTitle(@PathVariable String title) {
        return bookService.findBookByTitle(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Object> addBook(@RequestBody com.graphqldemo.promp_eng_project.dto.BookDTO requestDto) {
        try {
            BookDTO savedBook = bookService.createBook(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (IllegalArgumentException e) {
            // return a small error body identifying the offending field when possible
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "internal"));
        }
    }
    
    @PutMapping("/{id}/discount")
    public ResponseEntity<BookDTO> applyDiscount(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal discountPercentage = new BigDecimal(request.get("discountPercentage").toString());
            BookDTO updatedBook = bookService.applyDiscount(id, discountPercentage);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
