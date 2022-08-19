package com.mfpe;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Gamma
 *
 */
@SpringBootTest
class AuditAuthorizationApplicationTests {

	@Autowired
	AuditAuthorizationApplication auditAuthorizationApplication;
	@Test
	void contextLoads() {
		// simple test class
		assertNotNull(auditAuthorizationApplication);
	}

}
