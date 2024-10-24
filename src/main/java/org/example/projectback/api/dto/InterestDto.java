package org.example.projectback.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestDto {

    @NonNull
    private Long id;

    @NonNull
    private String name;

}
