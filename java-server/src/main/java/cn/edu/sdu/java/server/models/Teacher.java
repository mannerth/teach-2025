package cn.edu.sdu.java.server.models;

/**
 * *@Author：Cui
 * *@Date：2025/6/4  1:27
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "teacher")
public class Teacher {
    @Id
    private Integer personId;

    @OneToOne
    @JoinColumn(name="personId")
    @JsonIgnore
    private Person person;

    @Size(max = 20)
    private String title; // 职称
}