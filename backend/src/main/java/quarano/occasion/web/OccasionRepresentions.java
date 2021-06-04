package quarano.occasion.web;

import io.vavr.collection.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import quarano.core.PhoneNumber;
import quarano.core.validation.Email;
import quarano.core.validation.Strings;
import quarano.core.validation.Textual;
import quarano.core.web.MapperWrapper;
import quarano.occasion.Occasion;
import quarano.occasion.OccasionCode;
import quarano.occasion.VisitorGroup;
import quarano.core.ZipCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author David Bauknecht
 * @author Oliver Drotbohm
 * @since 1.4
 */
@RequiredArgsConstructor
@Component
class OccasionRepresentions {

	private final MapperWrapper mapperWrapper;
	private final ProjectionFactory projections;

	VisitorGroup from(VisitorGroupDto visitorTransmissionDto) {
		return mapperWrapper.map(visitorTransmissionDto, VisitorGroup.class);
	}

	OccasionSummary toSummary(Occasion occasion) {
		return projections.createProjection(OccasionSummary.class, occasion);
	}

	@Data
	@AllArgsConstructor
	public static class OccasionsDto {

		/**
		 * The title of the occasion.
		 */
		@Textual String title;

		/**
		 * The start date and time of the occasion.
		 */
		LocalDateTime start;

		/**
		 * The end date and time of the occasion.
		 */
		LocalDateTime end;

		@Pattern(regexp = Strings.STREET) String street;

		@Pattern(regexp = Strings.HOUSE_NUMBER) String houseNumber;

		@Pattern(regexp = ZipCode.PATTERN) String zipCode;

		@Pattern(regexp = Strings.CITY) String city;

		@Textual String contactPerson;

		@Textual String additionalInformation;
	}

	interface OccasionSummary {

		String getTitle();

		LocalDateTime getStart();

		LocalDateTime getEnd();

		OccasionAddress getAddress();

		String getAdditionalInformation();

		String getContactPerson();

		/**
		 * An 8-digit occasion code to be handed to location owners or third-party software to report visitor groups. Note
		 * the absence of characters that might be ambiguous when transmitted verbally or in hand writing (I, J, 1, O, 0).
		 * See <<third-party.visitor-groups>> for details.
		 */
		@Pattern(regexp = OccasionCode.REGEX)
		String getOccasionCode();

		List<VisitorGroupSummary> getVisitorGroups();

		interface OccasionAddress{

			String getStreet();

			String getHouseNumber();

			String getZipCode();

			String getCity();

		}

		interface VisitorGroupSummary {

			List<VisitorSummary> getVisitors();

			interface VisitorSummary {

				String getFirstName();

				String getLastName();

				AddressSummary getAddress();

				interface AddressSummary {

					String getStreet();

					String getHouseNumber();

					String getZipCode();

					String getCity();
				}
			}
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class VisitorDto {

		private @NotBlank @Pattern(regexp = Strings.NAMES) String lastName, firstName;
		private @NotBlank @Pattern(regexp = Strings.STREET) String street;
		private @NotBlank @Pattern(regexp = Strings.HOUSE_NUMBER) String houseNumber;
		private @NotBlank @Pattern(regexp = ZipCode.PATTERN) String zipCode;
		private @NotBlank @Pattern(regexp = Strings.CITY) String city;

		/**
		 * The date the visitor entered the occasion, if known.
		 */
		private @Past LocalDateTime checkin;

		/**
		 * The date the visitor left the occasion, if known.
		 */
		private @Past LocalDateTime checkout;

		/**
		 * If not given, email must be given.
		 **/
		private @Pattern(regexp = PhoneNumber.PATTERN) String phone;

		/**
		 * If not given, phone must be given.
		 **/
		private @Email String email;

		/**
		 * Can be used to give additional seating information (table...)
		 **/
		private @Textual String qualifier;

		/**
		 * If the visitor has been verified by the submitting application. I.e. the contact information given by the visitor
		 * can be given more trust.
		 */
		private boolean verified;

		/**
		 * The date of a positive test result a visitor had submitted, if known.
		 */
		private @Past LocalDate positiveTestDate;

		/**
		 * The date of a negative test result a visitor had submitted, if known.
		 */
		private @Past LocalDate negativeTestDate;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class VisitorGroupDto {

		/**
		 * A list of visitors.
		 */
		private @NotEmpty List<VisitorDto> visitors;

		/**
		 * An 8-digit occasion code provided by the health authorities. Note the absence of characters that might be
		 * ambiguous when transmitted verbally or in hand writing (I, J, 1, O, 0).
		 */
		private @Pattern(regexp = OccasionCode.REGEX) String occcasionCode;

		/**
		 * The name of the location associated with the occasion.
		 */
		private @NotBlank @Textual String locationName;

		/**
		 * A general comment.
		 */
		private @Textual String comment;

		/**
		 * The start date of the time frame for which the visitors are submitted. Submit only this fiel if you want to
		 * indicate you submit visitors for the entire day and can't or don't want to be more specific than that.
		 */
		private @NotNull LocalDate startDate;

		/**
		 * The start time for which the visitors are submitted. Defaults to midnight of the start date submitted.
		 */
		private LocalTime startTime;

		/**
		 * The end date of the time frame for which the visitors are submitted. Defaults to the day after the start date.
		 */
		private LocalDate endDate;

		/**
		 * The end time for which the visitors are submitted.
		 */
		private LocalTime endTime;

		VisitorGroupDto validate(Errors errors) {

			if (!getStart().isBefore(getEnd())) {

				Stream.of("startDate", "startTime", "endDate", "endTime")
						.forEach(it -> errors.rejectValue(it, "foobar"));
			}

			return this;
		}

		@JsonIgnore
		public LocalDateTime getStart() {

			var timeToUse = startTime == null ? LocalTime.MIDNIGHT : startTime;

			return LocalDateTime.of(startDate, timeToUse);
		}

		@JsonIgnore
		public LocalDateTime getEnd() {

			var timeToUse = endTime == null ? LocalTime.MIDNIGHT.minusMinutes(1) : endTime;
			var dateToUse = endDate == null ? startDate : endDate;

			return LocalDateTime.of(dateToUse, timeToUse);
		}
	}
}
