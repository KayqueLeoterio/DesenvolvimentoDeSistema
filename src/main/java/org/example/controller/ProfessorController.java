package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Professor;
import org.example.service.ProfessorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professores") // <- Caminho definido no enunciado do trabalho
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<Professor>> listar(@RequestParam(required = false) String area) {
        return ResponseEntity.ok(professorService.listar(area));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(professorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Professor> cadastrar(@Valid @RequestBody Professor professor) {
        Professor criado = professorService.cadastrar(professor);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> atualizar(@PathVariable Long id, @Valid @RequestBody Professor professor) {
        Professor atualizado = professorService.atualizar(id, professor);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        professorService.remover(id);
        return ResponseEntity.noContent().build();
    }

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }
}
