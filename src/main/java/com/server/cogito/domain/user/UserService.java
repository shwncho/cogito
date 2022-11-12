package com.server.cogito.domain.user;

import com.server.cogito.global.common.entity.Status;
import com.server.cogito.domain.user.domain.User;
import com.server.cogito.domain.user.dto.response.TestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public TestResponse test(String email){
        TestResponse dto = new TestResponse();
        User user = userRepository.findByEmailAndStatus(email, Status.ACTIVE).get();
        dto.setId(user.getId());

        return dto;
    }

}
