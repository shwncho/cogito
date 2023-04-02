package com.server.cogito.post.service;

import com.server.cogito.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockPostFacade {

    private final RedissonClient redissonClient;
    private final PostService postService;

    public void likePost(AuthUser authUser, Long postId){
        RLock lock = redissonClient.getLock(postId.toString());

        try {
            // 획득시도 시간, 락 점유 시간
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("lock 획득 실패");
                return;
            }
            postService.likePost(authUser,postId);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }
}
