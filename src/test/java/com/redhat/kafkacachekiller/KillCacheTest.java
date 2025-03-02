package com.redhat.kafkacachekiller;

import com.redhat.cleanbase.test.annotation.MvcInfo;
import com.redhat.cleanbase.test.base.BaseMvcTest;
import com.redhat.kafkacachekiller.status.Status;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestMethod;

@EmbeddedKafka(count = 3, topics = "${spring.kafka.template.default-topic}", brokerProperties = "auto.create.topics.enable = ${spring.kafka.admin.auto-create:true}", partitions = 1)
public class KillCacheTest extends BaseMvcTest {

    // todo 之後要改 prop 可以使用此方式
//    @DynamicPropertySource
//    public static void setup(DynamicPropertyRegistry registry) {
//        registry.add("key", () -> "value");
//    }

    @MvcInfo(url = "/cache/kill", httpMethod = RequestMethod.POST)
    @Test
    public void triggerKillCache() throws Exception {
        val perform = mvc.perform(getMockRequestBuilder());
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(Status.ASYNC.name()));
        Thread.sleep(1000);
    }

}
