package org.example.service;

import org.example.client.MatriculasClient;
import org.example.exception.AlunoInvalidoException;
import org.example.exception.AlunoNaoEncontradoException;
import org.example.exception.ConflitoException;
import org.example.model.Aluno;
import org.example.repository.AlunoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculasClient matriculasClient;

    public AlunoService(AlunoRepository alunoRepository, MatriculasClient matriculasClient) {
        this.alunoRepository = alunoRepository;
        this.matriculasClient = matriculasClient;
    }

    public Page<Aluno> listar(Pageable pageable) {
        return alunoRepository.findAll(pageable);
    }

    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno com id " + id + " não encontrado"));
    }

    public Aluno cadastrar(Aluno aluno) {
        if (alunoRepository.existsByMatriculaIgnoreCase(aluno.getMatricula())) {
            throw new AlunoInvalidoException("Já existe um aluno cadastrado com a matrícula " + aluno.getMatricula());
        }
        aluno.setId(null); // o id é sempre gerado pelo banco
        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(Long id, Aluno alunoAtualizado) {
        buscarPorId(id); // garante 404 se não existir
        if (alunoRepository.existsByMatriculaIgnoreCaseAndIdNot(alunoAtualizado.getMatricula(), id)) {
            throw new AlunoInvalidoException("Já existe um aluno cadastrado com a matrícula " + alunoAtualizado.getMatricula());
        }
        alunoAtualizado.setId(id);
        return alunoRepository.save(alunoAtualizado);
    }

    public void remover(Long id) {
        buscarPorId(id); // 404 se não existir
        if (matriculasClient.alunoPossuiMatriculaAtiva(id)) { // 503 se Matrículas falhar
            throw new ConflitoException("Aluno com id " + id + " possui matrícula ativa e não pode ser removido");
        }
        alunoRepository.deleteById(id);
    }
}
