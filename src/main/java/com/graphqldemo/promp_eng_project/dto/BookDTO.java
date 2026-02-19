package com.graphqldemo.promp_eng_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    
    private Long id;
    private String title;
    private AuthorDTO author;
    private BigDecimal price;
}
