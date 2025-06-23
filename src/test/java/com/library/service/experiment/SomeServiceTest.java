package com.library.service.experiment;

import com.library.experiment.Counter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Counter.class})
@TestPropertySource(properties = {"my.values=apple-banana-cherry"})
public class SomeServiceTest {
    @Value("${my.values}")
    String values;

    @Test
    void testInjectedValues() {
        assertEquals("apple-banana-cherry", values);
    }
}
