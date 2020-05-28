package quarano.diary;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.util.Assert;

/**
 * Represents a time slot, a {@link DiaryEntry} is assigned to and lives in. It consists of a date and a
 * {@link TimeOfDay} that covers certain time spans (6am-4pm, 4pm-6am). Interesting about esp. the latter period is that
 * e.g. the Slot for e.g. 2am of a day is the evening slot of the day before. {@link Slot#of(LocalDateTime)} correctly
 * considers this.
 *
 * @author Oliver Drotbohm
 * @author Michael J. Simons
 * @author Felix Schultze
 */
@Embeddable
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Slot implements Comparable<Slot> {

	private static final ZoneId ZONE_BERLIN = ZoneId.of("Europe/Berlin");

	private final @Column(name = "date_of_slot") LocalDate date;
	private final @Enumerated(EnumType.STRING) TimeOfDay timeOfDay;

	/**
	 * Creates a new {@link Slot} for the morning of the given date.
	 *
	 * @param date must not be {@literal null}.
	 * @return
	 */
	public static Slot morningOf(LocalDate date) {

		Assert.notNull(date, "Date must not be null!");

		return Slot.of(date, TimeOfDay.MORNING);
	}

	/**
	 * Creates a {@link Slot} for the evening of the given date.
	 *
	 * @param date must not be {@literal null}.
	 * @return
	 */
	public static Slot eveningOf(LocalDate date) {

		Assert.notNull(date, "Date must not be null!");

		return Slot.of(date, TimeOfDay.EVENING);
	}

	/**
	 * Creates the {@link Slot} for the given date.
	 *
	 * @param date must not be {@literal null}.
	 * @return
	 */
	public static Slot of(LocalDateTime date) {

		Assert.notNull(date, "Date must not be null!");

		var tod = TimeOfDay.of(date);

		return new Slot(tod.toLocalDate(date), tod);
	}

	/**
	 * Creates a new {@link Slot} for the current date and time.
	 *
	 * @return
	 */
	public static Slot now() {
		return Slot.of(LocalDateTime.now(ZONE_BERLIN));
	}

	/**
	 * Returns the previous {@link Slot}.
	 *
	 * @return
	 */
	public Slot previous() {
		return isEvening() ? Slot.morningOf(date) : Slot.eveningOf(date.minusDays(1));
	}

	public boolean isMorning() {
		return timeOfDay.equals(TimeOfDay.MORNING);
	}

	public boolean isMorningOf(LocalDate date) {
		return isMorning() && this.date.equals(date);
	}

	public boolean isEvening() {
		return timeOfDay.equals(TimeOfDay.EVENING);
	}

	public boolean isEveningOf(LocalDate date) {
		return isEvening() && this.date.equals(date);
	}

	public boolean hasDate(LocalDate date) {
		return this.date.equals(date);
	}

	/**
	 * Returns the official end date and time.
	 *
	 * @return
	 */
	public LocalDateTime getOfficialEnd() {
		return LocalDateTime.of(date, timeOfDay.to);
	}

	/**
	 * Returns the actual end of the time slot, including the period after the official end and the beginning of the next
	 * one.
	 *
	 * @return
	 */
	public LocalDateTime getEnd() {
		return LocalDateTime.of(timeOfDay == TimeOfDay.EVENING ? date.plusDays(1) : date, timeOfDay.end);
	}

	/**
	 * Returns whether the {@link Slot}'s official end has been longer ago than the given {@link TemporalAmount}.
	 *
	 * @param amount must not be {@literal null}.
	 * @return
	 */
	public boolean isOlderThan(TemporalAmount amount) {

		Assert.notNull(amount, "Temporal amount must not be null!");

		return LocalDateTime.now(ZONE_BERLIN).minus(amount).isAfter(getOfficialEnd());
	}

	public boolean contains(LocalDateTime date) {

		return this.date.equals(timeOfDay.toLocalDate(date)) //
				&& timeOfDay.contains(date.toLocalTime());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s of %s", timeOfDay, date);
	}

	@Override
	public int compareTo(Slot slot) {

		var dateComparison = this.date.compareTo(slot.date);

		if (dateComparison != 0) {
			return dateComparison;
		}

		return this.timeOfDay == slot.timeOfDay ? 0 : this.timeOfDay == TimeOfDay.MORNING ? -1 : 1;
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public enum TimeOfDay {

		MORNING(LocalTime.of(6, 0, 0), LocalTime.of(11, 59, 59), LocalTime.of(16, 59, 59)) {

			/*
			 * (non-Javadoc)
			 * @see quarano.tracking.Diary.TimeOfDay#contains(java.time.LocalTime)
			 */
			@Override
			boolean contains(LocalTime reference) {
				return MORNING.from.isBefore(reference) && MORNING.end.isAfter(reference);
			}

			/*
			 * (non-Javadoc)
			 * @see quarano.tracking.Slot.TimeOfDay#isInOvertime(java.time.LocalTime)
			 */
			@Override
			boolean isInOvertime(LocalTime time) {
				return time.isAfter(MORNING.to) && time.isBefore(MORNING.end);
			}
		},

		EVENING(LocalTime.of(17, 0, 0), LocalTime.of(22, 59, 59), LocalTime.of(5, 59, 59)) {

			/*
			 * (non-Javadoc)
			 * @see quarano.tracking.Diary.TimeOfDay#contains(java.time.LocalTime)
			 */
			@Override
			boolean contains(LocalTime reference) {
				return reference.isAfter(EVENING.from) || reference.isBefore(EVENING.to);
			}

			/*
			 * (non-Javadoc)
			 * @see quarano.tracking.Diary.TimeOfDay#toLocalDate(java.time.LocalDateTime)
			 */
			@Override
			LocalDate toLocalDate(LocalDateTime date) {

				var time = date.toLocalTime();
				var result = date.toLocalDate();

				return time.isBefore(EVENING.end) ? result.minusDays(1) : result;
			}

			/*
			 * (non-Javadoc)
			 * @see quarano.tracking.Slot.TimeOfDay#isInOvertime(java.time.LocalTime)
			 */
			@Override
			boolean isInOvertime(LocalTime time) {
				return time.isAfter(EVENING.to) && time.isBefore(EVENING.end);
			}
		};

		private final LocalTime from;
		private final LocalTime to;
		private final LocalTime end;

		abstract boolean contains(LocalTime reference);

		abstract boolean isInOvertime(LocalTime time);

		LocalDate toLocalDate(LocalDateTime date) {
			return date.toLocalDate();
		}

		public static TimeOfDay now() {
			return of(LocalTime.now(ZONE_BERLIN));
		}

		static TimeOfDay of(LocalDateTime reference) {
			return of(reference.toLocalTime());
		}

		static TimeOfDay of(LocalTime reference) {

			return Arrays.stream(values()) //
					.filter(it -> it.contains(reference)) //
					.findFirst() //
					.orElseThrow(() -> new IllegalStateException("Should not happen!"));
		}
	}
}
