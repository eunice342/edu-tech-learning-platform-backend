package rw.auca.edutech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.edutech.model.Lesson;
import rw.auca.edutech.model.LessonStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    // B2 (DERIVED): lessons in a status, earliest date first
    List<Lesson> findByStatusOrderByLessonDateAsc(LessonStatus status);

    // B3 (DERIVED): lessons between two dates (both inclusive), ordered by date
    List<Lesson> findByLessonDateBetweenOrderByLessonDateAsc(LocalDate start, LocalDate end);

    // D1 (DERIVED): does the tutor already have a lesson that day which is not DECLINED?
    boolean existsByTutorIdAndLessonDateAndStatusNot(UUID tutorId, LocalDate lessonDate, LessonStatus status);

    // F1 (JPQL): one row per status with its count (statuses with 0 lessons do not appear)
    @Query("SELECT e.status, COUNT(e) FROM Lesson e GROUP BY e.status")
    List<Object[]> countLessonsByStatus();

    // F2 (JPQL UPDATE): decline every lesson of the tutor on that date, except TAUGHT ones
    @Modifying
    @Query("UPDATE Lesson e SET e.status = :declined " +
            "WHERE e.tutor.id = :tutorId AND e.lessonDate = :date AND e.status <> :taught")
    int cancelDay(@Param("tutorId") UUID tutorId,
                  @Param("date") LocalDate date,
                  @Param("declined") LessonStatus declined,
                  @Param("taught") LessonStatus taught);

    // Bonus dashboard (DERIVED countBy)
    long countByStatus(LessonStatus status);

    long countByLessonDate(LocalDate lessonDate);
}
