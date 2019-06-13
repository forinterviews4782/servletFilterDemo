package com.demo;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.demo.metrics.MetricsFilter;

@Configuration
public class WebConfiguration {
	@Bean
	public FilterRegistrationBean<MetricsFilter> configureMetricsFilter() {
		FilterRegistrationBean<MetricsFilter> metricsFilterRegistration = new FilterRegistrationBean<MetricsFilter>();
		metricsFilterRegistration.setFilter(new MetricsFilter());
		metricsFilterRegistration.setName("metrics-filter");
		metricsFilterRegistration.setOrder(Ordered.LOWEST_PRECEDENCE);
		metricsFilterRegistration.addUrlPatterns("/demo");
		metricsFilterRegistration.addInitParameter("metricsEndpointURL", "http://localhost:8080/metric");
		return metricsFilterRegistration;
	}
}