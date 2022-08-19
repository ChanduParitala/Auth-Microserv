package com.mfpe.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mfpe.exception.ProjectManagerNotFoundException;
import com.mfpe.model.ProjectManagerDetails;
import com.mfpe.service.JwtService;
import com.mfpe.service.ProjectManagerDetailsService;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	public JwtRequestFilter(ProjectManagerDetailsService projectManagerDetailsService, JwtService jwtService) {
		this.projectManagerDetailsService = projectManagerDetailsService;
		this.jwtService = jwtService;
	}

	private ProjectManagerDetailsService projectManagerDetailsService;

	private JwtService jwtService;

	static final Logger log = LoggerFactory.getLogger("Jwt-Request-Filter");

	// an added security layer to authorize all the requests if they have valid jwt
	// or not
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, ProjectManagerNotFoundException {

		final String jwtRequestHeader = request.getHeader("Authorization");
		// comments
		// comments

		logger.info("Inside JwtRequestFilter : " + request.getRequestURI());

		String jwt = null, username = null;
		// comments
		// comments
		if (jwtRequestHeader != null && jwtRequestHeader.startsWith("Bearer ")) {
			jwt = jwtRequestHeader.substring(7);
			// comments
			// comments
			try {
				username = jwtService.extractUsername(jwt);
				logger.info("Successfully obtained username : (" + username + ") from JWT");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.error("Problem with JWT token obtained from Request-Header. JWT :: " + jwt);
		}
		// comments
		// comments
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			ProjectManagerDetails projectManagerDetails = projectManagerDetailsService.loadUserByUsername(username);

			// comments
			// comments
			if (jwtService.validateToken(jwt, projectManagerDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						projectManagerDetails, null, projectManagerDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				logger.info("Successfully obtained and validated JWT :: " + jwt);
			} else {
				logger.error("Validation failed for JWT :: " + jwt);
			}
		} else {
			logger.error("Problem with JWT token obtained from Request-Header. JWT :: " + jwt);
		}
		logger.info("-------- Exiting JwtRequestFilter");
		filterChain.doFilter(request, response);
	}
}
