package glowfly.be.msgman;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnExpression("${msgman.producer.enabled:true}")
public class Producer implements Runnable {
    private static final Random rnd = new Random();

    private final int chance;
    private final int sleep;
    private final String name;
    private final ScheduledExecutorService scheduledExecutorService;
    private final MsgBus msgBus;

    public Producer(@Value("${msgman.producer.chance}") Integer chance,
                    @Value("${msgman.producer.sleep}") Integer sleep,
                    @Value("${msgman.producer.name}") String name,
                    ScheduledExecutorService scheduledExecutorService,
                    MsgBus msgBus) {
        this.chance = chance;
        this.name = name;
        this.sleep = sleep;
        this.msgBus = msgBus;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @PostConstruct
    void begin() throws InterruptedException {
        scheduledExecutorService.scheduleAtFixedRate(this, 0, sleep, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        if (chance >= rnd.nextInt(100)) {
            msgBus.post(this.name);
        }
    }
}
