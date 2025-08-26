package com.arth.bot.plugins;

import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("plugins.test2")
@RequiredArgsConstructor
public class Test2 {

    private final Sender sender;

    public void index(ParsedPayloadDTO payload, List<String> args) {
        long ms = 5000L;
        try {
            if (args != null && !args.isEmpty()) {
                ms = Math.max(0, Long.parseLong(args.get(0)));
            }
        } catch (NumberFormatException ignore) {}

        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        sender.sendText(payload, "test2: done after " + ms + " ms");
    }
}