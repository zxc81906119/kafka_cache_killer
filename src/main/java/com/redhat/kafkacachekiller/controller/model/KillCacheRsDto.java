package com.redhat.kafkacachekiller.controller.model;

import com.redhat.kafkacachekiller.status.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KillCacheRsDto {

    private Status status;

}
