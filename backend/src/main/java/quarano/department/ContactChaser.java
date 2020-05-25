package quarano.department;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import quarano.department.TrackedCase.TrackedCaseIdentifier;
import quarano.tracking.ContactPerson;
import quarano.tracking.DiaryEntry;
import quarano.tracking.DiaryEntryRepository;
import quarano.tracking.Encounter;
import quarano.tracking.TrackedPerson;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author Felix Schultze
 * @author Oliver Drotbohm
 */
@Component
@RequiredArgsConstructor
public class ContactChaser {

	private final @NonNull TrackedCaseRepository cases;
	private final @NonNull DiaryEntryRepository diaries;

	public Stream<Contact> findIndexContactsFor(TrackedCase contactCase) {

		if (!contactCase.isContactCase() && !contactCase.isMedicalContactCase()) {
			return Stream.empty();
		}

		return contactCase.getOriginContacts().stream().distinct() //
				.flatMap(it -> cases.findByTrackedPerson(it.getOwnerId()) //
						.map(indexCase -> fromIndexCase(indexCase, it)) //
						.stream());
	}

	// having that index case, we need to find the contact date per encounters of index person
	private Contact fromIndexCase(TrackedCase indexCase, ContactPerson contactPerson) {

		LocalDate encounterDate = indexCase.getTrackedPerson().getEncounters().stream() //
				.filter(it -> it.isEncounterWith(contactPerson)) //
				.findFirst() //
				.map(Encounter::getDate) //
				.orElse(null);

		if (encounterDate == null) {
			// there are no encounters created for diary entries. so we need to find the contact person in the diaries
			encounterDate = diaries.findByTrackedPersonId(indexCase.getTrackedPerson().getId())
					.stream()
					.filter(diary -> diary.getContacts().contains(contactPerson))
					.map(DiaryEntry::getSlotDate)
					.sorted()
					.findFirst()
					.orElse(null);
		}

		return new Contact(indexCase, contactPerson, encounterDate);
	}

	@Getter
	public static class Contact {

		private final TrackedCaseIdentifier caseId;
		private final TrackedPerson person;
		private final LocalDate contactAt;
		private final ContactPerson contactPerson;

		private Contact(TrackedCase trackedCase, ContactPerson contactPerson, @Nullable LocalDate contactAt) {
			this.caseId = trackedCase.getId();
			this.person = trackedCase.getTrackedPerson();
			this.contactAt = contactAt;
			this.contactPerson = contactPerson;
		}
	}
}
