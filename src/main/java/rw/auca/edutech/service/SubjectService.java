package rw.auca.edutech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.auca.edutech.model.Subject;
import rw.auca.edutech.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubjectService {

    public static final String NOT_FOUND = "The subject with that id does not exist";

    @Autowired
    private SubjectRepository subjectRepository;

    public String save(Subject subject) {
        subjectRepository.save(subject);
        return "Subject saved successfully";
    }

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getById(UUID id) {
        return subjectRepository.findById(id);
    }

    public String update(UUID id, Subject newData) {
        Optional<Subject> found = subjectRepository.findById(id);
        if (found.isEmpty()) {
            return NOT_FOUND;
        }
        Subject subject = found.get();
        subject.setName(newData.getName());
        subjectRepository.save(subject);
        return "Subject updated successfully";
    }

    public String delete(UUID id) {
        if (!subjectRepository.existsById(id)) {
            return NOT_FOUND;
        }
        subjectRepository.deleteById(id);
        return "Subject deleted successfully";
    }

    // C3
    public List<Subject> getUnused() {
        return subjectRepository.findUnusedSubjects();
    }
}
