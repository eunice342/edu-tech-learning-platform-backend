package rw.auca.edutech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.auca.edutech.model.LessonStatus;
import rw.auca.edutech.repository.AssessmentRepository;
import rw.auca.edutech.repository.LearnerRepository;
import rw.auca.edutech.repository.LessonRepository;
import rw.auca.edutech.repository.StudioRepository;
import rw.auca.edutech.repository.TutorRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired private LearnerRepository learnerRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private StudioRepository studioRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private AssessmentRepository assessmentRepository;

    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();   // keeps the order
        summary.put("learners", learnerRepository.count());
        summary.put("tutors", tutorRepository.count());
        summary.put("freeStudios", (long) studioRepository.findFreeStudios().size());   // reuse C2
        summary.put("lessonsToday", lessonRepository.countByLessonDate(LocalDate.now()));
        summary.put("requested", lessonRepository.countByStatus(LessonStatus.REQUESTED));
        summary.put("assessments", assessmentRepository.count());
        return summary;
    }
}
