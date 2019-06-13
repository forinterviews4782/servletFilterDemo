package com.demo.metrics;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class MetricsResponseWrapper extends HttpServletResponseWrapper {

	private ByteArrayPrinter pw = new ByteArrayPrinter();

	public MetricsResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	@Override
	public PrintWriter getWriter() {
		return pw.getWriter();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return pw.getStream();
	}

	public byte[] getBytes() {
		return pw.toByteArray();
	}

}