package com.redhat.kafkacachekiller.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheKillerInfo {
    // CacheKiller 唯一標示
    private CacheKillerId cacheKillerId;
    // 清除 cache 資訊
    private Map<String, Object> spec;

}
