package ca.bc.gov.ag.courts.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import ca.bc.gov.ag.courts.config.AppProperties;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile(value = {"dev", "splunk"}) // Results in no security config when unit testing (e.g. profile is 'test' during unit tests). 
public class SecurityConfiguration {

	private final AppProperties props;

	public SecurityConfiguration(AppProperties props) {
		this.props = props;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable).authorizeHttpRequests((authorize) -> authorize
				// Allow access to health endpoint
				.requestMatchers("/actuator/**" ).permitAll()
				// Authenticate all other requests
				.anyRequest().authenticated())
				// Use basic authentication (user/pass)
				.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		System.out.println("PROPS USERNAME = " + props.getApplicationUsername());
		UserDetails user = User.builder().username(props.getApplicationUsername())
				.password(passwordEncoder().encode(props.getApplicationPassword())).roles("USER").build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
