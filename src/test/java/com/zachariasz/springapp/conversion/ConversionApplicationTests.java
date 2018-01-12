package com.zachariasz.springapp.conversion;

import com.zachariasz.springapp.conversion.exception.IncorrectInputDataException;
import com.zachariasz.springapp.conversion.model.Exchange;
import com.zachariasz.springapp.conversion.service.ExchangeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;


import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConversionApplicationTests {
	private WebApplicationContext webApplicationContext;
	private ExchangeService exchangeService;

	@Autowired
	public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
	}
	@Autowired
	public void setExchangeService(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

	MockMvc mockMvc;

	@Before
	public void setUp(){
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void checkExchangeTest(){
		checkExchangeTestingMethod(new Exchange("USD", 2.0), "");
		checkExchangeTestingMethod(new Exchange("USD", 0.0), "VALUE should not: be zero");
		checkExchangeTestingMethod(new Exchange("USD", null), "VALUE cannot be null");
		checkExchangeTestingMethod(new Exchange("US", 2.0), "ID should contain: 3 chars");
		checkExchangeTestingMethod(new Exchange("U@", 2.0), "ID should contain: 3 chars, only a-z or A-Z letters");
		checkExchangeTestingMethod(new Exchange("U@", 0.0), "ID should contain: 3 chars, only a-z or A-Z letters VALUE should not: be zero");
		checkExchangeTestingMethod(new Exchange("U@", null), "ID should contain: 3 chars, only a-z or A-Z letters VALUE cannot be null");
		checkExchangeTestingMethod(new Exchange(null, 0.0), "ID cannot be null VALUE should not: be zero");
	}



	private void checkExchangeTestingMethod(Exchange exchange, String desiredResult) {
		String result = exchangeService.checkExchange(exchange);
		assertEquals(desiredResult, result);
	}

}
