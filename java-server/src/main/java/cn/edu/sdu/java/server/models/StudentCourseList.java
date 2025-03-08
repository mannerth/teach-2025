package cn.edu.sdu.java.server.models;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(	name = "student_course_list",
        uniqueConstraints = {
        })

public class StudentCourseList {
    @Id
    private Integer personId;

//    @OneToOne
//    @JoinColumn(name="personId")
//    @JsonIgnore
//    private Person person;

    @Size(max = 20000)
    private String courseList;

}
