package com.wotiwan.medonline.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @Column(name = "user_id")
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    @Column(columnDefinition = "TEXT")
    private String description;

}