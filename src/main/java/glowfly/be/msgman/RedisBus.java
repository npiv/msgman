package glowfly.be.msgman;

import io.micrometer.core.instrument.Metrics;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RedisBus implements MsgBus {
    private final RedisConnectionFactory redisConnectionFactory;

    public RedisBus(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void post(String msg) {
        Metrics.counter("messages.sent", "msg", msg).increment();
        redisConnectionFactory.getConnection().publish("messages".getBytes(StandardCharsets.UTF_8), msg.getBytes(StandardCharsets.UTF_8));
    }
}
