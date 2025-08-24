package com.arth.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ConcurrencyConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
        return new ThreadPoolExecutor(
                cores, cores * 2,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                r -> {
                    Thread t = new Thread(r, "cmd-" + System.nanoTime());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()  // 回退策略：队满回退至调用线程
        );
    }
}
