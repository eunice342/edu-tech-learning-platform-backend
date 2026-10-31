package rw.auca.edutech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.auca.edutech.model.Lesson;
import rw.auca.edutech.model.LessonStatus;
import rw.auca.edutech.repository.AssessmentRepository;
import rw.auca.edutech.repository.LessonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonService {

    public static final String NOT_FOUND = "The lesson with that id does not exist";
    public static final String PAST_DATE = "Lesson date cannot be in the past";
    public static final String TUTOR_BUSY = "Tutor already has a lesson on that date";
    public static final String HAS_ASSESSMENTS = "That lesson has assessments, delete them first";
    public static final String BAD_STATUS_PREFIX = "Cannot change status from";

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    // D1: safe saving
    public String save(Lesson lesson) {
        if (lesson.getLessonDate() != null && lesson.getLessonDate().isBefore(LocalDate.now())) {
            return PAST_DATE;
        }
        if (lesson.getTutor() != null && lesson.getTutor().getId() != null) {
            boolean busy = lessonRepository.existsByTutorIdAndLessonDateAndStatusNot(
                    lesson.getTutor().getId(), lesson.getLessonDate(), LessonStatus.DECLINED);
            if (busy) {
                return TUTOR_BUSY;
            }
        }
        lesson.setStatus(LessonStatus.REQUESTED);   // always starts as REQUESTED
        lessonRepository.save(lesson);
        return "Lesson saved successfully";
    }

    public List<Lesson> getAll() {
        return lessonRepository.findAll();
    }

    public Optional<Lesson> getById(UUID id) {
        return lessonRepository.findById(id);
    }

    // D2: the general update does NOT touch the status any more
    public String update(UUID id, Lesson newData) {
        Optional<Lesson> found = lessonRepository.findById(id);
        if (found.isEmpty()) {
            return NOT_FOUND;
        }
        Lesson lesson = found.get();
        lesson.setLessonDate(newData.getLessonDate());
        lesson.setTopic(newData.getTopic());
        lesson.setDurationMinutes(newData.getDurationMinutes());
        if (newData.getLearner() != null) {
            lesson.setLearner(newData.getLearner());
        }
        if (newData.getTutor() != null) {
            lesson.setTutor(newData.getTutor());
        }
        lessonRepository.save(lesson);
        return "Lesson updated successfully";
    }

    // E3: a lesson with assessments cannot be deleted
    public String delete(UUID id) {
        Optional<Lesson> found = lessonRepository.findById(id);
        if (found.isEmpty()) {
            return NOT_FOUND;
        }
        if (assessmentRepository.existsByLesson(found.get())) {
            return HAS_ASSESSMENTS;
        }
        lessonRepository.deleteById(id);
        return "Lesson deleted successfully";
    }

    // B2
    public List<Lesson> getByStatus(LessonStatus status) {
        return lessonRepository.findByStatusOrderByLessonDateAsc(status);
    }

    // B3
    public List<Lesson> getBetween(LocalDate start, LocalDate end) {
        return lessonRepository.findByLessonDateBetweenOrderByLessonDateAsc(start, end);
    }

    // D2: status flow  REQUESTED -> ACCEPTED/DECLINED, ACCEPTED -> TAUGHT/DECLINED, others final
    public String updateStatus(UUID id, LessonStatus newStatus) {
        Optional<Lesson> found = lessonRepository.findById(id);
        if (found.isEmpty()) {
            return NOT_FOUND;
        }
        Lesson lesson = found.get();
        LessonStatus current = lesson.getStatus();

        boolean allowed = false;
        if (current == LessonStatus.REQUESTED
                && (newStatus == LessonStatus.ACCEPTED || newStatus == LessonStatus.DECLINED)) {
            allowed = true;
        } else if (current == LessonStatus.ACCEPTED
                && (newStatus == LessonStatus.TAUGHT || newStatus == LessonStatus.DECLINED)) {
            allowed = true;
        }

        if (!allowed) {
            return BAD_STATUS_PREFIX + " " + current + " to " + newStatus;
        }

        lesson.setStatus(newStatus);
        lessonRepository.save(lesson);
        return "Lesson status updated successfully";
    }

    // F1
    public List<Object[]> getStatsByStatus() {
        return lessonRepository.countLessonsByStatus();
    }

    // F2: one UPDATE query, no Java loop
    @Transactional
    public String cancelDay(UUID tutorId, LocalDate date) {
        int count = lessonRepository.cancelDay(tutorId, date, LessonStatus.DECLINED, LessonStatus.TAUGHT);
        return count + " lessons cancelled";
    }

    // Bonus: paging
    public Page<Lesson> getPage(Pageable pageable) {
        return lessonRepository.findAll(pageable);
    }
}
