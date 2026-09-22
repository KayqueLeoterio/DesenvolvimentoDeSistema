package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Matricula é obrigatória")
    private String matricula;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "email inválido")
    private String email;

    @NotBlank(message = "Curso é obrigatório")
    private String curso;

    @NotBlank(message = "Ano de Ingresso é obrigatório")
    @Pattern(regexp = "\\d{4}/[12]", message = "Ano de Ingresso deve estar no formato AAAA/1 (Ano/Semestre)")
    private String anoIngresso;

    @NotNull(message = "Data de nascimento é obrigatório")
    @Past(message = "Data de nascimento deve ser uma data no passado")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate nascimento;

}
