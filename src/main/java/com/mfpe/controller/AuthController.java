package com.mfpe.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mfpe.model.AuthenticationRequest;
import com.mfpe.model.AuthenticationResponse;
import com.mfpe.model.ProjectManagerDetails;
import com.mfpe.service.JwtService;
import com.mfpe.service.ProjectManagerDetailsService;

/**
 * This is the controller class for creating and validating JWT Tokens.
 */
@RestController
@RequestMapping("/auth") // Context Root
@CrossOrigin(origins = "*")
public class AuthController {

	private AuthenticationManager authenticationManager;

	/**
     * Constructor based injection.
     * */
	public AuthController(AuthenticationManager authenticationManager, ProjectManagerDetails projectManagerDetails,
			ProjectManagerDetailsService projectManagerDetailsService, JwtService jwtService) {

		this.authenticationManager = authenticationManager;
		this.projectManagerDetails = projectManagerDetails;
		this.projectManagerDetailsService = projectManagerDetailsService;
		this.jwtService = jwtService;
	}

	private ProjectManagerDetails projectManagerDetails;

	private ProjectManagerDetailsService projectManagerDetailsService;

	private JwtService jwtService;

	static final Logger log = LoggerFactory.getLogger("Auth-Controller-Logger");

	/**
     * this is a health check method
     * prints response in http page*/
	
	@GetMapping("/health-check")
	public ResponseEntity<String> healthCheck() { // for Health check [PERMITTED FOR ALL]
		return new ResponseEntity<String>("Audit-Authorization MS Running Fine!!", HttpStatus.OK);
	}

	// authentication - for the very first login
	  /**
     * After validation of username and password it will generate the token
     * if incorrect userdetails then it will go through GlobalException
     * 
     * @param authenticationRequest
     * @throws BadCredentialsException*/
	@PostMapping("/authenticate")
	public ResponseEntity<String> generateJwt(@Valid @RequestBody AuthenticationRequest request) {
		ResponseEntity<String> response = null;

		// authenticating the User-Credentials
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			// else when it authenticates successfully
			final ProjectManagerDetails projectManagerDetails = projectManagerDetailsService
					.loadUserByUsername(request.getUsername());

			final String jwt = jwtService.generateToken(projectManagerDetails); // returning the token as response

			// test
			log.info("Authenticated User :: " + projectManagerDetails);

			response = new ResponseEntity<String>(jwt, HttpStatus.OK);

			log.info("Successfully Authenticated user!");

		} catch (Exception e) {
			log.error(e.getMessage() + "!! info about request-body : " + request);
			response = new ResponseEntity<String>("Not Authorized Project Manager", HttpStatus.FORBIDDEN);
		}
		log.info("-------- Exiting /authenticate");
		return response;
	}

	// validate - for every request it validates the user-credentials from the
	// provided Jwt token in Authorization req. header
	/**
     * @param token - to validate the token
     * Sending the request header as "Authorization"
     * @return the validity of token
     */
	@PostMapping("/validate")
	public ResponseEntity<AuthenticationResponse> validateJwt(@RequestHeader("Authorization") String jwt) {

		AuthenticationResponse authenticationResponse = new AuthenticationResponse("Invalid", "Invalid", false);
		ResponseEntity<AuthenticationResponse> response = null;

		// first remove Bearer from Header
		jwt = jwt.substring(7);

		// check token
		log.info("--------JWT :: " + jwt);

		// check the jwt is proper or not
		 final ProjectManagerDetails projectManagerDetails =
		 projectManagerDetailsService
		 .loadUserByUsername(jwtService.extractUsername(jwt));

		// now validating the jwt
		try {
			if (jwtService.validateToken(jwt, projectManagerDetails)) {
				authenticationResponse.setName(projectManagerDetails.getName());
				authenticationResponse.setProjectName(projectManagerDetails.getProjectName());
				authenticationResponse.setValid(true);
				response = new ResponseEntity<AuthenticationResponse>(authenticationResponse, HttpStatus.OK);
				log.info("Successfully validated the jwt and sending response back!");
			} else {
				response = new ResponseEntity<AuthenticationResponse>(authenticationResponse, HttpStatus.OK);
				log.error("JWT Token validation failed!");
			}
		} catch (Exception jwtException) {
			log.error(jwtException.getMessage());
			response = new ResponseEntity<AuthenticationResponse>(authenticationResponse, HttpStatus.OK);
			log.error("Exception occured whil validating JWT : Exception info : " + jwtException.getMessage());
		}
		log.info("-------- Exiting /validate");
		return response;
	}

}
