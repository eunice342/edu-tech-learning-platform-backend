package rw.auca.edutech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.auca.edutech.model.Learner;
import rw.auca.edutech.repository.LearnerRepository;
import rw.auca.edutech.repository.TutorRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LearnerService {

    public static final String NOT_FOUND = "The learner with that id does not exist";

    @Autowired
    private LearnerRepository learnerRepository;

    @Autowired
    private TutorRepository tutorRepository;

    public String save(Learner learner) {
        learnerRepository.save(learner);
        return "Learner saved successfully";
    }

    public List<Learner> getAll() {
        return learnerRepository.findAll();
    }

    public Optional<Learner> getById(UUID id) {
        return learnerRepository.findById(id);
    }

    public String update(UUID id, Learner newData) {
        Optional<Learner> found = learnerRepository.findById(id);
        if (found.isEmpty()) {
            return NOT_FOUND;
        }
        Learner learner = found.get();
        learner.setFirstName(newData.getFirstName());
        learner.setLastName(newData.getLastName());
        learner.setLearnerCode(newData.getLearnerCode());
        learner.setSchool(newData.getSchool());
        learner.setEmail(newData.getEmail());
        learnerRepository.save(learner);
        return "Learner updated successfully";
    }

    public String delete(UUID id) {
        if (!learnerRepository.existsById(id)) {
            return NOT_FOUND;
        }
        learnerRepository.deleteById(id);
        return "Learner deleted successfully";
    }

    // B1
    public List<Learner> getBySchool(String school) {
        return learnerRepository.findBySchoolIgnoreCaseOrderByLastNameAsc(school);
    }

    // C4: empty Optional means "the tutor does not exist"
    public Optional<List<Learner>> getLearnersOfTutor(UUID tutorId) {
        if (!tutorRepository.existsById(tutorId)) {
            return Optional.empty();
        }
        return Optional.of(learnerRepository.findLearnersOfTutor(tutorId));
    }
}
