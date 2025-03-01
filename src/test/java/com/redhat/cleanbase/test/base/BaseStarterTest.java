package com.redhat.cleanbase.test.base;

import com.redhat.cleanbase.test.config.StarterTestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterTestConfiguration.class)
public abstract class BaseStarterTest {
}
