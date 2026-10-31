package rw.auca.edutech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.auca.edutech.model.Studio;
import rw.auca.edutech.repository.StudioRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudioService {

    public static final String NOT_FOUND = "The studio with that id does not exist";

    @Autowired
    private StudioRepository studioRepository;

    public String save(Studio studio) {
        studioRepository.save(studio);
        return "Studio saved successfully";
    }

    public List<Studio> getAll() {
        return studioRepository.findAll();
    }

    public Optional<Studio> getById(UUID id) {
        return studioRepository.findById(id);
    }

    public String update(UUID id, Studio newData) {
        Optional<Studio> found = studioRepository.findById(id);
        if (found.isEmpty()) {
            return NOT_FOUND;
        }
        Studio studio = found.get();
        studio.setStudioNumber(newData.getStudioNumber());
        studio.setName(newData.getName());
        studioRepository.save(studio);
        return "Studio updated successfully";
    }

    public String delete(UUID id) {
        if (!studioRepository.existsById(id)) {
            return NOT_FOUND;
        }
        studioRepository.deleteById(id);
        return "Studio deleted successfully";
    }

    // C2
    public List<Studio> getFree() {
        return studioRepository.findFreeStudios();
    }
}
