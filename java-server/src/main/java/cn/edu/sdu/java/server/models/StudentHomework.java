package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "student_homework")
public class StudentHomework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentHomeworkId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "homework_id")
    private Homework homework;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "person_id")
    Student student;


    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name=" photo", columnDefinition="longblob", nullable=true)
    private byte[] photo;
}
