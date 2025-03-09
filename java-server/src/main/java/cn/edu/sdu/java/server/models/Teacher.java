package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity         //表示它是实体类，对应数据库的一张表，从底向上走建造顺序：models, repositorys, services, controllers,从上向下走也可以
@Table(	name = "teacher",
        uniqueConstraints = {
        })
public class Teacher {
    @Id
    private Integer personId;

    @OneToOne
    @JoinColumn(name="personId")
    @JsonIgnore
    private Person person;
    @Size(max=50)       //String类型一定有Size
    private String title;
    @Size(max=50)
    private String degree;      //model中的属性的类一定用的是包装类
    private Integer studentNum;
    private Date enterTime;
}
