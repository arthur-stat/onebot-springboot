package com.arth.bot.core.infrastructure.forwarder;

import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.arth.bot.core.config.ForwarderConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForwardDispatcher implements SmartLifecycle {

    private final ForwarderConfig.ForwarderProperties props;
    private final ForwardMessageQueue queue;
    private final UpstreamPool upstreamPool;
    private List<ForwarderConfig.ForwarderProperties.Target> upStreamTargets = new ArrayList<>();
    private volatile boolean running;
    private Thread worker;

    @Override
    public synchronized void start() {
        if (running) return;
        running = true;
        worker = new Thread(this::loop, "forwarder-dispatcher");
        worker.setDaemon(true);
        worker.start();
        upStreamTargets = props.getTargets();
    }

    private void loop() {
        while (running) {
            try {
                ParsedPayloadDTO payload = queue.take();

                for (var target : upStreamTargets) {
                    upstreamPool.forward(target, payload);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("[forward] forward dispatch error: {}", e.toString());
            }
        }
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (worker != null) worker.interrupt();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
