package rw.auca.edutech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rw.auca.edutech.model.Studio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudioRepository extends JpaRepository<Studio, UUID> {

    // D4 (DERIVED): find a studio by its number
    Optional<Studio> findByStudioNumber(int studioNumber);

    // C2 (JPQL): studios that no tutor occupies, sorted by studioNumber
    @Query("SELECT s FROM Studio s WHERE NOT EXISTS " +
            "(SELECT t FROM Tutor t WHERE t.studio = s) ORDER BY s.studioNumber")
    List<Studio> findFreeStudios();
}
