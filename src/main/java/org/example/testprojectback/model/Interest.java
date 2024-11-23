package org.example.testprojectback.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interests")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false,length = 6)
    @Builder.Default
    private String color = "878787";

    @ManyToMany(mappedBy = "interests")
    @Builder.Default
    private Set<Group> groups = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "interest_users",
            joinColumns = @JoinColumn(name = "interest_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    public Interest(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
