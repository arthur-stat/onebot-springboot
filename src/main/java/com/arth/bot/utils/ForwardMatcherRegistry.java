package com.arth.bot.utils;

import com.arth.bot.infrastructure.forwarder.matcher.ForwardMatcher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ForwardMatcherRegistry {

    private final BeanFactory beanFactory;

    public ForwardMatcherRegistry(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public ForwardMatcher resolve(@Nullable String beanName) {
        String name = (beanName == null || beanName.isBlank()) ? "defaultForwardMatcher" : beanName;
        return beanFactory.getBean(name, ForwardMatcher.class);  // lazy + singleton
    }

    public boolean exists(String beanName) {
        return (beanFactory instanceof ListableBeanFactory lb)
                && Arrays.asList(lb.getBeanNamesForType(ForwardMatcher.class)).contains(beanName);
    }
}
