package org.example.projectback.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "groups")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3)
    private String chars;

    @Column(nullable = false)
    private String name;

    @Column(length = 6)
    @Builder.Default
    private String color ="878787";

    private String description;

    @ManyToMany(fetch = FetchType.LAZY,cascade= CascadeType.ALL)
    @Builder.Default
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,cascade= CascadeType.ALL)
    @Builder.Default
    private List<InterestEntity> interests = new ArrayList<>();
}
