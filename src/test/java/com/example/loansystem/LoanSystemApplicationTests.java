package com.example.loansystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.example.loansystem.loan.LoanRepository;
import com.example.loansystem.payment.PaymentRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanSystemApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@BeforeEach
	void setUp() {
		// Clean up H2 tables before each test to guarantee database consistency
		paymentRepository.deleteAll();
		loanRepository.deleteAll();
	}

	@Test
	void testLoanCreationSucceeds() throws Exception {
		String payload = "{\"loanAmount\": 15000.0, \"term\": 18}";

		mockMvc.perform(post("/loans")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.loanId", notNullValue()))
				.andExpect(jsonPath("$.loanAmount", is(15000.0)))
				.andExpect(jsonPath("$.term", is(18)))
				.andExpect(jsonPath("$.remainingBalance", is(15000.0)))
				.andExpect(jsonPath("$.status", is("ACTIVE")));
	}

	@Test
	void testPaymentReducesBalanceCorrectly() throws Exception {
		// Step 1: Create a loan
		String loanPayload = "{\"loanAmount\": 5000.0, \"term\": 12}";
		MvcResult result = mockMvc.perform(post("/loans")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loanPayload))
				.andExpect(status().isCreated())
				.andReturn();

		// Extracts generated loanId from response
		String content = result.getResponse().getContentAsString();
		String loanId = content.substring(content.indexOf("\"loanId\":\"") + 10);
		loanId = loanId.substring(0, loanId.indexOf("\""));

		// Step 2: Make a valid payment
		String paymentPayload = String.format("{\"loanId\": \"%s\", \"paymentAmount\": 1200.0}", loanId);
		mockMvc.perform(post("/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(paymentPayload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.paymentId", notNullValue()))
				.andExpect(jsonPath("$.remainingBalance", is(3800.0)))
				.andExpect(jsonPath("$.loanStatus", is("ACTIVE")));

		// Step 3: Fetch loan details and verify remaining balance
		mockMvc.perform(get("/loans/" + loanId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.remainingBalance", is(3800.0)))
				.andExpect(jsonPath("$.status", is("ACTIVE")));
	}

	@Test
	void testOverpaymentRaisesException() throws Exception {
		// Step 1: Create a loan
		String loanPayload = "{\"loanAmount\": 1000.0, \"term\": 6}";
		MvcResult result = mockMvc.perform(post("/loans")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loanPayload))
				.andExpect(status().isCreated())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		String loanId = content.substring(content.indexOf("\"loanId\":\"") + 10);
		loanId = loanId.substring(0, loanId.indexOf("\""));

		// Step 2: Make an overpayment (amount exceeds remaining balance)
		String overpaymentPayload = String.format("{\"loanId\": \"%s\", \"paymentAmount\": 1050.0}", loanId);
		mockMvc.perform(post("/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(overpaymentPayload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error", containsString("Payment exceeds remaining balance")));
	}

	@Test
	void testFullyPaidStatusMovesToSettled() throws Exception {
		// Step 1: Create a loan
		String loanPayload = "{\"loanAmount\": 3000.0, \"term\": 12}";
		MvcResult result = mockMvc.perform(post("/loans")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loanPayload))
				.andExpect(status().isCreated())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		String loanId = content.substring(content.indexOf("\"loanId\":\"") + 10);
		loanId = loanId.substring(0, loanId.indexOf("\""));

		// Step 2: Pay partially ($1000)
		mockMvc.perform(post("/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.format("{\"loanId\": \"%s\", \"paymentAmount\": 1000.0}", loanId)))
				.andExpect(status().isCreated());

		// Step 3: Pay the remaining balance ($2000) to settle
		mockMvc.perform(post("/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.format("{\"loanId\": \"%s\", \"paymentAmount\": 2000.0}", loanId)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.remainingBalance", is(0.0)))
				.andExpect(jsonPath("$.loanStatus", is("SETTLED")));

		// Step 4: Verify status is SETTLED in DB via GET
		mockMvc.perform(get("/loans/" + loanId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.remainingBalance", is(0.0)))
				.andExpect(jsonPath("$.status", is("SETTLED")));
	}
}
