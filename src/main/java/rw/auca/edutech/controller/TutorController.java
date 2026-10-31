package rw.auca.edutech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.auca.edutech.model.Tutor;
import rw.auca.edutech.service.TutorService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Tutor tutor) {
        return new ResponseEntity<>(tutorService.save(tutor), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Tutor>> getAll() {
        return new ResponseEntity<>(tutorService.getAll(), HttpStatus.OK);
    }

    // C1
    @GetMapping("/by-subject")
    public ResponseEntity<List<Tutor>> getBySubject(@RequestParam String name) {
        return new ResponseEntity<>(tutorService.getBySubject(name), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<Tutor> tutor = tutorService.getById(id);
        if (tutor.isPresent()) {
            return new ResponseEntity<>(tutor.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(TutorService.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Tutor tutor) {
        String message = tutorService.update(id, tutor);
        if (message.equals(TutorService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = tutorService.delete(id);
        if (message.equals(TutorService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // D3
    @PutMapping("/{tutorId}/subjects/{subjectId}")
    public ResponseEntity<String> addSubject(@PathVariable UUID tutorId, @PathVariable UUID subjectId) {
        String message = tutorService.addSubject(tutorId, subjectId);
        if (message.equals(TutorService.NOT_FOUND) || message.equals(TutorService.SUBJECT_NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.equals(TutorService.ALREADY_HAS_SUBJECT)) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // D4
    @PatchMapping("/{tutorId}/studio")
    public ResponseEntity<String> changeStudio(@PathVariable UUID tutorId, @RequestParam int studioNumber) {
        String message = tutorService.changeStudio(tutorId, studioNumber);
        if (message.equals(TutorService.NOT_FOUND) || message.equals(TutorService.STUDIO_NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.equals(TutorService.STUDIO_TAKEN)) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
