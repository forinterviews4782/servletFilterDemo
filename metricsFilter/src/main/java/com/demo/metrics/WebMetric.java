package com.demo.metrics;

import java.util.UUID;

public class WebMetric {

	private long end;
	private UUID requestId;
	private long start;
	private long responseSize;
	private long elapsedTime;

	public WebMetric() {
	}

	public WebMetric(long start, long end, UUID requestId, long responseSize) {
		this.start = start;
		this.end = end;
		this.requestId = requestId;
		this.responseSize = responseSize;
		this.elapsedTime = end - start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public UUID getRequestId() {
		return requestId;
	}

	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getResponseSize() {
		return responseSize;
	}

	public void setResponseSize(long responseSize) {
		this.responseSize = responseSize;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

}
