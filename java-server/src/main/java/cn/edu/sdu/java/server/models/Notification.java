package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table( name = "notification",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "num"),
        })

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String title;
    private String releaseTime;
}
