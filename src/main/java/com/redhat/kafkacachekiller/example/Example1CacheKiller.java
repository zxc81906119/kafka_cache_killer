package com.redhat.kafkacachekiller.example;

import com.redhat.kafkacachekiller.cache.CacheKiller;
import com.redhat.kafkacachekiller.cache.CacheKillerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

// todo 範例清 cache 實做,參考後可刪除
@Component
@Slf4j
public class Example1CacheKiller implements CacheKiller {

    @Override
    public void kill(Map<String, Object> spec) {
        log.info("Killing cache with spec: {}", spec);
    }

    @Override
    public CacheKillerId getIdentifier() {
        return CacheKillerId.with("oeai", "sysParam");
    }
}
