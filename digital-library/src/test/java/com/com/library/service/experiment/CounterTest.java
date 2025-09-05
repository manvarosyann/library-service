package com.com.library.service.experiment;

import com.library.experiment.Counter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Counter.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CounterTest {

    @Autowired
    private Counter counter;

    @Test
    void increment() {
        counter.increment();
        assertEquals(1, counter.getValue());
    }

    @Test
    void testInitialValue() {
        // This test will fail unless context is refreshed because value might already be 1
        assertEquals(0, counter.getValue(), "Counter is not reset between tests");
    }
}

