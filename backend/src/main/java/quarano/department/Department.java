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
package quarano.department;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import quarano.core.QuaranoAggregate;
import quarano.department.Department.DepartmentIdentifier;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jddd.core.types.Identifier;

/**
 * @author Oliver Drotbohm
 * @author Michael J. Simons
 */
@Entity
@Table(name = "departments")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Department extends QuaranoAggregate<Department, DepartmentIdentifier> {

	private final @Getter @Column(name = "department_name", unique = true) String name;

	public Department(String name) {
		this(name, UUID.randomUUID());
	}

	public Department(String name, UUID uuid) {
		this(name, DepartmentIdentifier.of(uuid));
	}

	// fixed Id department for tests and integration data
	Department(String name, DepartmentIdentifier fixedId) {

		this.id = fixedId;
		this.name = name;
	}

	@Embeddable
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of")
	@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
	public static class DepartmentIdentifier implements Identifier, Serializable {
		private static final long serialVersionUID = 7871473225101042167L;
		
		@Column(name = "id")
		final UUID departmentId;

		@Override
		public String toString() {
			return departmentId.toString();
		}
	}
}
