package com.redhat.kafkacachekiller.controller;

import com.redhat.kafkacachekiller.cache.CacheKillerInfo;
import com.redhat.kafkacachekiller.cache.CacheKillerRegistry;
import com.redhat.kafkacachekiller.controller.model.KillCacheRqDto;
import com.redhat.kafkacachekiller.controller.model.KillCacheRsDto;
import com.redhat.kafkacachekiller.prop.KillCacheProperties;
import com.redhat.kafkacachekiller.status.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class KillCacheController {

    private final KillCacheProperties killCacheProperties;
    private final CacheKillerRegistry cacheKillerRegistry;
    private final KafkaTemplate<String, CacheKillerInfo> kafkaTemplate;

    @PostMapping(value = "${cache.kill.endpoint:/cache/kill}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public KillCacheRsDto killCache(@RequestBody KillCacheRqDto killCacheRqDto) {

        val cacheKillerInfo = KillCacheRqDto.Converter.INSTANCE.toInfo(killCacheRqDto);

        if (killCacheRqDto.isSelf()) {
            cacheKillerRegistry.killCache(cacheKillerInfo);
            return KillCacheRsDto.builder()
                    .status(Status.SUCCESS)
                    .build();
        }


        val notifyResult = callKafka(cacheKillerInfo);

        val waitTimeProperties = killCacheProperties.getWaitTime();
        if (!waitTimeProperties.isEnabled()) {
            return KillCacheRsDto.builder()
                    .status(Status.ASYNC_NOTIFY)
                    .build();
        }

        try {
            return notifyResult
                    .handle((result, throwable) -> {
                        if (throwable != null) {
                            return KillCacheRsDto.builder()
                                    .status(Status.SYNC_NOTIFY_FAILURE)
                                    .build();
                        }
                        val metadata = result.getRecordMetadata();
                        log.debug("notify success, kafka topic: {} , partition: {}", metadata.topic(), metadata.partition());
                        return KillCacheRsDto.builder()
                                .status(Status.SYNC_NOTIFY_SUCCESS)
                                .build();
                    })
                    .get(waitTimeProperties.getValue(), waitTimeProperties.getUnit());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            return KillCacheRsDto.builder()
                    .status(Status.SYNC_NOTIFY_TIMEOUT)
                    .build();
        }
        return KillCacheRsDto.builder()
                .status(Status.FAILED)
                .build();
    }

    private CompletableFuture<SendResult<String, CacheKillerInfo>> callKafka(CacheKillerInfo cacheKillerDo) {
        return kafkaTemplate.sendDefault(cacheKillerDo);
    }


}
