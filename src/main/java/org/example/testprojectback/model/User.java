package org.example.testprojectback.model;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    @NotBlank
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String patronymic;

    @Column(nullable = false)
    @Min(value = 6,message = "Password must consist of at least 6 characters")
    @NotBlank
    private String password;

    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    private String email;

    @Builder.Default
    private boolean isActivated = false;

    private String activateCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 600)
    private String description;

    @Column(nullable = false)
    private String tgName;

    @Builder.Default
    private String profileImageId = "";

    @Transient
    private Integer age;

    @Builder.Default
    private boolean isAdmin = false;

    private LocalDate birthDay;

    @Column(nullable = false)
    private String secretKey;

    @Column(nullable = false)
    @Builder.Default
    private boolean mfaEnabled = false;

    @OneToMany(mappedBy = "creator")
    @Builder.Default
    private Set<Group> createdGroups = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "friendships",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @Builder.Default
    private  Set<User> friends = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    @Builder.Default
    private Set<Group> subscribedGroups = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_interest",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id"))
    @Builder.Default
    private  Set<Interest> interests = new HashSet<>();

    public User(String username, String firstName, String lastName, String patronymic, String password, String email, Gender gender, String description, String tgName, String profileImageId, LocalDate birthDay) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.description = description;
        this.tgName = tgName;
        this.profileImageId = profileImageId;
        this.birthDay = birthDay;
    }

    public User(String firstName, String lastName, String patronymic, Gender gender, String description, String tgName, LocalDate birthDay) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.gender = gender;
        this.description = description;
        this.tgName = tgName;
        this.birthDay = birthDay;
    }

    public Integer getAge() {
        return Period.between(birthDay, LocalDate.now()).getYears();
    }
}
