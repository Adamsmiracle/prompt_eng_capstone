package com.graphqldemo.promp_eng_project;

import com.graphqldemo.promp_eng_project.dto.AuthorDTO;
import com.graphqldemo.promp_eng_project.dto.BookDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class BookDtoPojoTest {

    private boolean isValid(BookDTO b) {
        if (b == null) return false;
        if (b.getTitle() == null || b.getTitle().isBlank()) return false;
        if (b.getPrice() == null) return false;
        if (b.getPrice().compareTo(BigDecimal.ZERO) < 0) return false;
        AuthorDTO a = b.getAuthor();
        if (a == null) return false;
        // AuthorDTO contains id, name, bio and books; only name is validated here
        if (a.getName() == null || a.getName().isBlank()) return false;
        return true;
    }

    @Test
    public void scenarios_validateAsExpected() {
        // This method is kept for backward compatibility but delegates to the individual tests.
        testValidCompleteBook();
        testValidMinimalBook();
        testInvalidMissingTitle();
        testInvalidNegativePrice();
        testEdgeCaseLongTitleMaxPrice();
    }

    @Test
    public void testValidCompleteBook() {
        AuthorDTO a1 = new AuthorDTO(1L, "Robert C. Martin", null, null);
        BookDTO b1 = new BookDTO(1L, "Clean Code: A Handbook of Agile Software Craftsmanship", a1, new BigDecimal("49.99"));
        assertTrue(isValid(b1), "1_valid_complete_book should be valid");
    }

    @Test
    public void testValidMinimalBook() {
        AuthorDTO a2 = new AuthorDTO(null, "Martin Fowler", null, null);
        BookDTO b2 = new BookDTO(null, "Refactoring", a2, new BigDecimal("39.99"));
        assertTrue(isValid(b2), "2_valid_minimal_book should be valid");
    }

    @Test
    public void testInvalidMissingTitle() {
        AuthorDTO a3 = new AuthorDTO(2L, "Joshua Bloch", null, null);
        BookDTO b3 = new BookDTO(2L, null, a3, new BigDecimal("54.99")); // missing title
        assertFalse(isValid(b3), "3_invalid_missing_title should be invalid (missing title)");
    }

    @Test
    public void testInvalidNegativePrice() {
        AuthorDTO a4 = new AuthorDTO(3L, "David Thomas", null, null);
        BookDTO b4 = new BookDTO(3L, "The Pragmatic Programmer", a4, new BigDecimal("-19.99")); // negative price
        assertFalse(isValid(b4), "4_invalid_negative_price should be invalid (negative price)");
    }

    @Test
    public void testEdgeCaseLongTitleMaxPrice() {
        AuthorDTO a5 = new AuthorDTO(4L, "Edward Longbottom-Haversham", null, null);
        BookDTO b5 = new BookDTO(4L,
                "An Exceptionally Verbose and Thoroughly Comprehensive Guide to Understanding the Deeply Complex and Highly Nuanced Principles of Modern Software Architecture and Design Patterns in Enterprise Applications",
                a5,
                new BigDecimal("99999999.99"));
        assertTrue(isValid(b5), "5_edge_case_long_title_max_price should be valid");
    }
}
