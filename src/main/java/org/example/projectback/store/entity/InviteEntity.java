package org.example.projectback.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectback.store.entity.enums.InviteStatus;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "invites")
public class InviteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private UserEntity from;

    @OneToOne
    private UserEntity to;

    @Enumerated(EnumType.ORDINAL) // в бд отобразиться как число
    private InviteStatus status;
}
