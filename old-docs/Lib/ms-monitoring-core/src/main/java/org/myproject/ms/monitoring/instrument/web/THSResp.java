

package org.myproject.ms.monitoring.instrument.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myproject.ms.monitoring.Item;


class THSResp extends HttpServletResponseWrapper {

	private static final Log log = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final Item span;

	THSResp(HttpServletResponse response, Item span) {
		super(response);
		this.span = span;
	}

	@Override public void flushBuffer() throws IOException {
		if (log.isTraceEnabled()) {
			log.trace("Will annotate SS once the response is flushed");
		}
		SsLogSetter.annotateWithServerSendIfLogIsNotAlreadyPresent(this.span);
		super.flushBuffer();
	}

	@Override public ServletOutputStream getOutputStream() throws IOException {
		return new TSOStr(super.getOutputStream(), this.span);
	}

	@Override public PrintWriter getWriter() throws IOException {
		return new TPWriter(super.getWriter(), this.span);
	}
}
