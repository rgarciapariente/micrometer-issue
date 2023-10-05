package com.example.testobservabilityerror;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureObservability
@AutoConfigureWebTestClient
@SpringBootTest
class TestWithoutFilterTests {

	@Autowired
	WebTestClient testClient;

	@Test
	@Order(10)
	void withoutAutomaticPropagationAllsWorksOK() throws InterruptedException {

		Hooks.disableAutomaticContextPropagation();

		Set traceIDs = new HashSet<>();

		for (int i = 0; i < 15; i++) {
			testTraceId(traceIDs);
		}
	}

	@Test
	@Order(20)
	void withAutomaticPropagationAllsWorksKOError1() throws InterruptedException {

		Hooks.enableAutomaticContextPropagation();


		Set traceIDs = new HashSet<>();

		for (int i = 0; i < 15; i++) {
			testTraceId(traceIDs);
		}
	}


	void testTraceId(Set set) {
		testClient.get().uri("/test-traceid").exchange()
					.expectStatus().isOk().expectBody().jsonPath("$").value(o -> {
					assertFalse(set.contains(o));
					set.add(o);
				});
	}


}
