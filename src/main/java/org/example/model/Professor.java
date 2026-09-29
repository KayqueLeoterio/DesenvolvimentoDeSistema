package org.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Professor {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "SIAPE é obrigatório")
    private String siape;

    @NotBlank(message = "Área é obrigatória")
    private String area;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

}
