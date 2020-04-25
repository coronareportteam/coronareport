package quarano.auth.web;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import lombok.RequiredArgsConstructor;
import quarano.QuaranoWebIntegrationTest;
import quarano.auth.ActivationCodeDataInitializer;
import quarano.tracking.TrackedPerson;
import quarano.tracking.TrackedPersonDataInitializer;
import quarano.tracking.TrackedPersonRepository;
import quarano.util.TokenResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@QuaranoWebIntegrationTest
@RequiredArgsConstructor
@DirtiesContext
class RegistrationWebIntegrationTests {

	private final MockMvc mvc;
	private final ObjectMapper mapper;
	private final TrackedPersonRepository repository;

	@Test
	public void registerNewAccountForClientSuccess() throws Exception {

		// Given
		var person = repository.findById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON1_ID_DEP1).orElseThrow();

		var password = "myPassword";
		var username = "testusername";

		var registrationDto = AccountRegistrationDto.builder() //
				.username(username) //
				.password(password) //
				.passwordConfirm(password) //
				.dateOfBirth(person.getDateOfBirth()) //
				.email("mytestmail@test.com") //
				.clientCode(UUID.fromString(ActivationCodeDataInitializer.ACTIVATIONCODE_PERSON1.getId().toString())) //
				.build();

		// when
		mvc.perform(post("/api/registration") //
				.header("Origin", "*").contentType(MediaType.APPLICATION_JSON) //
				.content(mapper.writeValueAsString(registrationDto))) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

		// then check if login works with new account
		String token = loginAndCheckSuccess(password, username);

		// and check if token is valid for authentication
		callUserMeAndCheckSuccess(token, person);
	}

	@Test
	public void registerNewAccountWithInvalidActivationCodeFails() throws Exception {

		var person = repository.findById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON1_ID_DEP1).orElseThrow();

		var password = "myPassword";
		var username = "testusername";

		var registrationDto = AccountRegistrationDto.builder() //
				.username(username) //
				.password(password) //
				.passwordConfirm(password) //
				.dateOfBirth(person.getDateOfBirth()) //
				.email("mytestmail@test.com") //
				.clientCode(UUID.randomUUID()) //
				.build();

		// when
		var result = mvc.perform(post("/api/registration") //
				.header("Origin", "*").contentType(MediaType.APPLICATION_JSON) //
				.content(mapper.writeValueAsString(registrationDto))) //
				.andExpect(status().isBadRequest()) //
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) //
				.andReturn().getResponse().getContentAsString();

		var document = JsonPath.parse(result);

		assertThat(document.read("$.clientCode", String.class)).isNotNull();

		// then check that login does not work with new account
		checkLoginFails(username, password);
	}

	@Test
	public void registerAccountWithExistingUsernameFails() throws Exception {

		var person = repository.findById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON1_ID_DEP1).orElseThrow();
		var password = "myPassword";

		var registrationDto = AccountRegistrationDto.builder() //
				.username("DemoAccount") //
				.password(password) //
				.passwordConfirm(password) //
				.dateOfBirth(person.getDateOfBirth()) //
				.email("mytestmail@test.com") //
				.clientCode(UUID.fromString(ActivationCodeDataInitializer.ACTIVATIONCODE_PERSON1.getId().toString())) //
				.build();

		// when
		var responseBody = mvc.perform(post("/api/registration") //
				.header("Origin", "*") //
				.contentType(MediaType.APPLICATION_JSON) //
				.content(mapper.writeValueAsString(registrationDto))) //
				.andExpect(status().isBadRequest()) //
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) //
				.andReturn().getResponse().getContentAsString();

		assertThat(responseBody).isNotBlank();

		var document = JsonPath.parse(responseBody);

		assertThat(document.read("$.username", String.class)).isNotNull();

		checkLoginFails("DemoAccount", password);
	}

	@Test
	@Disabled
	void testCheckClientCodeWithValidCode() throws Exception {

		var code = ActivationCodeDataInitializer.ACTIVATIONCODE_PERSON1;

		String resultAsString = mvc
				.perform(get("/client/checkcode/" + code).header("Origin", "*").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		Assertions.assertTrue(Boolean.parseBoolean(resultAsString));

	}

	@Test
	@Disabled
	void testCheckClientCodeWithInvalidCodeFailsWith404() throws Exception {

		var code = ActivationCodeDataInitializer.ACTIVATIONCODE_PERSON3_CANCELED;

		mvc.perform(get("/client/checkcode/" + code).header("Origin", "*").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
	}

	@Test
	@Disabled
	void testCheckClientCodeWithoutCodeFailsWith400() throws Exception {

		mvc.perform(get("/client/checkcode/").header("Origin", "*").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
	}

	private void callUserMeAndCheckSuccess(String token, TrackedPerson person) throws Exception {

		String resultDtoStr = mvc.perform(get("/api/user/me") //
				.header("Origin", "*") //
				.header("Authorization", "Bearer " + token) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) //
				.andReturn().getResponse().getContentAsString();

		var document = JsonPath.parse(resultDtoStr);

		assertThat(document.read("$.client.firstName", String.class)).isEqualTo(person.getFirstName());
		assertThat(document.read("$.client.lastName", String.class)).isEqualTo(person.getLastName());
	}

	private String loginAndCheckSuccess(final String password, final String username)
			throws UnsupportedEncodingException, Exception, IOException {
		// login with new account
		String requestbody = createLoginRequestBody(username, password);

		String jwtToken = mvc.perform(post("/login") //
				.header("Origin", "*") //
				.contentType(MediaType.APPLICATION_JSON) //
				.content(requestbody)).andExpect(status().isOk()) //
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn().getResponse()
				.getContentAsString();

		TokenResponse response = mapper.readValue(jwtToken, TokenResponse.class);

		return response.getToken();
	}

	private void checkLoginFails(String username, String password) throws Exception {

		// login with new account
		var requestbody = createLoginRequestBody(username, password);

		mvc.perform(post("/login") //
				.header("Origin", "*") //
				.contentType(MediaType.APPLICATION_JSON) //
				.content(requestbody)) //
				.andExpect(status().isUnauthorized());
	}

	public String createLoginRequestBody(String username, String password) throws Exception {
		return mapper.writeValueAsString(Map.of("username", username, "password", password));
	}
}
