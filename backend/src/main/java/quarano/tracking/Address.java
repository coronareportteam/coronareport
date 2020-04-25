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
package quarano.tracking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Embeddable;

import org.springframework.util.StringUtils;

/**
 * @author Oliver Drotbohm
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Address {

	private String street;
	private HouseNumber houseNumber = HouseNumber.NONE;
	private String city;
	private ZipCode zipCode;

	boolean isComplete() {

		return StringUtils.hasText(street) //
				&& StringUtils.hasText(city) //
				&& zipCode != null;
	}

	@EqualsAndHashCode
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
	public static class HouseNumber {

		public static final HouseNumber NONE = HouseNumber.of("¯\\_(ツ)_/¯");

		private final String value;

		public static HouseNumber of(String source) {
			return source == null || source.isBlank() //
					? HouseNumber.NONE //
					: new HouseNumber(source);
		}

		public boolean isAbsent() {
			return NONE.equals(this);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return value;
		}
	}
}
