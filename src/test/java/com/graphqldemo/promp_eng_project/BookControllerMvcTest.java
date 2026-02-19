package com.graphqldemo.promp_eng_project;

import com.graphqldemo.promp_eng_project.controller.BookController;
import com.graphqldemo.promp_eng_project.dto.AuthorDTO;
import com.graphqldemo.promp_eng_project.dto.BookDTO;
import com.graphqldemo.promp_eng_project.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BookControllerMvcTest {

    private BookService bookService;
    private BookController controller;

    private BookDTO savedBookForScenario1;
    private BookDTO savedBookForScenario2;

    @BeforeEach
    public void setUp() {
        bookService = Mockito.mock(BookService.class);
        controller = new BookController(bookService);

        // Prepare mocked responses from BookService.createBook
        AuthorDTO author1 = new AuthorDTO();
        author1.setName("Robert C. Martin");
        author1.setId(10L);

        savedBookForScenario1 = new BookDTO();
        savedBookForScenario1.setId(100L);
        savedBookForScenario1.setTitle("Clean Code: A Handbook of Agile Software Craftsmanship");
        savedBookForScenario1.setAuthor(author1);
        savedBookForScenario1.setPrice(new BigDecimal("49.99"));

        AuthorDTO author2 = new AuthorDTO();
        author2.setId(1L);
        author2.setName("Martin Fowler");

        savedBookForScenario2 = new BookDTO();
        savedBookForScenario2.setId(101L);
        savedBookForScenario2.setTitle("Refactoring: Improving the Design of Existing Code");
        savedBookForScenario2.setAuthor(author2);
        savedBookForScenario2.setPrice(new BigDecimal("54.99"));

        // Stubbing by title to return the prepared DTOs
        Mockito.when(bookService.createBook(ArgumentMatchers.argThat(b -> b != null && b.getTitle() != null && b.getTitle().startsWith("Clean Code"))))
                .thenReturn(savedBookForScenario1);
        Mockito.when(bookService.createBook(ArgumentMatchers.argThat(b -> b != null && "Refactoring: Improving the Design of Existing Code".equals(b.getTitle()))))
                .thenReturn(savedBookForScenario2);
    }

    @Test
    public void valid_complete_book_new_author() {
        BookDTO request = new BookDTO();
        request.setTitle("Clean Code: A Handbook of Agile Software Craftsmanship");
        AuthorDTO a = new AuthorDTO();
        a.setName("Robert C. Martin");
        // AuthorDTO has no email field in this project, so we only set name/id
        request.setAuthor(a);
        request.setPrice(new BigDecimal("49.99"));

        ResponseEntity<Object> resp = controller.addBook(request);
        assertEquals(201, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof BookDTO);
        BookDTO body = (BookDTO) resp.getBody();
        assertEquals("Clean Code: A Handbook of Agile Software Craftsmanship", body.getTitle());
        assertEquals("Robert C. Martin", body.getAuthor().getName());
    }

    @Test
    public void valid_book_existing_author() {
        BookDTO request = new BookDTO();
        request.setTitle("Refactoring: Improving the Design of Existing Code");
        AuthorDTO a = new AuthorDTO();
        a.setId(1L);
        request.setAuthor(a);
        request.setPrice(new BigDecimal("54.99"));

        ResponseEntity<Object> resp = controller.addBook(request);
        assertEquals(201, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof BookDTO);
        BookDTO body = (BookDTO) resp.getBody();
        assertEquals("Refactoring: Improving the Design of Existing Code", body.getTitle());
        assertNotNull(body.getAuthor());
        assertEquals(1L, body.getAuthor().getId().longValue());
    }

    @Test
    public void invalid_missing_title() {
        BookDTO request = new BookDTO();
        AuthorDTO a = new AuthorDTO();
        a.setName("Joshua Bloch");
        request.setAuthor(a);
        request.setPrice(new BigDecimal("44.99"));

        Mockito.when(bookService.createBook(ArgumentMatchers.argThat(b -> b != null && (b.getTitle() == null || b.getTitle().isBlank()))))
                .thenThrow(new IllegalArgumentException("title is required"));

        ResponseEntity<Object> resp = controller.addBook(request);
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof Map);
        assertEquals("title is required", ((Map) resp.getBody()).get("error"));
    }

    @Test
    public void invalid_negative_price() {
        BookDTO request = new BookDTO();
        request.setTitle("The Pragmatic Programmer");
        AuthorDTO a = new AuthorDTO();
        a.setName("David Thomas");
        request.setAuthor(a);
        request.setPrice(new BigDecimal("-19.99"));

        Mockito.when(bookService.createBook(ArgumentMatchers.argThat(b -> b != null && b.getPrice() != null && b.getPrice().compareTo(BigDecimal.ZERO) < 0)))
                .thenThrow(new IllegalArgumentException("price must be non-negative"));

        ResponseEntity<Object> resp = controller.addBook(request);
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof Map);
        assertEquals("price must be non-negative", ((Map) resp.getBody()).get("error"));
    }

    @Test
    public void invalid_email_format() {
        BookDTO request = new BookDTO();
        request.setTitle("Design Patterns: Elements of Reusable Object-Oriented Software");
        AuthorDTO a = new AuthorDTO();
        a.setName("Gang of Four");
        request.setAuthor(a);
        request.setPrice(new BigDecimal("59.99"));

        // AuthorDTO doesn't hold email; simulate service validation failure based on author name
        Mockito.when(bookService.createBook(ArgumentMatchers.argThat(b -> b != null && b.getAuthor() != null && "Gang of Four".equals(b.getAuthor().getName()))))
                .thenThrow(new IllegalArgumentException("author.email must be a valid email"));

        ResponseEntity<Object> resp = controller.addBook(request);
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof Map);
        assertEquals("author.email must be a valid email", ((Map) resp.getBody()).get("error"));
    }
}
