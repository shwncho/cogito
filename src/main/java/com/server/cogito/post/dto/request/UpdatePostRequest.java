package com.server.cogito.post.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePostRequest {

    private String title;

    private String content;

    private List<String> files;

    private List<String> tags;
}
