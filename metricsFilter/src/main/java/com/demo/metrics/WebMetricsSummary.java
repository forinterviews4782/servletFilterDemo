package com.demo.metrics;

public class WebMetricsSummary {

	private double responseAverage;
	private long responseMax;
	private long responseMin;
	private double timingAverage;
	private long timingMax;
	private long timingMin;

	public WebMetricsSummary(long timingMin, double timingAverage, long timingMax, long responseMin,
			double responseAverage, long responseMax) {
		this.timingMin = timingMin;
		this.timingAverage = timingAverage;
		this.timingMax = timingMax;
		this.responseMin = responseMin;
		this.responseMax = responseMax;
		this.responseAverage = responseAverage;
	}

	public double getResponseAverage() {
		return responseAverage;
	}

	public void setResponseAverage(double responseAverage) {
		this.responseAverage = responseAverage;
	}

	public long getResponseMax() {
		return responseMax;
	}

	public void setResponseMax(long responseMax) {
		this.responseMax = responseMax;
	}

	public long getResponseMin() {
		return responseMin;
	}

	public void setResponseMin(long responseMin) {
		this.responseMin = responseMin;
	}

	public double getTimingAverage() {
		return timingAverage;
	}

	public void setTimingAverage(double timingAverage) {
		this.timingAverage = timingAverage;
	}

	public long getTimingMax() {
		return timingMax;
	}

	public void setTimingMax(long timingMax) {
		this.timingMax = timingMax;
	}

	public long getTimingMin() {
		return timingMin;
	}

	public void setTimingMin(long timingMin) {
		this.timingMin = timingMin;
	}

}
