package com.delivalue.tidings.config;

import com.delivalue.tidings.common.security.JwtAuthenticationEntryPoint;
import com.delivalue.tidings.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Value("${spring.profiles.active:prod}")
	private String activeProfile;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
				.authorizeHttpRequests(requests -> requests
						.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
						.requestMatchers("/auth/login", "/auth/register", "/auth/refresh", "/auth/check").permitAll()
						.requestMatchers("/signInEvent.html").permitAll()
						.requestMatchers(HttpMethod.POST, "/post/recent").permitAll()
						.requestMatchers(HttpMethod.GET, "/post/{postId}").permitAll()
						.requestMatchers(HttpMethod.GET, "/comment/{postId}").permitAll()
						.requestMatchers("/profile/{publicId}").permitAll()
						.requestMatchers("/profile/{publicId}/**").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(login -> login.defaultSuccessUrl("/signInEvent.html"))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		List<String> origins = new ArrayList<>(List.of(
				"https://stellagram.kr",
				"https://www.stellagram.kr"
		));
		if ("dev".equals(activeProfile) || "local".equals(activeProfile)) {
			origins.add("https://dev.stellagram.kr");
			origins.add("http://localhost:5173");
		}

		configuration.setAllowedOriginPatterns(origins);
		configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

		return bean;
	}
}
