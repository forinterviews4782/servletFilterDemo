package com.demo.metrics;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MetricsSender {
	private String endpointURL;

	public MetricsSender(String endpointURL) {
		this.endpointURL = endpointURL;
	}

	public void sendMetric(WebMetric metric) throws MalformedURLException, IOException {
		URL postURL = new URL(endpointURL);
		HttpURLConnection con = (HttpURLConnection) postURL.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json;");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Method", "POST");
		OutputStream os = con.getOutputStream();
		String metricJSONString = generateJSONString(metric);
		os.write(metricJSONString.getBytes("UTF-8"));
		os.close();
		// We do not care about returned text so just handle response code
		int HttpResult = con.getResponseCode();
		if (HttpResult != HttpURLConnection.HTTP_OK) {
			// Use System.out.println instead of real logging since this is not production
			// code.
			// I assume based on my experience each app server has there own way of handling
			// logging
			System.out.println("Error sending " + metricJSONString + " got response code " + con.getResponseCode());
			System.out.println(con.getResponseMessage());
		}

	}

	protected String generateJSONString(WebMetric metric) {
		return "{\"end\":" + metric.getEnd() + ", \"requestId\":\"" + metric.getRequestId() + "\", \"start\":"
				+ metric.getStart() + ", \"responseSize\":" + metric.getResponseSize() + ", \"elapsedTime\":"
				+ metric.getElapsedTime() + "}";
	}

}