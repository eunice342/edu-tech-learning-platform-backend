package rw.auca.edutech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.auca.edutech.model.Studio;
import rw.auca.edutech.model.Subject;
import rw.auca.edutech.model.Tutor;
import rw.auca.edutech.repository.StudioRepository;
import rw.auca.edutech.repository.SubjectRepository;
import rw.auca.edutech.repository.TutorRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TutorService {

    public static final String NOT_FOUND = "The tutor with that id does not exist";
    public static final String SUBJECT_NOT_FOUND = "That subject does not exist";
    public static final String ALREADY_HAS_SUBJECT = "The tutor already has that subject";
    public static final String STUDIO_NOT_FOUND = "The studio with that studioNumber does not exist";
    public static final String STUDIO_TAKEN = "That studio is taken by another tutor";

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudioRepository studioRepository;

    public String save(Tutor tutor) {
        tutorRepository.save(tutor);
        return "Tutor saved successfully";
    }

    public List<Tutor> getAll() {
        return tutorRepository.findAll();
    }

    public Optional<Tutor> getById(UUID id) {
        return tutorRepository.findById(id);
    }

    // The general update changes names, email and studio only.
    // Subjects are managed by D3 so a PUT without subjects never wipes them.
    public String update(UUID id, Tutor newData) {
        Optional<Tutor> found = tutorRepository.findById(id);
        if (found.isEmpty()) {
            return NOT_FOUND;
        }
        Tutor tutor = found.get();
        tutor.setFirstName(newData.getFirstName());
        tutor.setLastName(newData.getLastName());
        tutor.setEmail(newData.getEmail());
        if (newData.getStudio() != null) {
            tutor.setStudio(newData.getStudio());
        }
        tutorRepository.save(tutor);
        return "Tutor updated successfully";
    }

    public String delete(UUID id) {
        if (!tutorRepository.existsById(id)) {
            return NOT_FOUND;
        }
        tutorRepository.deleteById(id);
        return "Tutor deleted successfully";
    }

    // C1
    public List<Tutor> getBySubject(String name) {
        return tutorRepository.findBySubjectName(name);
    }

    // D3: add one subject to a tutor and keep the ones already there
    @Transactional
    public String addSubject(UUID tutorId, UUID subjectId) {
        Optional<Tutor> foundTutor = tutorRepository.findById(tutorId);
        if (foundTutor.isEmpty()) {
            return NOT_FOUND;
        }
        Optional<Subject> foundSubject = subjectRepository.findById(subjectId);
        if (foundSubject.isEmpty()) {
            return SUBJECT_NOT_FOUND;
        }
        Tutor tutor = foundTutor.get();
        Subject subject = foundSubject.get();

        // compare by id: the entities have no equals(), so contains(subject) could miss it
        boolean alreadyHas = tutor.getSubjects().stream()
                .anyMatch(s -> s.getId().equals(subject.getId()));
        if (alreadyHas) {
            return ALREADY_HAS_SUBJECT;
        }

        tutor.getSubjects().add(subject);
        tutorRepository.save(tutor);   // Tutor owns the join table, so we save the Tutor
        return "Subject added to the tutor";
    }

    // D4: move a tutor to another studio
    public String changeStudio(UUID tutorId, int studioNumber) {
        Optional<Tutor> foundTutor = tutorRepository.findById(tutorId);
        if (foundTutor.isEmpty()) {
            return NOT_FOUND;
        }
        Optional<Studio> foundStudio = studioRepository.findByStudioNumber(studioNumber);
        if (foundStudio.isEmpty()) {
            return STUDIO_NOT_FOUND;
        }
        Tutor tutor = foundTutor.get();
        Studio studio = foundStudio.get();

        boolean alreadyHisStudio = tutor.getStudio() != null
                && tutor.getStudio().getId().equals(studio.getId());
        if (!alreadyHisStudio && tutorRepository.existsByStudio(studio)) {
            return STUDIO_TAKEN;
        }

        tutor.setStudio(studio);
        tutorRepository.save(tutor);
        return "Studio changed successfully";
    }
}
