package com.redhat.kafkacachekiller.cache;

import com.redhat.kafkacachekiller.pattern.IdentifiableGetter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;

// CacheKiller 註冊者,利用 spring 依賴注入機制註冊
@Component
@Slf4j
public class CacheKillerRegistry extends IdentifiableGetter<CacheKillerId, CacheKiller> {

    public CacheKillerRegistry(List<CacheKiller> cacheKillers) {
        super(cacheKillers);
    }

    public void killCache(CacheKillerInfo cacheKillerInfo) {
        val cacheKillerId = cacheKillerInfo.getCacheKillerId();
        getIdentifiableOpt(cacheKillerId)
                .ifPresentOrElse(
                        (cacheKiller) -> cacheKiller.kill(cacheKillerInfo.getSpec())
                        , () -> log.info("cacheKillerId: {} , not match any CacheKiller", cacheKillerId)
                );
    }


}
