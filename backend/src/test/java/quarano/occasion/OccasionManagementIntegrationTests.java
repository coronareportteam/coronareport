package quarano.occasion;

import static org.assertj.core.api.Assertions.*;

import lombok.RequiredArgsConstructor;
import quarano.QuaranoWebIntegrationTest;
import quarano.department.TrackedCaseDataInitializer;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * @author David Bauknecht
 * @author Oliver Drotbohm
 * @since 1.4
 */
@QuaranoWebIntegrationTest
@RequiredArgsConstructor
class OccasionManagementIntegrationTests {

	private final OccasionManagement occasions;
	private final LocalDate today = LocalDate.now();

	@Test // CORE-631
	void testCreateEvent() {

		var event = occasions.createOccasion("TestEvent", today.atStartOfDay(), today.plusDays(1).atStartOfDay(),
				"Musterstraße", "2", "12345", "Musterstadt", "Event Location ftw", "Oma Gerda", TrackedCaseDataInitializer.TRACKED_CASE_MARKUS);

		assertThat(event).map(Occasion::getOccasionCode).isPresent();
	}

	@Test // CORE-631
	void testCreateEventAndAddGroup() {

		var event = occasions.createOccasion("TestEvent 2", today.atStartOfDay(), today.plusDays(1).atStartOfDay(),
                "Musterstraße", "2", "12345", "Musterstadt", "Event Location ftw", "Oma Gerda", TrackedCaseDataInitializer.TRACKED_CASE_MARKUS).orElseThrow();

		var visitorGroup = new VisitorGroup(today.atStartOfDay(), event.getOccasionCode())
				.setComment("Comment")
				.setEnd(today.plusDays(1).atStartOfDay())
				.setLocationName("Event Location ftw")
				.setVisitors(new ArrayList<>());

		var eventOptional = occasions.registerVisitorGroupForEvent(event.getOccasionCode(), visitorGroup);

		assertThat(eventOptional).hasValueSatisfying(it -> {
			assertThat(it.getVisitorGroups()).hasSize(1);
		});
	}
}
