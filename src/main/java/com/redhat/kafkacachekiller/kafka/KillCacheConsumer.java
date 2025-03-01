package com.redhat.kafkacachekiller.kafka;

import com.redhat.kafkacachekiller.cache.CacheKillerInfo;
import com.redhat.kafkacachekiller.cache.CacheKillerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KillCacheConsumer {

    private final CacheKillerRegistry cacheKillerRegistry;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}")
    public void consume(CacheKillerInfo cacheKillerInfo) {
        cacheKillerRegistry.killCache(cacheKillerInfo);
    }

}
