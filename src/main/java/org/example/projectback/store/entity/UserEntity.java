package org.example.projectback.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectback.store.entity.enums.Gender;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

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

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String patronymic; //отчество

    @Column(nullable = false)
    @Min(value = 6,message = "Password must consist of at least 6 characters")
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String description;

    @Column(nullable = false)
    private String tgName;

    private String profileImageId;

    @Builder.Default
    private boolean isAdmin = false;

    @Transient
    private Integer age;

    private LocalDate birthDay;

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @Builder.Default
    private Set<UserEntity> friends = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,cascade= CascadeType.ALL)
    @Builder.Default
    private List<GroupEntity> groups = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY,cascade= CascadeType.ALL)
    @Builder.Default
    private List<InterestEntity> interests = new ArrayList<>();

    public Integer getAge() {
        return Period.between(birthDay, LocalDate.now()).getYears();
    }

}
