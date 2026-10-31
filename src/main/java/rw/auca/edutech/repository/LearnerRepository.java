package rw.auca.edutech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.edutech.model.Learner;

import java.util.List;
import java.util.UUID;

public interface LearnerRepository extends JpaRepository<Learner, UUID> {

    // B1 (DERIVED): by school, ignore case, sorted by lastName A to Z
    List<Learner> findBySchoolIgnoreCaseOrderByLastNameAsc(String school);

    // C4 (JPQL): learners having at least one lesson with the tutor, listed once
    @Query("SELECT DISTINCT e.learner FROM Lesson e WHERE e.tutor.id = :tutorId")
    List<Learner> findLearnersOfTutor(@Param("tutorId") UUID tutorId);
}
