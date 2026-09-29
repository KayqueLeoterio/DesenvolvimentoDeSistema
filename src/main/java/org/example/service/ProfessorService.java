package org.example.service;

import org.example.exception.ProfessorInvalidoException;
import org.example.exception.ProfessorNaoEncontradoException;
import org.example.model.Professor;
import org.example.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public List<Professor> listar(String area) {
        List<Professor> professores = professorRepository.listarTodos();

        // Aplica o filtro de query string se o parâmetro foi enviado
        if (area != null && !area.isBlank()) {
            return professores.stream()
                    .filter(p -> p.getArea().equalsIgnoreCase(area))
                    .toList();
        }

        return professores;
    }

    public Professor buscarPorId(Long id) {
        return professorRepository.buscarPorId(id)
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Professor com id " + id + " não encontrado"));
    }

    public Professor cadastrar(Professor professor) {
        validarSiapeUnico(professor.getSiape(), null);
        return professorRepository.salvar(professor);
    }

    public Professor atualizar(Long id, Professor professorAtualizado) {
        buscarPorId(id); // Garante 404 se não existir
        validarSiapeUnico(professorAtualizado.getSiape(), id);
        return professorRepository.atualizar(id, professorAtualizado)
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Professor com id " + id + " não encontrado"));
    }

    public void remover(Long id) {
        boolean removeu = professorRepository.remover(id);
        if (!removeu) {
            throw new ProfessorNaoEncontradoException("Professor com id " + id + " não encontrado");
        }
    }

    private void validarSiapeUnico(String siape, Long idParaIgnorar) {
        boolean duplicada = professorRepository.listarTodos().stream()
                .anyMatch(p -> p.getSiape().equalsIgnoreCase(siape)
                        && !p.getId().equals(idParaIgnorar));
        if (duplicada) {
            throw new ProfessorInvalidoException("Já existe um professor cadastrado com o SIAPE " + siape);
        }
    }

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }
}
