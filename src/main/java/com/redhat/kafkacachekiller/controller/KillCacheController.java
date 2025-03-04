package com.redhat.kafkacachekiller.controller;

import com.redhat.kafkacachekiller.cache.CacheKillerInfo;
import com.redhat.kafkacachekiller.cache.CacheKillerRegistry;
import com.redhat.kafkacachekiller.prop.KillCacheProperties;
import com.redhat.kafkacachekiller.status.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class KillCacheController {

    @Qualifier(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private final Executor executor;
    private final KillCacheProperties killCacheProperties;
    private final CacheKillerRegistry cacheKillerRegistry;
    private final KafkaTemplate<String, CacheKillerInfo> kafkaTemplate;


    @PostMapping(
            value = "${cache.kill.endpoint:/cache/kill}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public KillCacheRsDto killCache(@Valid @RequestBody KillCacheRqDto killCacheRqDto) {
        try {
            val cacheKillerInfo = KillCacheRqDto.Converter.INSTANCE.toInfo(killCacheRqDto);
            val asyncProcessResult = asyncProcess(cacheKillerInfo, killCacheRqDto.isSelf());
            val waitTimeProperties = killCacheProperties.getWaitTime();
            return !waitTimeProperties.isEnabled()
                    ? asyncProcessResult.getNow(KillCacheRsDto.builder().status(Status.ASYNC).build())
                    : asyncProcessResult.get(waitTimeProperties.getValue(), waitTimeProperties.getUnit());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return KillCacheRsDto.builder()
                    .status(Status.INTERRUPTED)
                    .build();
        } catch (TimeoutException e) {
            return KillCacheRsDto.builder()
                    .status(Status.TIMEOUT)
                    .build();
        } catch (ExecutionException | CompletionException e) {
            return KillCacheRsDto.builder()
                    .status(Status.FAILED)
                    .build();
        }
    }

    private CompletableFuture<KillCacheRsDto> asyncProcess(CacheKillerInfo cacheKillerInfo, boolean self) {
        return self
                ? CompletableFuture.supplyAsync(() -> directKillCache(cacheKillerInfo), executor)
                : notifyKillCacheInfo(cacheKillerInfo);
    }

    private KillCacheRsDto directKillCache(CacheKillerInfo cacheKillerInfo) {
        cacheKillerRegistry.killCache(cacheKillerInfo);
        return KillCacheRsDto.builder()
                .status(Status.SUCCESS)
                .build();
    }

    private CompletableFuture<KillCacheRsDto> notifyKillCacheInfo(CacheKillerInfo cacheKillerInfo) {
        return kafkaTemplate.sendDefault(cacheKillerInfo)
                .handle((result, throwable) -> {
                    if (throwable == null) {
                        val metadata = result.getRecordMetadata();
                        log.debug("notify success, kafka topic: {} , partition: {}", metadata.topic(), metadata.partition());
                        return KillCacheRsDto.builder()
                                .status(Status.NOTIFY_SUCCESS)
                                .build();
                    }

                    return KillCacheRsDto.builder()
                            .status(Status.NOTIFY_FAILURE)
                            .build();

                });
    }


}
