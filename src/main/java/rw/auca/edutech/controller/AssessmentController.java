package rw.auca.edutech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.auca.edutech.model.Assessment;
import rw.auca.edutech.service.AssessmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    // E1
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID lessonId, @RequestBody Assessment assessment) {
        String message = assessmentService.save(lessonId, assessment);
        if (message.equals(AssessmentService.LESSON_NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.equals(AssessmentService.NOT_TAUGHT)) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Assessment>> getAll() {
        return new ResponseEntity<>(assessmentService.getAll(), HttpStatus.OK);
    }

    // E2
    @GetMapping("/learner/{learnerCode}")
    public ResponseEntity<List<Assessment>> getByLearner(@PathVariable String learnerCode) {
        return new ResponseEntity<>(assessmentService.getByLearnerCode(learnerCode), HttpStatus.OK);
    }

    // E3
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = assessmentService.delete(id);
        if (message.equals(AssessmentService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
