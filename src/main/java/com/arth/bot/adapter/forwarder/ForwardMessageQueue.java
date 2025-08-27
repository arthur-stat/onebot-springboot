package com.arth.bot.adapter.forwarder;

import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.arth.bot.core.config.ForwarderConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程安全的消息队列
 */
@Component
public class ForwardMessageQueue {

    public enum DropPolicy {DROP_OLDEST, DROP_NEWEST, BLOCK}

    private final BlockingDeque<ParsedPayloadDTO> q;
    private final DropPolicy dropPolicy;
    private final AtomicLong dropped = new AtomicLong();

    public ForwardMessageQueue(ForwarderConfig.ForwarderProperties props) {
        int capacity = Math.max(1, props.getMessageQueueCapacity());
        this.q = new LinkedBlockingDeque<>(capacity);
        this.dropPolicy = props.getDropPolicy() == null
                ? DropPolicy.DROP_OLDEST
                : DropPolicy.valueOf(props.getDropPolicy());
    }

    /**
     * 非阻塞入队，按策略丢弃或阻塞
     */
    public void offer(ParsedPayloadDTO payload) {
        switch (dropPolicy) {
            case DROP_NEWEST -> {
                boolean ok = q.offerLast(payload);
                if (!ok) dropped.incrementAndGet();
            }
            case DROP_OLDEST -> {
                if (!q.offerLast(payload)) {
                    q.pollFirst();
                    dropped.incrementAndGet();
                    q.offerLast(payload);
                }
            }
            case BLOCK -> {
                try {
                    q.putLast(payload);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public ParsedPayloadDTO take() throws InterruptedException {
        return q.takeFirst();
    }

    public ParsedPayloadDTO poll(Duration timeout) throws InterruptedException {
        return q.pollFirst(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    public int size() {
        return q.size();
    }

    public long droppedCount() {
        return dropped.get();
    }
}
