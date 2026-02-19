package com.graphqldemo.promp_eng_project.service;

import com.graphqldemo.promp_eng_project.dto.BookDTO;
import com.graphqldemo.promp_eng_project.entity.Author;
import com.graphqldemo.promp_eng_project.entity.Book;
import com.graphqldemo.promp_eng_project.repository.AuthorRepository;
import com.graphqldemo.promp_eng_project.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    
    public Optional<BookDTO> findBookByTitle(String title) {
        return bookRepository.findByTitle(title)
                .map(this::convertToDTO);
    }
    
    public List<BookDTO> findBooksByTitleContaining(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public BookDTO addBook(String title, String authorName, BigDecimal price) {
        if (bookRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("Book with title '" + title + "' already exists");
        }
        
        Author author = authorRepository.findByName(authorName)
                .orElseThrow(() -> new IllegalArgumentException("Author '" + authorName + "' not found"));
        
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }
    
    /**
     * Create a book from BookDTO. If author contains an id, link existing author; otherwise create a new Author.
     * Performs minimal validations and throws IllegalArgumentException for bad input (mapped to 400 by controller).
     */
    public BookDTO createBook(BookDTO dto) {
        if (dto == null) throw new IllegalArgumentException("payload empty");
        if (dto.getTitle() == null || dto.getTitle().isBlank()) throw new IllegalArgumentException("title is required");
        if (dto.getPrice() == null) throw new IllegalArgumentException("price is required");
        if (dto.getPrice().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("price must be non-negative");

        com.graphqldemo.promp_eng_project.dto.AuthorDTO authorDto = dto.getAuthor();
        if (authorDto == null) throw new IllegalArgumentException("author is required");

        Author authorEntity = null;
        if (authorDto.getId() != null) {
            authorEntity = authorRepository.findById(authorDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("author not found"));
        } else {
            if (authorDto.getName() == null || authorDto.getName().isBlank()) throw new IllegalArgumentException("author.name is required");
            // Basic email format check (defer to controller/validation in real app)
            if (authorDto.getBio() == null) authorDto.setBio(null);
            Author a = new Author();
            a.setName(authorDto.getName());
            a.setBio(authorDto.getBio());
            authorEntity = authorRepository.save(a);
        }

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(authorEntity);
        book.setPrice(dto.getPrice());

        Book saved = bookRepository.save(book);
        return convertToDTO(saved);
    }

    public BookDTO applyDiscount(Long bookId, BigDecimal discountPercentage) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || 
            discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with id " + bookId + " not found"));
        
        BigDecimal discountAmount = book.getPrice()
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100));
        
        BigDecimal newPrice = book.getPrice().subtract(discountAmount);
        book.setPrice(newPrice);
        
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }
    
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    private BookDTO convertToDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setPrice(book.getPrice());
        
        if (book.getAuthor() != null) {
            bookDTO.setAuthor(convertAuthorToDTO(book.getAuthor()));
        }
        
        return bookDTO;
    }
    
    private com.graphqldemo.promp_eng_project.dto.AuthorDTO convertAuthorToDTO(Author author) {
        com.graphqldemo.promp_eng_project.dto.AuthorDTO authorDTO = 
            new com.graphqldemo.promp_eng_project.dto.AuthorDTO();
        authorDTO.setId(author.getId());
        authorDTO.setName(author.getName());
        authorDTO.setBio(author.getBio());
        return authorDTO;
    }
}
