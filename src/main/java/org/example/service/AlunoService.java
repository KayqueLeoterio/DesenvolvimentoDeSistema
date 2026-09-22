package org.example.service;

import org.example.exception.AlunoNaoEncontradoException;
import org.example.model.Aluno;
import org.example.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public List<Aluno> listar() {
        return alunoRepository.listarTodos();
    }

    public Aluno buscarPorId(Long id) {
        return alunoRepository.buscarPorId(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno com id " + id + " não encontrado"));
    }

    public Aluno cadastrar(Aluno aluno) {
        validarMatriculaUnica(aluno.getMatricula(), null);
        return alunoRepository.salvar(aluno);
    }

    public Aluno atualizar(Long id, Aluno alunoAtualizado) {
        buscarPorId(id); // garante 404 se não existir
        validarMatriculaUnica(alunoAtualizado.getMatricula(), id);
        return alunoRepository.atualizar(id, alunoAtualizado)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno com id " + id + " não encontrado"));
    }

    public void remover(Long id) {
        boolean removeu = alunoRepository.remover(id);
        if (!removeu) {
            throw new AlunoNaoEncontradoException("Aluno com id " + id + " não encontrado");
        }
    }


    private void validarMatriculaUnica(String matricula, Long idParaIgnorar) {
        boolean duplicada = alunoRepository.listarTodos().stream()
                .anyMatch(a -> a.getMatricula().equalsIgnoreCase(matricula)
                        && !a.getId().equals(idParaIgnorar));
        if (duplicada) {
            throw new AlunoNaoEncontradoException("Já existe um aluno cadastrado com a matrícula " + matricula);
        }
    }

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }


}
