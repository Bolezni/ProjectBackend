package org.example.testprojectback.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "groups")
public class Group {

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

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @Builder.Default
    private Set<User> subscribers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "group_interest",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    @Builder.Default
    private Set<Interest> interests = new HashSet<>();

    public Group(String chars, String name, String color, String description, User creator) {
        this.chars = chars;
        this.name = name;
        this.color = color;
        this.description = description;
        this.creator = creator;
    }
}
