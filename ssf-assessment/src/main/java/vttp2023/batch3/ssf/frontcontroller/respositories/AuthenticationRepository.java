package vttp2023.batch3.ssf.frontcontroller.respositories;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepository {
	@Autowired 
	RedisTemplate<String, String> redisTemplate;

	// TODO Task 5
	// Use this class to implement CRUD operations on Redis
    // public void save(String redisKey, String redisValue, Duration timeToLive) {
    //     redisTemplate.opsForValue().set(redisKey, redisValue, timeToLive);
    // }

	public void save(String redisKey, String redisValue) {
        redisTemplate.opsForValue().set(redisKey, redisValue);
    }

    public void remove(String redisKey) {
        redisTemplate.opsForValue().getAndDelete(redisKey);
    }

    public Optional<String> get(String redisKey) {
        String json = redisTemplate.opsForValue().get(redisKey);

        if (null == json) {
            return Optional.empty();
        }

        return Optional.of(json);
    }
}

