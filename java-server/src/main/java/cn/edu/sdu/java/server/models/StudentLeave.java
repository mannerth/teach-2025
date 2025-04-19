package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * Student学生表实体类 保存每个学生的信息，
 * Integer personId 学生表 student 主键 person_id 与Person表主键相同
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String major 专业
 * String className 班级
 *
 */
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(	name = "studentLeave",
        uniqueConstraints = {
        })
public class StudentLeave {
//    @Id
//    private Integer personId;
//
//    @OneToOne
//    @JoinColumn(name = "personId")
//    @JsonIgnore
//    private Person person;
//
//    @Size(max = 50)
//    private String name;
//
//    @Size(max = 50)
//    private String className;
//
//    private Date startdate;
//    private Date enddate;
//
//    @Size(max = 50)
//    private String reason;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveId;

//       // 字段非空
//    @Size(max = 20)   //字段长度最长为20
//    private String num;

//    @JoinColumn(name="leaveId")

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String className;

    private Date startdate;
    private Date enddate;

    @Size(max = 50)
    private String reason;
}