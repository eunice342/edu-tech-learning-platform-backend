package rw.auca.edutech.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tutor")
@Getter
@Setter
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;

    // a tutor is assigned to one studio (may be null)
    @ManyToOne
    @JoinColumn(name = "studio_id")
    private Studio studio;

    // owning side of the many-to-many relation
    @ManyToMany
    @JoinTable(name = "tutor_subject",
            joinColumns = @JoinColumn(name = "tutor_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private List<Subject> subjects = new ArrayList<>();
}
