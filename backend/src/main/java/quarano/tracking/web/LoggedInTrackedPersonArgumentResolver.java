package quarano.tracking.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import quarano.account.AuthenticationManager;
import quarano.core.web.LoggedIn;
import quarano.tracking.TrackedPerson;
import quarano.tracking.TrackedPersonManagement;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@link HandlerMethodArgumentResolver} to inject the {@link TrackedPerson} of the currently logged in user into Spring
 * MVC controller method parameters annotated with {@link LoggedIn}.
 *
 * @author Oliver Drotbohm
 */
@Component
@RequiredArgsConstructor
class LoggedInTrackedPersonArgumentResolver implements HandlerMethodArgumentResolver, WebMvcConfigurer {

	private static final String USER_ACCOUNT_EXPECTED = "Expected to find a current %s but none available!";
	private final @NonNull AuthenticationManager authenticationManager;
	private final @NonNull TrackedPersonManagement people;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	@org.springframework.lang.NonNull
	public Object resolveArgument(MethodParameter parameter,
			@Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			@Nullable WebDataBinderFactory binderFactory) throws Exception {

		Class<?> type = parameter.getParameterType();

		return authenticationManager.getCurrentUser()
				.flatMap(people::findByAccount)
				.orElseThrow(
						() -> new ServletRequestBindingException(String.format(USER_ACCOUNT_EXPECTED, type.getSimpleName())));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		return parameter.hasParameterAnnotation(LoggedIn.class)
				&& TrackedPerson.class.isAssignableFrom(parameter.getParameterType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addArgumentResolvers(java.util.List)
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(this);
	}
}
