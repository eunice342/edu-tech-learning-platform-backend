package rw.auca.edutech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.auca.edutech.model.Lesson;
import rw.auca.edutech.model.LessonStatus;
import rw.auca.edutech.service.LessonService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    // A2 + D1
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Lesson lesson) {
        String message = lessonService.save(lesson);
        if (message.equals(LessonService.PAST_DATE)) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } else if (message.equals(LessonService.TUTOR_BUSY)) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Lesson>> getAll() {
        return new ResponseEntity<>(lessonService.getAll(), HttpStatus.OK);
    }

    // B2
    @GetMapping("/by-status")
    public ResponseEntity<List<Lesson>> getByStatus(@RequestParam LessonStatus status) {
        return new ResponseEntity<>(lessonService.getByStatus(status), HttpStatus.OK);
    }

    // B3: the Strings are parsed into LocalDate here in the controller
    @GetMapping("/between")
    public ResponseEntity<List<Lesson>> getBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(lessonService.getBetween(startDate, endDate), HttpStatus.OK);
    }

    // F1
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getStatsByStatus() {
        return new ResponseEntity<>(lessonService.getStatsByStatus(), HttpStatus.OK);
    }

    // Bonus: the Pageable is built here from the three parameters
    @GetMapping("/page")
    public ResponseEntity<Page<Lesson>> getPage(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size,
                                                @RequestParam(defaultValue = "lessonDate,desc") String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, field));
        return new ResponseEntity<>(lessonService.getPage(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<Lesson> lesson = lessonService.getById(id);
        if (lesson.isPresent()) {
            return new ResponseEntity<>(lesson.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(LessonService.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Lesson lesson) {
        String message = lessonService.update(id, lesson);
        if (message.equals(LessonService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // A2 + E3
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = lessonService.delete(id);
        if (message.equals(LessonService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.equals(LessonService.HAS_ASSESSMENTS)) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // D2
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam LessonStatus status) {
        String message = lessonService.updateStatus(id, status);
        if (message.equals(LessonService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.startsWith(LessonService.BAD_STATUS_PREFIX)) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // F2
    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID tutorId, @RequestParam String date) {
        String message = lessonService.cancelDay(tutorId, LocalDate.parse(date));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
