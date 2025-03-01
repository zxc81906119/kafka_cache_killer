package com.redhat.kafkacachekiller.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

// todo 範例清 cache 實做,參考後可刪除
@Component
@Slf4j
public class ExampleCacheKiller implements CacheKiller {

    @Override
    public void kill(Map<String, Object> spec) {
        log.info("Killing cache with spec: {}", spec);
    }

    @Override
    public CacheKillerId getIdentifier() {
        return CacheKillerId.with("mid", "hello");
    }
}
