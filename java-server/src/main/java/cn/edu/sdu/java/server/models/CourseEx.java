package cn.edu.sdu.java.server.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
@Getter
@Setter
@Entity
@EqualsAndHashCode
@Table(name = "CourseEx"
)
public class CourseEx {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseExId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;


    @Size(max = 200)
    private String course_num; //课序号

    private Boolean is_choosable; // 是否可选
    private Integer max_stu_num;

    @Size(max = 100)
    private String place;

    @Size(max = 300)
    private String time_inf;

    @Size(max = 300)
    private String information;

    @OneToMany(mappedBy = "courseEx", cascade = CascadeType.ALL)
    private List<StudentCourse> studentCourses = new ArrayList<>();

}
