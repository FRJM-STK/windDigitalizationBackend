package com.ge.predix.solsvc.boot;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class CrossSiteScriptFilter extends OncePerRequestFilter {
	private static final Log LOG = LogFactory.getLog(CrossSiteScriptFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		LOG.debug("Is setting up the headers in the filter............");
		if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
			LOG.trace("Setting Header....");
			response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
			response.addHeader("Access-Control-Max-Age", "1");
		}
		
		filterChain.doFilter(request, response);
	}

}