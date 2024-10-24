package org.example.projectback.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "interests")
public class InterestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;


}
