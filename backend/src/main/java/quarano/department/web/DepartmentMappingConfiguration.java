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
package quarano.department.web;

import quarano.department.TrackedCase;
import quarano.tracking.Quarantine;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Customizations for {@link ModelMapper}.
 *
 * @author Oliver Drotbohm
 */
@Component
public class DepartmentMappingConfiguration {

	public DepartmentMappingConfiguration(ModelMapper mapper) {

		// TrackedCase

		mapper.typeMap(TrackedCase.class, TrackedCaseDto.class).setPreConverter(it -> {

			var source = it.getSource();
			var target = it.getDestination();
			var quarantine = source.getQuarantine();

			if (quarantine != null) {
				target.setQuarantineStartDate(quarantine.getFrom());
				target.setQuarantineEndDate(quarantine.getTo());
			}

			return target;

		}).addMappings(it -> {
			it.skip(TrackedCaseDto::setQuarantineStartDate);
			it.skip(TrackedCaseDto::setQuarantineEndDate);
		});

		mapper.typeMap(TrackedCaseDto.class, TrackedCase.class).setPreConverter(it -> {

			var source = it.getSource();
			var target = it.getDestination();

			return target.setQuarantine(Quarantine.of(source.getQuarantineStartDate(), source.getQuarantineEndDate()));
		});
	}
}
