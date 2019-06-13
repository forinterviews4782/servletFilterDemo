package com.demo.metrics;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MetricStorage {
	private BlockingQueue<WebMetric> metricsQueue;

	public MetricStorage(int queueSize) {
		metricsQueue = new ArrayBlockingQueue<WebMetric>(queueSize);
	}

	public void add(WebMetric webMetric) {
		// Attempt to add item to queue, but ignore failure so we return quickly and
		// allow request chain to continue
		metricsQueue.offer(webMetric);
	}

	public BlockingQueue<WebMetric> getMetricsQueue() {
		return metricsQueue;
	}

}
