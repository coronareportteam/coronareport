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
package quarano;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.jddd.core.types.Identifier;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author Oliver Drotbohm
 */
@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
public class Quarano {

	public static void main(String... args) {
		SpringApplication.run(Quarano.class, args);
	}

	@Bean
	public LocalValidatorFactoryBean validatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	Module quaranoModule() {

		var module = new SimpleModule();
		module.setMixInAnnotation(Identifier.class, IdentifierMixin.class);

		return module;
	}

	@JsonSerialize(using = ToStringSerializer.class)
	static abstract class IdentifierMixin {}

}
