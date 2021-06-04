package quarano.occasion;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import quarano.core.Address;
import quarano.core.QuaranoAggregate;
import quarano.department.TrackedCase.TrackedCaseIdentifier;
import quarano.occasion.Occasion.OccasionIdentifier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jmolecules.ddd.types.Identifier;

@Getter
@Entity
@Table(name = "occasions")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Occasion extends QuaranoAggregate<Occasion, OccasionIdentifier> {

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "occasionCode") //
	private List<VisitorGroup> visitorGroups;
	private @Setter @Column(name = "start_date") LocalDateTime start;
	private @Setter @Column(name = "end_date") LocalDateTime end;
	private @Setter String title;
	private @Setter String additionalInformation;
	private @Setter String contactPerson;
	private OccasionCode occasionCode;
	private TrackedCaseIdentifier trackedCaseId;
	private @Setter Address address;

	Occasion(String title, LocalDateTime start, LocalDateTime end, Address address, String additionalInformation, String contactPerson, OccasionCode eventCode,
			TrackedCaseIdentifier trackedCaseId) {

		this.id = OccasionIdentifier.of(UUID.randomUUID());
		this.start = start;
		this.end = end;
		this.title = title;
		this.address = address;
		this.additionalInformation = additionalInformation;
		this.contactPerson = contactPerson;
		this.visitorGroups = new ArrayList<>();
		this.occasionCode = eventCode;
		this.trackedCaseId = trackedCaseId;
	}

	Occasion registerVisitorGroup(VisitorGroup visitorGroup) {

		this.visitorGroups.add(visitorGroup);
		return this;
	}

	@Embeddable
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of")
	@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
	public static class OccasionIdentifier implements Identifier, Serializable {

		private static final long serialVersionUID = -8938479214117686141L;

		private final UUID occasionId;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return occasionId.toString();
		}
	}
}
