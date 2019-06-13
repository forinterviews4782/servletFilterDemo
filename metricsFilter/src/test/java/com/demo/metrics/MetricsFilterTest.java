package com.demo.metrics;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetricsFilterTest {
	@Mock
	private ServletRequest mockRequest;
	@Mock
	private HttpServletResponse mockResponse;
	@Mock
	private FilterChain mockChain;
	@Mock
	private ServletOutputStream mockOutputStream;
	@Mock
	private FilterConfig mockConfiguration;
	@Mock
	private MetricsSendingThread mockMetricsSendingThread;
	private MetricsFilter filter;

	@Before
	public void setup() throws IOException, ServletException {
		when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
		filter = new MetricsFilter();
		filter.internalInit(mockConfiguration, mockMetricsSendingThread);
		filter.doFilter(mockRequest, mockResponse, mockChain);
	}

	@Test
	public void testDoFilterCallsFilterChain() throws IOException, ServletException, InterruptedException {
		verify(mockChain, times(1)).doFilter(eq(mockRequest), any(HttpServletResponseWrapper.class));
	}

	@Test
	public void verifyOneItemAddedToQueueWhenDoFilterCalled() {
		assertEquals("Queue size should be 1", 1, filter.getMetricsStorage().getMetricsQueue().size());
	}

	@Test
	public void verifyMetricStartTimeCloseToCurrentTime() throws InterruptedException {
		WebMetric metric = filter.getMetricsStorage().getMetricsQueue().take();
		assertEquals(System.currentTimeMillis(), metric.getStart(), 1000.0);
	}

	@Test
	public void verifyMetricEndTimeCloseToCurrentTime() throws InterruptedException {
		WebMetric metric = filter.getMetricsStorage().getMetricsQueue().take();
		assertEquals(System.currentTimeMillis(), metric.getEnd(), 1000.0);
	}

// Since we did not mock out ByteArrayPrinter class no bytes are written. Because of this response size should always be zero  
	@Test
	public void insureResponseSizeIsZero() throws InterruptedException {
		WebMetric metric = filter.getMetricsStorage().getMetricsQueue().take();
		assertEquals(0, metric.getResponseSize());
	}

	@Test
	public void verifyRequestTrackingHeaderIsSetProperly() throws IOException, ServletException, InterruptedException {
		ArgumentCaptor<String> requestIdCapture = ArgumentCaptor.forClass(String.class);
		verify(mockResponse, times(1)).addHeader(requestIdCapture.capture(), requestIdCapture.capture());
		List<String> capturedValues = requestIdCapture.getAllValues();
		assertEquals("request-id", capturedValues.get(0)); // header name
		assertEquals(36, capturedValues.get(1).length()); // If length is not 36 header value is not string
															// representation of UUID
		WebMetric metric = filter.getMetricsStorage().getMetricsQueue().take();
		assertEquals(capturedValues.get(1), metric.getRequestId().toString());
	}

	@Test
	public void verifyDefaultQueueSize() throws InterruptedException {
		BlockingQueue<WebMetric> queue = filter.getMetricsStorage().getMetricsQueue();
		queue.take();
		assertEquals(MetricsFilter.DEFAULT_QUEUE_SIZE, queue.remainingCapacity());
	}

	@Test
	public void testInitializingQueueWithNonDefaultValue() throws ServletException {
		FilterConfig customMockConfiguration = mock(FilterConfig.class);
		when(customMockConfiguration.getInitParameter("queueSize")).thenReturn("200");
		MetricsFilter testFilter = new MetricsFilter();
		testFilter.internalInit(customMockConfiguration, mockMetricsSendingThread);
		BlockingQueue<WebMetric> queue = testFilter.getMetricsStorage().getMetricsQueue();
		assertEquals(200, queue.remainingCapacity());
	}

	@Test
	public void testInitializingQueueWithZeroUsesDefaultSize() throws ServletException {
		FilterConfig customMockConfiguration = mock(FilterConfig.class);
		when(customMockConfiguration.getInitParameter("queueSize")).thenReturn("0");
		MetricsFilter testFilter = new MetricsFilter();
		testFilter.internalInit(customMockConfiguration, mockMetricsSendingThread);
		BlockingQueue<WebMetric> queue = testFilter.getMetricsStorage().getMetricsQueue();
		assertEquals(MetricsFilter.DEFAULT_QUEUE_SIZE, queue.remainingCapacity());
	}

	@Test
	public void testInitializingQueueNegativeValueUsesDefault() throws ServletException {
		FilterConfig customMockConfiguration = mock(FilterConfig.class);
		when(customMockConfiguration.getInitParameter("queueSize")).thenReturn("-1");
		MetricsFilter testFilter = new MetricsFilter();
		testFilter.internalInit(customMockConfiguration, mockMetricsSendingThread);
		BlockingQueue<WebMetric> queue = testFilter.getMetricsStorage().getMetricsQueue();
		assertEquals(MetricsFilter.DEFAULT_QUEUE_SIZE, queue.remainingCapacity());
	}

	@Test
	public void testInternalInitCallsStartOnThread() throws IOException, ServletException, InterruptedException {
		verify(mockMetricsSendingThread, times(1)).start();
	}

}
