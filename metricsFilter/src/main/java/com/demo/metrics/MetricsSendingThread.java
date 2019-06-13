package com.demo.metrics;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class MetricsSendingThread extends Thread {

	private MetricStorage metricStorage;
	private MetricsSender sendMetrics;

	MetricsSendingThread(MetricStorage metricStorage, String endpointURL) {
		this.metricStorage = metricStorage;
		this.sendMetrics = new MetricsSender(endpointURL);
	}

	// Constructor to allow mocking out calls that send metrics info for unit tests
	protected MetricsSendingThread(MetricStorage metricStorage, MetricsSender sendMetrics) {
		this.metricStorage = metricStorage;
		this.sendMetrics = sendMetrics;
	}

	@Override
	public void run() {
		BlockingQueue<WebMetric> queue = metricStorage.getMetricsQueue();
		while (true) {
			WebMetric currentMetric;
			try {
				currentMetric = queue.take();
			} catch (InterruptedException e) {
				// Assume we can not recover
				return;
			}
			// Check for poison pill and return if found
			if (currentMetric.getEnd() == -1 && currentMetric.getStart() == -1) {
				return;
			}
			try {
				sendMetrics.sendMetric(currentMetric);
			} catch (IOException e) {
				// Hopefully intermittent conectivity issue, try again later
				e.printStackTrace();
			}
		}
	}

}
