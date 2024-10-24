package org.example.projectback.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectback.store.entity.enums.Gender;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, name = "login")
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String patronymic; //отчество

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Gender gender;

    private String description;

    @Column(nullable = false)
    private String tgName;

    @Builder.Default
    private boolean isAdmin = false;

    @Transient
    private Integer age;

    private LocalDate birthDay;

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    private Set<UserEntity> friends;

    @ManyToMany(fetch = FetchType.LAZY,cascade= CascadeType.ALL)
    private List<GroupEntity> groups;


    @OneToMany
    private List<InterestEntity> interests;

    public Integer getAge() {
        return Period.between(birthDay, LocalDate.now()).getYears();
    }

}
