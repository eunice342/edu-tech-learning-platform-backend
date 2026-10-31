package rw.auca.edutech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.auca.edutech.model.Studio;
import rw.auca.edutech.service.StudioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/studios")
public class StudioController {

    @Autowired
    private StudioService studioService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Studio studio) {
        return new ResponseEntity<>(studioService.save(studio), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Studio>> getAll() {
        return new ResponseEntity<>(studioService.getAll(), HttpStatus.OK);
    }

    // C2
    @GetMapping("/free")
    public ResponseEntity<List<Studio>> getFree() {
        return new ResponseEntity<>(studioService.getFree(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<Studio> studio = studioService.getById(id);
        if (studio.isPresent()) {
            return new ResponseEntity<>(studio.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(StudioService.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Studio studio) {
        String message = studioService.update(id, studio);
        if (message.equals(StudioService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = studioService.delete(id);
        if (message.equals(StudioService.NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
