package rw.auca.edutech.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "assessment")
@Getter
@Setter
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String skill;
    private String comment;
    private int score;
    private LocalDate assessedDate;

    // many assessments belong to one lesson (Lesson has no list of assessments)
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
}
