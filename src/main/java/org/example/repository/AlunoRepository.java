package org.example.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.example.model.Aluno;
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
public class AlunoRepository {

    private static final Path ARQUIVO = Path.of("dados", "alunos.json");

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private List<Aluno> alunos = new ArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    @PostConstruct
    public void carregar() {
        try {
            Files.createDirectories(ARQUIVO.getParent());
            File arquivo = ARQUIVO.toFile();
            if (arquivo.exists() && arquivo.length() > 0) {
                Aluno[] carregados = objectMapper.readValue(arquivo, Aluno[].class);
                alunos = new ArrayList<>(List.of(carregados));
            } else {
                salvarEmDisco();
            }
            long maiorId = alunos.stream().mapToLong(Aluno::getId).max().orElse(0);
            proximoId.set(maiorId + 1);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o arquivo de alunos", e);
        }
    }

    public List<Aluno> listarTodos() {
        return List.copyOf(alunos);
    }

    public Optional<Aluno> buscarPorId(Long id) {
        return alunos.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public Aluno salvar(Aluno aluno) {
        aluno.setId(proximoId.getAndIncrement());
        alunos.add(aluno);
        salvarEmDisco();
        return aluno;
    }

    public Optional<Aluno> atualizar(Long id, Aluno alunoAtualizado) {
        for (int i = 0; i < alunos.size(); i++) {
            if (alunos.get(i).getId().equals(id)) {
                alunoAtualizado.setId(id);
                alunos.set(i, alunoAtualizado);
                salvarEmDisco();
                return Optional.of(alunoAtualizado);
            }
        }
        return Optional.empty();
    }

    public boolean remover(Long id) {
        boolean removeu = alunos.removeIf(a -> a.getId().equals(id));
        if (removeu) {
            salvarEmDisco();
        }
        return removeu;
    }

    private void salvarEmDisco() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(ARQUIVO.toFile(), alunos);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gravar o arquivo de alunos", e);
        }
    }

}
