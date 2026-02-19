package com.graphqldemo.promp_eng_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    
    private Long id;
    private String name;
    private String bio;
    private List<BookDTO> books;
}
