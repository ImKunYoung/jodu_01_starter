package org.example.jodu_01_Starter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsUpdateRequestDto {

    private String title;
    private String content;

}
