package quarano.auth.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import quarano.auth.RoleType;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configures security settings and defines which url pattern can be accessed by which role
 *
 * @author Patrick Otto
 * @author Oliver Drotbohm
 */
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
class QuaranoWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	private final JwtProperties configuration;

	private static final String[] SWAGGER_UI_WHITELIST = {

			// -- swagger ui
			"/swagger-resources/**", //
			"/swagger-ui.html", //
			"/swagger-ui/**", //
			"/v3/api-docs/**", //
			"/webjars/**" };

	@Bean
	JwtDecoder jwtDecoder() {
		return configuration.getJwtDecoder();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		log.debug("Configuring HTTP security, allowing public access to '/login', 'clinet/register' and 'swagger-ui.html'");

		httpSecurity.oauth2ResourceServer() //
				.jwt().jwtAuthenticationConverter(configuration.getJwtConverter());

		httpSecurity.authorizeRequests(it -> {
			it.mvcMatchers(SWAGGER_UI_WHITELIST).permitAll();
			it.mvcMatchers("/docs/**").permitAll();
			it.mvcMatchers("/login").permitAll();
			it.mvcMatchers("/api/hd/**").access("hasRole('" + RoleType.ROLE_HD_CASE_AGENT + "')"); //
			it.mvcMatchers("/api/hd/**").access("hasRole('" + RoleType.ROLE_HD_ADMIN + "')"); //
			it.mvcMatchers("/api/login").permitAll();
			it.mvcMatchers("/api/registration").permitAll();
			it.mvcMatchers("/api/registration/checkcode/**").permitAll(); //
			it.mvcMatchers("/api/registration/checkusername/**").permitAll(); //
			it.mvcMatchers("/api/user/me").authenticated(); //
			it.mvcMatchers("/**").access("hasRole('" + RoleType.ROLE_USER + "')"); //
			it.mvcMatchers("/hd/**").access("hasRole('" + RoleType.ROLE_HD_CASE_AGENT + "')"); //
			it.mvcMatchers("/hd/**").access("hasRole('" + RoleType.ROLE_HD_ADMIN + "')"); //
		});

		httpSecurity.csrf().disable().cors(it -> {
			it.configurationSource(corsConfigurationSource());
		});
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*")); // Problem!
		configuration.setAllowCredentials(true);
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token", "Origin"));
		configuration.setExposedHeaders(Arrays.asList("X-Auth-Token"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
