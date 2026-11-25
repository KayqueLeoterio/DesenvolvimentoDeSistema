package org.example.service;

import org.example.client.TurmasClient;
import org.example.exception.ConflitoException;
import org.example.exception.ProfessorInvalidoException;
import org.example.exception.ProfessorNaoEncontradoException;
import org.example.model.Professor;
import org.example.repository.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final TurmasClient turmasClient;

    public ProfessorService(ProfessorRepository professorRepository, TurmasClient turmasClient) {
        this.professorRepository = professorRepository;
        this.turmasClient = turmasClient;
    }

    public Page<Professor> listar(String area, Pageable pageable) {
        if (area != null && !area.isBlank()) {
            return professorRepository.findByAreaIgnoreCase(area.trim(), pageable);
        }
        return professorRepository.findAll(pageable);
    }

    public Professor buscarPorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Professor com id " + id + " não encontrado"));
    }

    public Professor cadastrar(Professor professor) {
        if (professorRepository.existsBySiapeIgnoreCase(professor.getSiape())) {
            throw new ProfessorInvalidoException("Já existe um professor cadastrado com o SIAPE " + professor.getSiape());
        }
        professor.setId(null);
        return professorRepository.save(professor);
    }

    public Professor atualizar(Long id, Professor professorAtualizado) {
        buscarPorId(id);
        if (professorRepository.existsBySiapeIgnoreCaseAndIdNot(professorAtualizado.getSiape(), id)) {
            throw new ProfessorInvalidoException("Já existe um professor cadastrado com o SIAPE " + professorAtualizado.getSiape());
        }
        professorAtualizado.setId(id);
        return professorRepository.save(professorAtualizado);
    }

    public void remover(Long id) {
        buscarPorId(id); // 404 se não existir
        if (!turmasClient.listarTurmasDoProfessor(id).isEmpty()) { // 503 se Turmas falhar
            throw new ConflitoException("Professor com id " + id + " é responsável por turma(s) e não pode ser removido");
        }
        professorRepository.deleteById(id);
    }

    public List<Object> listarTurmas(Long id) {
        buscarPorId(id); // 404 se o professor não existir
        return turmasClient.listarTurmasDoProfessor(id);
    }
}
