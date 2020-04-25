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

import lombok.RequiredArgsConstructor;

import java.util.Iterator;

import org.springframework.data.util.Streamable;

/**
 * @author Oliver Drotbohm
 */
@RequiredArgsConstructor(staticName = "of")
public class ActionItems implements Streamable<ActionItem> {

	private final Streamable<ActionItem> items;

	public float getPriority() {

		return items.stream() //
				.map(ActionItem::getWeight) //
				.reduce(0.0f, (l, r) -> l + r);
	}

	public Streamable<ActionItem> getHealthItems() {

		return items //
				.filter(ActionItem::isUnresolved) //
				.filter(ActionItem::isMedicalItem);
	}

	public Streamable<ActionItem> getProcessItems() {

		return items //
				.filter(ActionItem::isUnresolved) //
				.filter(ActionItem::isProcessItem);
	}

	public Streamable<ActionItem> getDoneItems() {

		return items //
				.filter(ActionItem::isResolved);
	}

	public boolean hasUnresolvedItems() {

		return items.stream() //
				.anyMatch(ActionItem::isUnresolved);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<ActionItem> iterator() {
		return items.iterator();
	}
}
