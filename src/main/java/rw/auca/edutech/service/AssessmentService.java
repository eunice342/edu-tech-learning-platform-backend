package rw.auca.edutech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.auca.edutech.model.Assessment;
import rw.auca.edutech.model.Lesson;
import rw.auca.edutech.model.LessonStatus;
import rw.auca.edutech.repository.AssessmentRepository;
import rw.auca.edutech.repository.LessonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssessmentService {

    public static final String LESSON_NOT_FOUND = "The lesson with that id does not exist";
    public static final String NOT_TAUGHT = "Assessments are only for TAUGHT lessons";
    public static final String NOT_FOUND = "The assessment with that id does not exist";

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    // E1
    public String save(UUID lessonId, Assessment assessment) {
        Optional<Lesson> found = lessonRepository.findById(lessonId);
        if (found.isEmpty()) {
            return LESSON_NOT_FOUND;
        }
        Lesson lesson = found.get();
        if (lesson.getStatus() != LessonStatus.TAUGHT) {
            return NOT_TAUGHT;
        }
        assessment.setAssessedDate(LocalDate.now());   // set by the service
        assessment.setLesson(lesson);                  // link the lesson
        assessmentRepository.save(assessment);
        return "Assessment saved successfully";
    }

    public List<Assessment> getAll() {
        return assessmentRepository.findAll();
    }

    // E2
    public List<Assessment> getByLearnerCode(String learnerCode) {
        return assessmentRepository.findByLearnerCode(learnerCode);
    }

    // E3
    public String delete(UUID id) {
        if (!assessmentRepository.existsById(id)) {
            return NOT_FOUND;
        }
        assessmentRepository.deleteById(id);
        return "Assessment deleted successfully";
    }
}
