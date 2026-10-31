package rw.auca.edutech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subject")
@Getter
@Setter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    // inverse side: the Tutor entity owns the join table.
    // @JsonIgnore stops the endless loop Subject -> Tutor -> Subject.
    @ManyToMany(mappedBy = "subjects")
    @JsonIgnore
    private List<Tutor> tutors;
}
