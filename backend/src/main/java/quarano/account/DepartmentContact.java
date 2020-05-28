package quarano.account;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import quarano.core.EmailAddress;
import quarano.core.PhoneNumber;
import quarano.core.QuaranoEntity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.jddd.core.types.Identifier;

@Entity
@Table(name = "departments_contacts")
@EqualsAndHashCode(callSuper = true, of = {})
public class DepartmentContact extends QuaranoEntity<Department, DepartmentContact.DepartmentContactIdentifier> {
	private @Enumerated(EnumType.STRING) @Getter @Setter ContactType type;
	private @Getter @Setter EmailAddress emailAddress;
	private @Getter @Setter PhoneNumber phoneNumber;

	private DepartmentContact() {
		this.id = DepartmentContactIdentifier.of(UUID.randomUUID());
	}

	public enum ContactType {
		INDEX, CONTACT
	}

	public static DepartmentContact of() {
		return new DepartmentContact();
	}

	@Embeddable
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of")
	@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
	public static class DepartmentContactIdentifier implements Identifier, Serializable {
		private static final long serialVersionUID = 7871473225101042167L;

		final UUID departmentContactId;

		@Override
		public String toString() {
			return departmentContactId.toString();
		}
	}
}
