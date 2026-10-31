package rw.auca.edutech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.auca.edutech.model.Learner;
import rw.auca.edutech.service.LearnerService;
import rw.auca.edutech.service.TutorService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/learners")
public class LearnerController {

    @Autowired
    private LearnerService learnerService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Learner learner) {
        return new ResponseEntity<>(learnerService.save(learner), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Learner>> getAll() {
        return new ResponseEntity<>(learnerService.getAll(), HttpStatus.OK);
    }

    // B1
    @GetMapping("/by-school")
    public ResponseEntity<List<Learner>> getBySchool(@RequestParam String school) {
        return new ResponseEntity<>(learnerService.getBySchool(school), HttpStatus.OK);
    }

    // C4
    @GetMapping("/of-tutor/{tutorId}")
    public ResponseEntity<?> getLearnersOfTutor(@PathVariable UUID tutorId) {
        Optional<List<Learner>> learners = learnerService.getLearnersOfTutor(tutorId);
        if (learners.isPresent()) {
            return new ResponseEntity<>(learners.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(TutorService.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<Learner> learner = learnerService.getById(id);
        if (learner.isPresent()) {
            return new ResponseEntity<>(learner.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(LearnerService.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Learner learner) {
        String message = learnerService.update(id, learner);
        if (message.equals(LearnerService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = learnerService.delete(id);
        if (message.equals(LearnerService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
