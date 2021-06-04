package quarano.occasion;

import lombok.RequiredArgsConstructor;
import quarano.core.Address;
import quarano.core.ZipCode;
import quarano.department.TrackedCase.TrackedCaseIdentifier;
import quarano.department.TrackedCaseRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

/**
 * Application service to manage {@link Occasion}s.
 *
 * @author David Bauknecht
 * @author Oliver Drotbohm
 * @since 1.4
 */
@Service
@RequiredArgsConstructor
public class OccasionManagement {

	private final OccasionRepository occasions;
	private final @NotNull TrackedCaseRepository trackedCaseRepository;

	/**
	 * Creates a new {@link Occasion} with the given title, start and end date.
	 *
	 * @param title must not be {@literal null} or empty.
	 * @param start must not be {@literal null}.
	 * @param end must not be {@literal null}.
	 * @param street
	 * @param s
	 * @param postalCode
	 * @param city
	 * @param contactPerson
	 * @param additionalInformation
	 * @param trackedCaseId the {@link TrackedCaseIdentifier} for the case which the {@link Occasion} to be created shall
	 *          be associated with. Must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	public Optional<Occasion> createOccasion(String title, LocalDateTime start, LocalDateTime end,
											 String street, String houseNumber, String zipCode, String city, String additionalInformation, String contactPerson, TrackedCaseIdentifier trackedCaseId) {
		Address address = new Address(street, Address.HouseNumber.of(houseNumber), city, ZipCode.of(zipCode));
		return !trackedCaseRepository.existsById(trackedCaseId)
				? Optional.empty()
				: Optional.of(occasions.save(new Occasion(title, start, end, address,additionalInformation, contactPerson, findValidOccasionCode(), trackedCaseId)));
	}

	/**
	 * Updates the {@link Occasion} that has the given {@link OccasionCode} assigned.
	 *
	 * @param trackedCaseId
	 * @param occasion must not be {@literal null}.
	 * @param id
	 * @return will never be {@literal null}.
	 */
	public Occasion updateOccasionBy(String title, LocalDateTime start, LocalDateTime end,
									 String street, String houseNumber, String zipCode, String city, String additionalInformation, String contactPerson, Occasion existing) {
		Address address = new Address(street, Address.HouseNumber.of(houseNumber), city, ZipCode.of(zipCode));
		existing.setTitle(title);
		existing.setStart(start);
		existing.setEnd(end);
		existing.setAddress(address);
		existing.setAdditionalInformation(additionalInformation);
		existing.setContactPerson(contactPerson);
		return occasions.save(existing);
	}

	/**
	 * Registers the given {@link VisitorGroup} for the {@link Occasion} for which the given {@link OccasionCode} was
	 * registered.
	 *
	 * @param eventCode must not be {@literal null}.
	 * @param visitorGroup must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	public Optional<Occasion> registerVisitorGroupForEvent(OccasionCode eventCode, VisitorGroup visitorGroup) {

		return occasions.findByOccasionCode(eventCode)
				.map(e -> e.registerVisitorGroup(visitorGroup))
				.map(occasions::save);
	}

	/**
	 * Returns the {@link Occasion} that has the given {@link OccasionCode} assigned.
	 *
	 * @param eventCode must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	public Optional<Occasion> findOccasionBy(OccasionCode eventCode) {
		return occasions.findByOccasionCode(eventCode);
	}

	/**
	 * Returns all {@link Occasion}s.
	 *
	 * @return will never be {@literal null}.
	 */
	public Streamable<Occasion> findAll() {
		return occasions.findAll();
	}

	private OccasionCode findValidOccasionCode() {

		OccasionCode occasionCode = OccasionCode.generate();

		return occasions.isOccasionCodeAvailable(occasionCode)
				? occasionCode
				: findValidOccasionCode();
	}

    public void deleteOccasion(Occasion occasion) {
		occasions.delete(occasion);
    }
}
