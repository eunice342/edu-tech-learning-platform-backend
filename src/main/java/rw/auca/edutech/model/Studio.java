package rw.auca.edutech.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "studio")
@Getter
@Setter
public class Studio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int studioNumber;

    private String name;
}
