

package org.myproject.ms.monitoring.instrument.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


class ServletUtils {

	static String getHeader(HttpServletRequest request, HttpServletResponse response,
			String name) {
		String value = request.getHeader(name);
		return value != null ? value : response.getHeader(name);
	}

}
