package quarano.tracking.web;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.*;

import lombok.Data;
import lombok.Getter;
import quarano.core.validation.AlphaNumeric;
import quarano.core.validation.Alphabetic;
import quarano.core.validation.Textual;
import quarano.tracking.ContactPerson.ContactPersonIdentifier;
import quarano.tracking.EmailAddress;
import quarano.tracking.PhoneNumber;
import quarano.tracking.ZipCode;

import java.util.Collections;
import java.util.Map;

import javax.validation.constraints.Pattern;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
class ContactPersonDto {

	private static final String INVALID_CONTACT_WAYS_KEY = "Invalid.contactWays";

	@Getter(onMethod = @__(@JsonProperty(access = JsonProperty.Access.READ_ONLY))) //
	private ContactPersonIdentifier id;

	private @Alphabetic String lastName, firstName, street, city;
	private @AlphaNumeric String houseNumber;
	private @Pattern(regexp = ZipCode.PATTERN) String zipCode;
	private @Pattern(regexp = PhoneNumber.PATTERN) String phone;
	private @Pattern(regexp = PhoneNumber.PATTERN) String mobilePhone;
	private @Pattern(regexp = EmailAddress.PATTERN) String email;
	private @Textual String remark;
	private @Textual String identificationHint;
	private Boolean isHealthStaff;
	private Boolean isSenior;
	private Boolean hasPreExistingConditions;

	@JsonProperty("_links")
	@JsonInclude(Include.NON_EMPTY)
	public Map<String, Object> getLinks() {

		if (id == null) {
			return Collections.emptyMap();
		}

		var contactResource = on(ContactPersonController.class).getContact(null, id);

		return Map.of("self", Map.of("href", fromMethodCall(contactResource).toUriString()));
	}

	Errors validate(Errors errors) {

		if (phone == null && email == null && mobilePhone == null && !StringUtils.hasText(identificationHint)) {
			errors.rejectValue("phone", INVALID_CONTACT_WAYS_KEY);
			errors.rejectValue("mobilePhone", INVALID_CONTACT_WAYS_KEY);
			errors.rejectValue("email", INVALID_CONTACT_WAYS_KEY);
			errors.rejectValue("identificationHint", INVALID_CONTACT_WAYS_KEY);
		}

		return errors;
	}
}
