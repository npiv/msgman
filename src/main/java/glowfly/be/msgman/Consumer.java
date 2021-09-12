package glowfly.be.msgman;

import io.micrometer.core.instrument.Metrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnExpression("${msgman.consumer.enabled:true}")
public class Consumer implements MessageListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RedisConnectionFactory redisConnectionFactory;

    public Consumer(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        Metrics.counter("messages.received", "msg", msg).increment();
        LOGGER.info("received {}", msg);
    }

    @PostConstruct
    void setup() {
        redisConnectionFactory.getConnection().pSubscribe(this, "messages".getBytes(StandardCharsets.UTF_8));
    }
}
