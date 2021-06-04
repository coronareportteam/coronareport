package quarano.occasion.web;

import static capital.scalable.restdocs.AutoDocumentation.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import lombok.RequiredArgsConstructor;
import quarano.AbstractDocumentation;
import quarano.DocumentationFlow;
import quarano.QuaranoWebIntegrationTest;
import quarano.WithQuaranoUser;
import quarano.department.TrackedCaseDataInitializer;
import quarano.occasion.OccasionCode;
import quarano.occasion.web.OccasionRepresentions.OccasionsDto;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

/**
 * Integration tests for {@link OccasionController}.
 *
 * @author David Bauknecht
 * @author Oliver Drotbohm
 * @since 1.4
 */
@RequiredArgsConstructor
@QuaranoWebIntegrationTest
class OccasionControllerWebIntegrationTests extends AbstractDocumentation {

	private final ObjectMapper objectMapper;
	private final DocumentationFlow flow = DocumentationFlow.of("create-occasions");

	@Test // CORE-613
	@WithQuaranoUser("admin")
	void createOccasionTest() throws Exception {

		var now = LocalDateTime.now();
		var payload = new OccasionsDto("Omas 80. Geburtstag", now.minusDays(7), now.minusDays(6),"Musterstraße","2","12435", "Musterstadt", "Max Mustermann", "War eine nette Feier");

		var respones = mvc.perform(post("/hd/cases/{id}/occasions", TrackedCaseDataInitializer.TRACKED_CASE_MARKUS)
				.content(objectMapper.writeValueAsString(payload))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(flow.document("create-occasion",
						responseFields().responseBodyAsType(OccasionRepresentions.OccasionSummary.class)))
				.andReturn().getResponse().getContentAsString();

		var parseDoc = JsonPath.parse(respones);

		assertThat(parseDoc.read("$.occasionCode", String.class)).isNotBlank();
	}

	@Test // CORE-613
	@WithQuaranoUser("admin")
	void deleteOccasionTest() throws Exception {

		var respones = mvc.perform(get("/hd/occasions")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(flow.document("get-occasions",
						responseFields().responseBodyAsType(OccasionRepresentions.OccasionSummary.class)))
				.andReturn().getResponse().getContentAsString();

		var parseDoc = JsonPath.parse(respones);
		String occasionCode = parseDoc.read("$._embedded.occasions[0].occasionCode", String.class);
		assertThat(occasionCode).isNotBlank();

		mvc.perform(delete("/hd/occasions/{occasion-code}", occasionCode)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(flow.document("delete-occasion",
						responseFields().responseBodyAsType(OccasionRepresentions.OccasionSummary.class)));

	}

	@Test // CORE-613
	@WithQuaranoUser("admin")
	void updateOccasionTest() throws Exception {

		var respones = mvc.perform(get("/hd/occasions")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(flow.document("get-occasions",
						responseFields().responseBodyAsType(OccasionRepresentions.OccasionSummary.class)))
				.andReturn().getResponse().getContentAsString();

		var parseDoc = JsonPath.parse(respones);
		String occasionCode = parseDoc.read("$._embedded.occasions[0].occasionCode", String.class);
		assertThat(occasionCode).isNotBlank();

		var now = LocalDateTime.now();
		var dtoUpdate = new OccasionsDto("Omas 79. Geburtstag", now.minusDays(7), now.minusDays(6), "Musterstraße", "2", "54321", "Musterstadt", "Max Mustermann", "War eine nette Feier");

		mvc.perform(put("/hd/occasions/{occasion-code}", occasionCode)
				.content(objectMapper.writeValueAsString(dtoUpdate))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(flow.document("update-occasions",
						responseFields().responseBodyAsType(OccasionRepresentions.OccasionSummary.class)));

		var responseUpdated = mvc.perform(get("/hd/occasions/{occasion-code}", occasionCode)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(flow.document("get-occasions",
						responseFields().responseBodyAsType(OccasionRepresentions.OccasionSummary.class)))
				.andReturn().getResponse().getContentAsString();

		var parseDocUpdated = JsonPath.parse(responseUpdated);
		String occasionCodeUpdated = parseDocUpdated.read("$.occasionCode", String.class);
		String zipCodeUpdated = parseDocUpdated.read("$.address.zipCode", String.class);
		String titleUpdated = parseDocUpdated.read("$.title", String.class);

		assertThat(occasionCodeUpdated).isEqualTo(occasionCode);
		assertThat(zipCodeUpdated).isEqualTo("54321");
		assertThat(titleUpdated).isEqualTo("Omas 79. Geburtstag");
	}

	@Test // CORE-613
	@WithQuaranoUser("admin")
	void updateOccasionWithInvalidOccasionCodeTest() throws Exception {
		OccasionCode occasionCode = OccasionCode.of("INVALID");

		var now = LocalDateTime.now();
		var payload = new OccasionsDto("Omas 80. Geburtstag", now.minusDays(7), now.minusDays(6), "Musterstraße", "2", "12435", "Musterstadt", "Max Mustermann", "War eine nette Feier");

		mvc.perform(put("/hd/occasions/{occasion-code}", occasionCode)
				.content(objectMapper.writeValueAsString(payload))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andDo(flow.document("update-occasions",
						responseFields().responseBodyAsType(OccasionRepresentions.OccasionSummary.class)));
	}
}
