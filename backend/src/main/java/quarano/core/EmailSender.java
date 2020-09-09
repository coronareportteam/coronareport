package quarano.core;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import quarano.core.EmailTemplates.Key;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Generic email sender for Quarano that uses the templates of {@link EmailTemplates} as body. The data for a email are
 * given with an instance of <code>EmailData</code>.
 * <p>
 * The EmailSender uses <strong>lastName = <code>getToLastName()</code> und host = {host from configuration}</strong> as
 * default placeholders with the template.
 * </p>
 *
 * @author Jens Kutzsche
 * @author Oliver Drotbohm
 */
@Service
@RequiredArgsConstructor
public class EmailSender {

	private final @NonNull JavaMailSenderImpl emailSender;
	private final @NonNull EmailTemplates templates;
	private final @NonNull CoreProperties configuration;
	private final @NonNull MailProperties mailProperties;

	/**
	 * Verifies the connection to the actual email server.
	 *
	 * @return an {@link Object} to represent the server information, will never be {@literal null}.
	 */
	public Try<Object> testConnection() {

		return Try.success(emailSender) //
				.filter(it -> it.getHost() != null, () -> new IllegalStateException("No email server host configured!"))
				.andThenTry(JavaMailSenderImpl::testConnection)
				.map(it -> new Object() {

					@Override
					@SuppressWarnings("null")
					public String toString() {
						return it.getHost().concat(":") + it.getPort();
					}
				});
	}

	/**
	 * Sends the given {@link TemplatedEmail}.
	 *
	 * @param email must not be {@literal null}.
	 * @return
	 */
	public Try<Void> sendMail(TemplatedEmail email) {

		Assert.notNull(email, "Email must not be null!");

		return Try.run(() -> emailSender.send(email.toMailMessage(templates, configuration, mailProperties)));
	}

	/**
	 * An email to be composed from {@link EmailTemplates} and {@link CoreProperties}. Custom implementations usually
	 * extend {@link AbstractTemplatedEmail}.
	 *
	 * @author Jens Kutzsche
	 * @author Oliver Drotbohm
	 * @see AbstractTemplatedEmail
	 */
	public interface TemplatedEmail {

		/**
		 * Composes a {@link SimpleMailMessage} using the given {@link EmailTemplates} and {@link CoreProperties}.
		 *
		 * @param templates must not be {@literal null}.
		 * @param configuration must not be {@literal null}.
		 * @param mailProperties
		 * @return
		 */
		SimpleMailMessage toMailMessage(EmailTemplates templates, CoreProperties configuration,
				@NonNull MailProperties mailProperties);
	}

	/**
	 * Convenient base class to compose emails from a template defined by a {@link Key} and placeholders to expand the
	 * template with. Sender and Receiver are abstracted to be composed separately and handed into the instance.
	 *
	 * @author Jens Kutzsche
	 * @author Oliver Drotbohm
	 */
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static abstract class AbstractTemplatedEmail implements TemplatedEmail {

		/**
		 * At the moment we can only use a configured fix sender address, to avoid problems with permissions. This comes
		 * from spam protection. With other sender addresses we get the following error:
		 * <code>550 5.7.60 SMTP; Client does not
		 * have permissions to send as this sender</code>. To configure the fix sender address use
		 * <code>spring.mail.properties.fixSender</code>.
		 */
		private static final String FIX_SENDER_PROPERTY_KEY = "fixSender";

		private final @Getter Sender from;
		private final @Getter Recipient to;
		private final @Getter String subject;
		private final Key template;
		private final Map<String, ? extends Object> placeholders;

		/*
		 * (non-Javadoc)
		 * @see quarano.core.EmailSender.TemplatedEmail#toMailMessage(quarano.core.EmailTemplates, quarano.core.CoreProperties)
		 */
		@Override
		public SimpleMailMessage toMailMessage(EmailTemplates templates, CoreProperties configuration,
				@NonNull MailProperties mailProperties) {

			var fixSender = mailProperties.getProperties().get(FIX_SENDER_PROPERTY_KEY);
			var from = StringUtils.isEmpty(fixSender) ? this.from.toInternetAddress() : EmailAddress.of(fixSender).toString();

			var message = new SimpleMailMessage();
			message.setFrom(from);
			message.setTo(to.toInternetAddress());
			message.setSubject(subject);
			message.setText(getBody(templates, configuration));
			message.setSentDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

			return message;
		}

		private String getBody(EmailTemplates templates, CoreProperties configuration) {

			var placeholders = new HashMap<String, Object>();
			var toLastName = to.getLastName();

			placeholders.put("lastName", StringUtils.hasText(toLastName) ? toLastName : "");
			placeholders.put("host", configuration.getHost());
			placeholders.putAll(this.placeholders);

			return templates.expandTemplate(template, placeholders);
		}

		private interface InternetAdressSource {

			static final String ADRESS_FORMAT = "%s <%s>";

			String getFullName();

			EmailAddress getEmailAddress();

			default String toInternetAddress() {

				var fullName = StringUtils.hasText(getFullName()) ? getFullName() : "";

				return String.format(ADRESS_FORMAT, fullName, getEmailAddress());
			}
		}

		public interface Sender extends InternetAdressSource {}

		public interface Recipient extends InternetAdressSource {
			String getLastName();
		}
	}
}
