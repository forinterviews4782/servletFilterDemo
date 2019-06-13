package com.demo.metrics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ByteArrayServletStream extends ServletOutputStream {

	ByteArrayOutputStream outputStream;

	ByteArrayServletStream(ByteArrayOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void write(int param) throws IOException {
		outputStream.write(param);
	}

	@Override
	public boolean isReady() {
		// Required by Servlet 3.1 but does not get called as far as I can tell
		return false;
	}

	@Override
	public void setWriteListener(WriteListener listener) {
		// Required by Servlet 3.1 but does not get called as far as I can tell
		return;
	}

}