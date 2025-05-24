package cn.edu.sdu.java.server.models;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
* 学生选课多对多关系中间关系表
* 记录选课时间
* */

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "student_course")
public class StudentCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentCourseId;

    @ManyToOne(fetch = FetchType.LAZY)
    //@MapsId("personId")
    @JoinColumn(name = "person_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    //@MapsId("courseExId")
    @JoinColumn(name = "course_ex_id")
    private CourseEx courseEx;

    private LocalDateTime selectedTime;
}