package com.demo.controlers;

import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.demo.metrics.WebMetric;
import com.demo.metrics.WebMetricsSummary;

@RestController
public class MetricsControler {
	private static ConcurrentHashMap<String, WebMetric> metricsList = new ConcurrentHashMap<String, WebMetric>();

	@GetMapping(value = "/metric/{metricId}")
	public WebMetric getSpecificMetric(@PathVariable("metricId") String metricId) {
		WebMetric metricToReturn = metricsList.get(metricId);
		if (metricToReturn == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested metric was not found.");
		}
		return metricToReturn;
	}

	@PostMapping("/metric")
	public WebMetric addNewMetric(@RequestBody WebMetric metric) {
		metricsList.put(metric.getRequestId().toString(), metric);
		return metric;
	}

	@GetMapping("/metricSummary")
	public WebMetricsSummary getMetricsSummary() {
		LongSummaryStatistics responseStatistics = metricsList.values().stream()
				.collect(Collectors.summarizingLong(WebMetric::getResponseSize));
		LongSummaryStatistics timingStatistics = metricsList.values().stream()
				.collect(Collectors.summarizingLong(WebMetric::getElapsedTime));
		return new WebMetricsSummary(timingStatistics.getMin(), timingStatistics.getAverage(),
				timingStatistics.getMax(), responseStatistics.getMin(), responseStatistics.getAverage(),
				responseStatistics.getMax());
	}
}
