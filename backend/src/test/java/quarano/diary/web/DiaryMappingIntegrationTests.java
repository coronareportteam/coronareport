package quarano.diary.web;

import static org.assertj.core.api.Assertions.*;

import lombok.RequiredArgsConstructor;
import quarano.QuaranoWebIntegrationTest;
import quarano.diary.DiaryManagement;
import quarano.diary.Slot;
import quarano.diary.Slot.TimeOfDay;
import quarano.tracking.TrackedPersonDataInitializer;
import quarano.tracking.TrackedPersonRepository;
import quarano.util.TestUtils;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * @author Oliver Drotbohm
 */
@QuaranoWebIntegrationTest
@RequiredArgsConstructor
class DiaryMappingIntegrationTests {

	private final DiaryRepresentations representations;
	private final TrackedPersonRepository people;
	private final DiaryManagement diaries;
	private final ObjectMapper mapper;
	private final WebApplicationContext context;

	@Test
	void doesNotExposeCreationOfCurrentEveningsEntryInTheMorning() throws Exception {

		var person = people.findRequiredById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON3_ID_DEP2);
		var diary = diaries.findDiaryFor(person);
		var thisMorning = Slot.of(LocalDate.now(), TimeOfDay.MORNING);
		var summary = representations.toSummary(diary, person.getAccountRegistrationDate(), thisMorning);

		TestUtils.fakeRequest(HttpMethod.GET, "/api", context);

		var result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(summary);
		var document = JsonPath.parse(result);

		assertThatExceptionOfType(PathNotFoundException.class).isThrownBy(() -> {
			document.read("$._embedded.entries[0].evening._links.create");
		});
	}
}
