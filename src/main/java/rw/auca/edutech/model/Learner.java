package rw.auca.edutech.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "learner")
@Getter
@Setter
public class Learner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;

    // business key of a learner
    @Column(unique = true)
    private String learnerCode;

    private String school;
    private String email;
}
