package quarano.occasion;

import static org.assertj.core.api.Assertions.*;

import lombok.RequiredArgsConstructor;
import quarano.QuaranoWebIntegrationTest;
import quarano.core.QuaranoDateTimeProvider;
import quarano.department.TrackedCaseDataInitializer;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * @author Jens Kutzsche
 * @since 1.4
 */
@QuaranoWebIntegrationTest
@RequiredArgsConstructor
class GDPRDeleteJobIntegrationTests {

	private final OccasionManagement occasions;
	private final QuaranoDateTimeProvider dateTimeProvider;
	private final GDPRDeleteJob gdprDeleteJob;
	private final LocalDate today = LocalDate.now();

	@Test // CORE-294
	void testDeleteOccasions() {

		dateTimeProvider.setDelta(Period.ofMonths(-6));

		createOccasion("AAA", "AA", "A", "B", "C");

		dateTimeProvider.setDelta(Period.ofMonths(-6).minusDays(1));

		createOccasion("DDD", "DD", "D", "E", "F");

		dateTimeProvider.reset();

		var all = occasions.findAll();

		// one extra element from data initialization
		assertThat(all).hasSize(3).element(2).satisfies(it -> {
			assertThat(it.getTitle()).isEqualTo("DDD");
			assertThat(it.getVisitorGroups()).singleElement().satisfies(it2 -> {
				assertThat(it2.getVisitors()).hasSize(3);
			});
		});

		gdprDeleteJob.deleteOccasions();

		all = occasions.findAll();

		assertThat(all).hasSize(2).noneMatch(it -> it.getTitle().equals("DDD"));
	}

	@Test // CORE-294
	void testDeleteVisitors() {

		dateTimeProvider.setDelta(Period.ofDays(-16));

		createOccasion("AAA", "AA", "A", "B", "C");

		dateTimeProvider.setDelta(Period.ofDays(-17));

		createOccasion("DDD", "DD", "D", "E", "F");

		dateTimeProvider.reset();

		var all = occasions.findAll();

		// one extra element from data initialization
		assertThat(all).hasSize(3).element(2).satisfies(it -> {
			assertThat(it.getTitle()).isEqualTo("DDD");
			assertThat(it.getVisitorGroups()).singleElement().satisfies(it2 -> {
				assertThat(it2.getVisitors()).hasSize(3);
			});
		});

		gdprDeleteJob.deleteVisitors();

		all = occasions.findAll();

		assertThat(all).hasSize(3).element(2).satisfies(it -> {
			assertThat(it.getTitle()).isEqualTo("DDD");
			assertThat(it.getVisitorGroups()).isEmpty();
		});
	}

	private void createOccasion(String title, String locationName, String... visitorNames) {

/* TODO 614		var event = occasions.createOccasion(title, today.atStartOfDay(), today.plusDays(1).atStartOfDay(),
				TrackedCaseDataInitializer.TRACKED_CASE_MARKUS).orElseThrow();

		var visitors = Arrays.stream(visitorNames)
				.map(it -> new Visitor(it.toUpperCase(), it.toLowerCase(), null))
				.collect(Collectors.toList());

		var visitorGroup = new VisitorGroup(today.atStartOfDay(), event.getOccasionCode())
				.setLocationName(locationName)
				.setVisitors(visitors);

		occasions.registerVisitorGroupForEvent(event.getOccasionCode(), visitorGroup);*/
	}
}
