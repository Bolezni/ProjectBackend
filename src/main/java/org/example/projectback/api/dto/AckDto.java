package org.example.projectback.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AckDto {

    String answer;

    public static AckDto makeDefault(String answer) {
        return builder()
                .answer(answer)
                .build();
    }
}