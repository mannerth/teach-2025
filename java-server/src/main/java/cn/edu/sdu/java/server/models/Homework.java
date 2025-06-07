package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
/*
 * Homework 作业表实体类  保存作业的基本信息，
 * Integer homeworkId homework 主键 homework_id
 * Course course 关联课程 course_id 关联课程的主键 course_id
 * String content 作业内容
 * Date releasingTime 作业发布时间
 * Date deadline 作业截止时间
 */

@Getter
@Setter
@Entity
@Table( name = "homework",
        uniqueConstraints = {
        })
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer homeworkId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "courseExId")
    private CourseEx courseEx;

    private String content;
    private Date homeworkReleasingTime;
    private Date homeworkDeadline;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name=" photo", columnDefinition="longblob", nullable=true)
    private byte[] photo;
}
