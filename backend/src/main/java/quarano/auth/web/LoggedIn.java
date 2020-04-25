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
package quarano.auth.web;

import quarano.tracking.TrackedPerson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.ServletRequestBindingException;

/**
 * Annotation to mark the method parameter with that shall get the {@link TrackedPerson} corresponding to the currently
 * logged in user injected. The method parameter needs to be of type {@code TrackedPerson} A
 * {@link ServletRequestBindingException} will be thrown if no {@link TrackedPerson} could have been obtained in the
 * first place.
 *
 * @author Oliver Drotbohm
 * @see LoggedInArgumentResolver
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggedIn {
}
