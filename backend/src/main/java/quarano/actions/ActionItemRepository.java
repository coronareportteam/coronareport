/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quarano.actions;

import quarano.actions.ActionItem.ActionItemIdentifier;
import quarano.core.QuaranoRepository;
import quarano.department.TrackedCase;
import quarano.tracking.Slot;
import quarano.tracking.TrackedPerson.TrackedPersonIdentifier;

import org.springframework.data.jpa.repository.Query;

/**
 * @author Oliver Drotbohm
 */
public interface ActionItemRepository extends QuaranoRepository<ActionItem, ActionItemIdentifier> {

	default ActionItems findByCase(TrackedCase trackedCase) {
		return findByTrackedPerson(trackedCase.getTrackedPerson().getId());
	}


	default ActionItems findUnresolvedByCase(TrackedCase trackedCase) {
		return findUnresolvedByTrackedPerson(trackedCase.getTrackedPerson().getId());
	}

	@Query("select i from ActionItem i where i.personIdentifier = :identifier")
	ActionItems findByTrackedPerson(TrackedPersonIdentifier identifier);

	@Query("select i from ActionItem i " +
			"where i.resolved = false " +
			"and i.personIdentifier = :identifier")
	ActionItems findUnresolvedByTrackedPerson(TrackedPersonIdentifier identifier);

	@Query("select i from ActionItem i where i.personIdentifier = :identifier and i.description.code = :code")
	ActionItems findByDescriptionCode(TrackedPersonIdentifier identifier, DescriptionCode code);

	@Query("select i from DiaryEntryMissingActionItem i" //
			+ " where i.slot = :slot" //
			+ " and i.personIdentifier = :personIdentifier")
	ActionItems findDiaryEntryMissingActionItemsFor(TrackedPersonIdentifier personIdentifier, Slot slot);
}
