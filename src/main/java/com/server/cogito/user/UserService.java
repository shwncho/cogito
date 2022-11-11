package com.server.cogito.user;

import com.server.cogito.common.entity.Status;
import com.server.cogito.user.dto.response.TestResponse;
import com.server.cogito.user.domain.User;
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
