package com.demo.metrics;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

public class WebMetricTest {
	@Test
	public void testElapsedTimeCalculation() {
		WebMetric metric = new WebMetric(1000, 2000, UUID.randomUUID(), 500);
		assertEquals(1000, metric.getElapsedTime());
	}
}
