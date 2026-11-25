package org.example.repository;

import org.example.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Page<Professor> findByAreaIgnoreCase(String area, Pageable pageable);

    boolean existsBySiapeIgnoreCase(String siape);

    boolean existsBySiapeIgnoreCaseAndIdNot(String siape, Long id);
}
