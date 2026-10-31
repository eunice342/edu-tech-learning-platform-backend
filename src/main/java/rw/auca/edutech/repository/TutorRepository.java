package rw.auca.edutech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.edutech.model.Studio;
import rw.auca.edutech.model.Tutor;

import java.util.List;
import java.util.UUID;

public interface TutorRepository extends JpaRepository<Tutor, UUID> {

    // C1 (JPQL): tutors holding a subject, ignoring letter case.
    // JOIN through the many-to-many list; "math" matches "Mathematics".
    @Query("SELECT DISTINCT t FROM Tutor t JOIN t.subjects s " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tutor> findBySubjectName(@Param("name") String name);

    // D4 (DERIVED): is this studio already used by a tutor?
    boolean existsByStudio(Studio studio);
}
