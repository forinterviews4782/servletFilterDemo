package com.demo.metrics;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

public class ByteArrayPrinter {

	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	private PrintWriter writer = new PrintWriter(byteArrayOutputStream);

	private ServletOutputStream servletOutputStream = new ByteArrayServletStream(byteArrayOutputStream);

	protected PrintWriter getWriter() {
		return writer;
	}

	protected ServletOutputStream getStream() {
		return servletOutputStream;
	}

	protected byte[] toByteArray() {
		return byteArrayOutputStream.toByteArray();
	}
}
