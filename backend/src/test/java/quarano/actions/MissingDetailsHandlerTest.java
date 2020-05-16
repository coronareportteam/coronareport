package quarano.actions;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import quarano.QuaranoUnitTest;
import quarano.account.Department;
import quarano.department.CaseType;
import quarano.department.TrackedCase;
import quarano.tracking.TrackedPerson;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

@QuaranoUnitTest
class MissingDetailsHandlerTest {
	@Mock private ActionItemRepository items;

	private MissingDetailsHandler missingDetailsHandler;

	@BeforeEach
	void setup() {
		missingDetailsHandler = new MissingDetailsHandler(items);
	}

	@ParameterizedTest(name = "{index} creates a new ActionItem for {0}")
	@MethodSource("trackedCases")
	void testHandleMissingDetailsCreated(DescriptionCode descriptionCode, TrackedCase trackedCase) {
		var spyCase = spy(trackedCase);
		var person = spyCase.getTrackedPerson();
		when(spyCase.isInvestigationNeeded()).thenReturn(true);
		when(items.findByDescriptionCode(person.getId(), descriptionCode)).thenReturn(ActionItems.empty());

		assertThatCode(() -> missingDetailsHandler.handleTrackedCaseMissingDetails(spyCase)).doesNotThrowAnyException();

		var itemsCaptor = forClass(ActionItem.class);
		verify(items, times(1)).save(itemsCaptor.capture());
		assertThat(itemsCaptor.getValue().getPersonIdentifier()).isEqualTo(person.getId());
		assertThat(itemsCaptor.getValue().isResolved()).isEqualTo(false);
		assertThat(itemsCaptor.getValue().getDescription().getCode()).isEqualTo(descriptionCode);
	}

	@ParameterizedTest(name = "{index} does not overwrite ActionItem for {0}")
	@MethodSource("trackedCases")
	void testHandleMissingDetailsDoesNotOverwrite(DescriptionCode descriptionCode, TrackedCase trackedCase) {
		var spyCase = spy(trackedCase);
		var person = spyCase.getTrackedPerson();
		when(spyCase.isInvestigationNeeded()).thenReturn(true);
		var actionItem = mock(ActionItem.class);
		when(items.findByDescriptionCode(person.getId(), descriptionCode)).thenReturn(ActionItems.of(actionItem));

		assertThatCode(() -> missingDetailsHandler.handleTrackedCaseMissingDetails(spyCase)).doesNotThrowAnyException();

		verify(items, times(0)).save(any());
	}

	@ParameterizedTest(name = "{index} resolves ActionItems for {0}")
	@MethodSource("trackedCases")
	void testHandleMissingDetailsDoesResolves(DescriptionCode descriptionCode, TrackedCase trackedCase) {
		var spyCase = spy(trackedCase);
		var person = spyCase.getTrackedPerson();
		when(spyCase.isInvestigationNeeded()).thenReturn(false);
		var actionItems = mock(ActionItems.class);
		when(items.findByDescriptionCode(person.getId(), descriptionCode)).thenReturn(actionItems);

		assertThatCode(() -> missingDetailsHandler.handleTrackedCaseMissingDetails(spyCase)).doesNotThrowAnyException();

		verify(actionItems, times(1)).resolveAutomatically(any());
	}

	private Stream<Arguments> trackedCases() {
		return Stream.of(
				arguments(DescriptionCode.MISSING_DETAILS_INDEX,
						new TrackedCase(new TrackedPerson("firstName", "lastName"), CaseType.INDEX, new Department("test"))),
				arguments(DescriptionCode.MISSING_DETAILS_CONTACT,
						new TrackedCase(new TrackedPerson("firstName", "lastName"), CaseType.CONTACT, new Department("test"))));
	}
}
