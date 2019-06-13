package com.demo.metrics;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.Thread.State;
import java.net.MalformedURLException;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetricsSendingThreadTest {
	private static final long TEST_SLEEP_DELAY = 100;
	@Mock
	private MetricsSender mockMetricsSender;

	@Test
	public void testMetricsThread() throws InterruptedException, MalformedURLException, IOException {
		MetricStorage metricStorage = new MetricStorage(500);
		MetricsSendingThread metricsSendingThread = new MetricsSendingThread(metricStorage, mockMetricsSender);
		metricsSendingThread.start();
		metricStorage.add(new WebMetric(1000, 2000, UUID.randomUUID(), 100));
		metricStorage.add(new WebMetric(3000, 5000, UUID.randomUUID(), 100));
		// Add poison pill to terminate thread
		metricStorage.add(new WebMetric(-1, -1, UUID.randomUUID(), 100));
		Thread.sleep(TEST_SLEEP_DELAY);
		// poison pill should not be sent so only expect two send calls
		verify(mockMetricsSender, times(2)).sendMetric(any(WebMetric.class));
		assertTrue("Thread not in terminated state.", metricsSendingThread.getState() == State.TERMINATED);
	}
}
