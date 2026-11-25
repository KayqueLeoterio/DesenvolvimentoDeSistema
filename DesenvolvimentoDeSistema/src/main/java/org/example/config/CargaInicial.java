package org.example.config;

import org.example.model.Aluno;
import org.example.model.Professor;
import org.example.repository.AlunoRepository;
import org.example.repository.ProfessorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Cadastra registros de exemplo apenas quando as tabelas estão vazias. */
@Component
public class CargaInicial implements CommandLineRunner {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public CargaInicial(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    public void run(String... args) {
        if (professorRepository.count() == 0) {
            professorRepository.save(new Professor(null, "Manoella Azevedo", "1234567", "Informática", "manoella.azevedo@ifsul.edu.br"));
            professorRepository.save(new Professor(null, "Carlos Eduardo Martins", "2345678", "Matemática", "carlos.martins@ifsul.edu.br"));
            professorRepository.save(new Professor(null, "Juliana Ferreira Costa", "3456789", "Português", "juliana.costa@ifsul.edu.br"));
            professorRepository.save(new Professor(null, "Rafael Oliveira Santos", "4567890", "Física", "rafael.santos@ifsul.edu.br"));
            professorRepository.save(new Professor(null, "Patrícia Almeida Rocha", "5678901", "Química", "patricia.rocha@ifsul.edu.br"));
            professorRepository.save(new Professor(null, "Felipe Henrique Souza", "6789012", "História", "felipe.souza@ifsul.edu.br"));
        }
        if (alunoRepository.count() == 0) {
            alunoRepository.save(new Aluno(null, "Ana Souza", "2023001", "ana.souza@escola.edu.br", "Informática", "2023/1", LocalDate.of(2005, 3, 15)));
            alunoRepository.save(new Aluno(null, "Bruno Oliveira", "2023002", "bruno.oliveira@escola.edu.br", "Sistemas de Informação", "2023/2", LocalDate.of(2004, 7, 22)));
            alunoRepository.save(new Aluno(null, "Carlos Mendes", "2024003", "carlos.mendes@escola.edu.br", "Análise e Desenvolvimento de Sistemas", "2024/1", LocalDate.of(2005, 11, 10)));
            alunoRepository.save(new Aluno(null, "Daniela Ferreira", "2024004", "daniela.ferreira@escola.edu.br", "Informática", "2024/2", LocalDate.of(2006, 1, 3)));
            alunoRepository.save(new Aluno(null, "Eduardo Martins", "2024005", "eduardo.martins@escola.edu.br", "Redes de Computadores", "2024/1", LocalDate.of(2004, 9, 18)));
            alunoRepository.save(new Aluno(null, "Fernanda Alves", "2024006", "fernanda.alves@escola.edu.br", "Sistemas de Informação", "2024/2", LocalDate.of(2006, 5, 27)));
            alunoRepository.save(new Aluno(null, "Gabriel Costa", "2025007", "gabriel.costa@escola.edu.br", "Análise e Desenvolvimento de Sistemas", "2025/1", LocalDate.of(2005, 12, 12)));
            alunoRepository.save(new Aluno(null, "Helena Rodrigues", "2025008", "helena.rodrigues@escola.edu.br", "Informática", "2025/1", LocalDate.of(2007, 2, 8)));
        }
    }
}
