package com.demo.metrics;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class MetricsFilter implements Filter {

	protected static final int DEFAULT_QUEUE_SIZE = 1000;
	private MetricStorage metricsStorage;
	private MetricsSendingThread metricsSendingThread;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		UUID requestId = UUID.randomUUID();
		long start = System.currentTimeMillis();
// Wrap the response and write to a byte array instead of the normal output stream. After returning from doFilter get the length of the byte array for request metrics. Finally write the byte array to the origional output stream
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		MetricsResponseWrapper responseWrapper = new MetricsResponseWrapper(httpServletResponse);
		responseWrapper.addHeader("request-id", requestId.toString());
		chain.doFilter(request, responseWrapper);
		byte[] bytesToWrite = responseWrapper.getBytes();
		response.getOutputStream().write(bytesToWrite);
		long responseSize = bytesToWrite.length;
		long end = System.currentTimeMillis();
		metricsStorage.add(new WebMetric(start, end, requestId, responseSize));
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		internalInit(filterConfig, null);
	}

	@Override
	public void destroy() {
		// Send poison pill so sending thread will end
		metricsStorage.add(new WebMetric(-1, -1, UUID.randomUUID(), -1));
	}

	public void internalInit(FilterConfig filterConfig, MetricsSendingThread metricsSendingThreadArgument)
			throws ServletException {
		int initializationSize = DEFAULT_QUEUE_SIZE;
		try {
			initializationSize = Integer.parseInt(filterConfig.getInitParameter("queueSize"));
		} catch (NumberFormatException e) {
			// Do nothing leave default queue size
		}
		if (initializationSize < 1) {
			initializationSize = DEFAULT_QUEUE_SIZE;
		}
		metricsStorage = new MetricStorage(initializationSize);
		// If thread is null we are not dealing with unit test that needs a mock object
		// so create a new one
		if (metricsSendingThreadArgument == null) {
			metricsSendingThread = new MetricsSendingThread(metricsStorage,
					filterConfig.getInitParameter("metricsEndpointURL"));
		} else // Unit test passed in a mock so respect that
		{
			metricsSendingThread = metricsSendingThreadArgument;
		}
		metricsSendingThread.start();
	}

	public MetricStorage getMetricsStorage() {
		return metricsStorage;
	}

}
