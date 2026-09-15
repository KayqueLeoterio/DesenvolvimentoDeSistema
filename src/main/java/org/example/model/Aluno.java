package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno {

    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String matricula;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String curso;

    @Pattern(regexp = "\\d{4}/[12]")
    private String anoIngresso;

    @NotNull
    @Past
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate nascimento;

}
