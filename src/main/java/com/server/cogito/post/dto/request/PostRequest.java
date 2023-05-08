package com.server.cogito.post.dto.request;

import com.server.cogito.post.entity.Post;
import com.server.cogito.user.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "본문을 입력해주세요.")
    private String content;

    private List<String> files;

    private List<String> tags;

    public static Post toEntity(String title, String content, User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }
}
