package rw.auca.edutech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rw.auca.edutech.model.Subject;

import java.util.List;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    // C3 (JPQL): subjects that no tutor holds - IS EMPTY on the tutors collection
    @Query("SELECT s FROM Subject s WHERE s.tutors IS EMPTY")
    List<Subject> findUnusedSubjects();
}
