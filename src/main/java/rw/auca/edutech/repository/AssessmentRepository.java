package rw.auca.edutech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.edutech.model.Assessment;
import rw.auca.edutech.model.Lesson;

import java.util.List;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    // E2 (JPQL): all assessments of a learner, newest first (navigate lesson -> learner)
    @Query("SELECT a FROM Assessment a WHERE a.lesson.learner.learnerCode = :learnerCode " +
            "ORDER BY a.assessedDate DESC")
    List<Assessment> findByLearnerCode(@Param("learnerCode") String learnerCode);

    // E3 (DERIVED): does this lesson have assessments?
    boolean existsByLesson(Lesson lesson);
}
