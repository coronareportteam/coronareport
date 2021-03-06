package quarano.diary;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import quarano.account.DepartmentRepository;
import quarano.core.EmailSender;
import quarano.core.EmailTemplates.Keys;
import quarano.core.EnumMessageSourceResolvable;
import quarano.tracking.TrackedPerson;
import quarano.tracking.TrackedPerson.TrackedPersonIdentifier;
import quarano.tracking.TrackedPersonEmailFactory;
import quarano.tracking.TrackedPersonRepository;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class collects the persons with missing diary entries and sends a reminder mail to every person.
 *
 * @author Jens Kutzsche
 * @author Oliver Drotbohm
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "quarano.diary", name = "reminder.enable", matchIfMissing = true)
class DiaryEntryReminderMailJob {

	private final @NonNull DiaryManagement diaries;
	private final @NonNull EmailSender emailSender;
	private final @NonNull TrackedPersonRepository people;
	private final @NonNull DepartmentRepository departments;
	private final @NonNull MessageSourceAccessor messages;
	private final @NonNull TrackedPersonEmailFactory emailFactory;

	@Transactional
	@Scheduled(cron = "0 10 12,23 * * *")
	void checkForReminderMail() {

		var testResult = emailSender.testConnection()
				.onFailure(it -> {

					if (log.isTraceEnabled()) {
						log.warn("Quarano can't connect the mail server to send reminder mails!", it);
					} else {
						log.warn("Quarano can't connect the mail server to send reminder mails! {}", it.getMessage());
					}
				});

		if (testResult.isFailure()) {
			return;
		}

		var slot = Slot.now().previous();

		collectPersonsMissingEntry(slot)
				.map(people::findRequiredById)
				.forEach(it -> sendReminderMail(it, slot));
	}

	Streamable<TrackedPersonIdentifier> collectPersonsMissingEntry(Slot slot) {

		log.debug("Check missing diary entries for slot {}.", slot);

		return diaries.findMissingDiaryEntryPersons(List.of(slot));
	}

	void sendReminderMail(TrackedPerson trackedPerson, Slot slot) {

		trackedPerson.getAccount()
				.flatMap(it -> departments.findById(it.getDepartmentId()))
				.ifPresent(it -> {

					var locale = trackedPerson.getLocale();
					var subject = messages.getMessage("DiaryEntryReminderMail.subject", locale);
					var textTemplate = Keys.DIARY_REMINDER;
					Function<Locale, String> slotFunction = slotLocale -> {

						return messages.getMessage(EnumMessageSourceResolvable.of(slot.getTimeOfDay()).getCodes()[0],
								new Object[] { slot.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) },
								slotLocale);
					};
					var logArgs = new Object[] { trackedPerson.getFullName(), String.valueOf(trackedPerson.getEmailAddress()),
							trackedPerson.getId().toString() };

					var placeholders = Map.of("slot", slotFunction);

					Try.success(emailFactory.getEmailFor(trackedPerson, subject, textTemplate, placeholders))
							.flatMap(emailSender::sendMail)
							.onSuccess(__ -> log.debug("Reminder mail sended to {{}; {}; Person-ID {}}", logArgs))
							.onFailure(e -> log.debug("Can't send reminder mail to {{}; {}; Person-ID {}}", logArgs))
							.onFailure(e -> log.debug("Exception", e));
				});
	}
}
