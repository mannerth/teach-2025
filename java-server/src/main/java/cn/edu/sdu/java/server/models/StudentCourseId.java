package cn.edu.sdu.java.server.models;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StudentCourseId implements Serializable {
    private Integer personId;
    private Integer courseExId;
}
