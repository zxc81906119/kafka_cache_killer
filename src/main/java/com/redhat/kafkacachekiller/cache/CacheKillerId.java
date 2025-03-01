package com.redhat.kafkacachekiller.cache;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CacheKillerId {
    // 種類
    private String kind;
    // 名稱
    private String name;

    public static CacheKillerId with(String kind, String name) {
        return new CacheKillerId(kind, name);
    }
}
