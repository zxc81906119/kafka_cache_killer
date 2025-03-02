package com.redhat.kafkacachekiller.controller;

import com.redhat.kafkacachekiller.cache.CacheKillerInfo;
import com.redhat.kafkacachekiller.mapstruct.BaseMapperConfig;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KillCacheRqDto {
    @NotNull
    private String kind;
    @NotNull
    private String name;
    private boolean self = false;
    private Map<String, Object> spec;

    @Mapper(
            config = BaseMapperConfig.class
    )
    public interface Converter {
        Converter INSTANCE = Mappers.getMapper(Converter.class);

        @BeanMapping(
                ignoreUnmappedSourceProperties = {"self"}
        )
        @Mapping(source = "name", target = "cacheKillerId.name")
        @Mapping(source = "kind", target = "cacheKillerId.kind")
        CacheKillerInfo toInfo(KillCacheRqDto killCacheRqDto);
    }
}
