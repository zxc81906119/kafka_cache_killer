package com.redhat.kafkacachekiller.prop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "cache.kill")
public class KillCacheProperties {

    private WaitTimeProperties waitTime = new WaitTimeProperties();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitTimeProperties {
        private long value = 1;
        private TimeUnit unit = TimeUnit.SECONDS;
        private boolean enabled = false;
    }

}
