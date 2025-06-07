package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "honor_type")
public class HonorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorTypeId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EHonorType type;
}
