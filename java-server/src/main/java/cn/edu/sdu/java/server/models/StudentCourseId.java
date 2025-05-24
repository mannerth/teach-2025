package cn.edu.sdu.java.server.models;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StudentCourseId implements Serializable {
    private Integer personId;
    private Integer courseExId;

    public void setPersonId(Integer x){this.personId = x;}
    public void setCourseExId(Integer x){this.courseExId = x;}
}
