package org.example.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.example.model.Professor;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProfessorRepository {

    private static final Path ARQUIVO = Path.of("dados", "professores.json");

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private List<Professor> professores = new ArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    @PostConstruct
    public void carregar() {
        try {
            Files.createDirectories(ARQUIVO.getParent());
            File arquivo = ARQUIVO.toFile();
            if (arquivo.exists() && arquivo.length() > 0) {
                Professor[] carregados = objectMapper.readValue(arquivo, Professor[].class);
                professores = new ArrayList<>(List.of(carregados));
            } else {
                salvarEmDisco();
            }
            long maiorId = professores.stream().mapToLong(Professor::getId).max().orElse(0);
            proximoId.set(maiorId + 1);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o arquivo de professores", e);
        }
    }

    public List<Professor> listarTodos() {
        return List.copyOf(professores);
    }

    public Optional<Professor> buscarPorId(Long id) {
        return professores.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Professor salvar(Professor professor) {
        professor.setId(proximoId.getAndIncrement());
        professores.add(professor);
        salvarEmDisco();
        return professor;
    }

    public Optional<Professor> atualizar(Long id, Professor professorAtualizado) {
        for (int i = 0; i < professores.size(); i++) {
            if (professores.get(i).getId().equals(id)) {
                professorAtualizado.setId(id);
                professores.set(i, professorAtualizado);
                salvarEmDisco();
                return Optional.of(professorAtualizado);
            }
        }
        return Optional.empty();
    }

    public boolean remover(Long id) {
        boolean removeu = professores.removeIf(p -> p.getId().equals(id));
        if (removeu) {
            salvarEmDisco();
        }
        return removeu;
    }

    private void salvarEmDisco() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(ARQUIVO.toFile(), professores);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gravar o arquivo de professores", e);
        }
    }
}
