package ca.brij.security;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import ca.brij.bean.user.MyUserDetailsService;



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	
	@Resource(name="authService")
    private MyUserDetailsService myUserDetailsService;
 
    @Autowired
    private MySavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler;
	
    @Autowired
    private CustomLogOutSuccessHandler logOutHandler;
    
    /**
	 * This section defines the user accounts which can be used for
	 * authentication as well as the roles each user has.
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

        BCryptPasswordEncoder encoder = passwordEncoder();
        auth
			.userDetailsService(myUserDetailsService)
			.passwordEncoder(encoder);
		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER").and().withUser("admin")
				.password("admin").roles("USER", "ADMIN");
		
	}

	private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

	}
	

	/**
	 * This section defines the security policy for the app. - BASIC
	 * authentication is supported (enough for this REST-based demo) -
	 * /employees is secured using URL security shown below - CSRF headers are
	 * disabled since we are only testing the REST interface, not a web one.
	 *
	 * NOTE: GET is not shown which defaults to permitted.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//TODO change admin to have role admin instead
		http.httpBasic().and().authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers(HttpMethod.GET, "/webPortal/**").permitAll()
				.antMatchers(HttpMethod.GET, "/download/**").permitAll()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/img/**").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/fonts/**").permitAll()
				.antMatchers("/res/**").permitAll()
				.antMatchers("/spec/**").permitAll()
				.antMatchers("/reset**").permitAll()
				.antMatchers(HttpMethod.POST, "/user/register").permitAll()
				.antMatchers(HttpMethod.POST, "/login**").permitAll()
				.antMatchers(HttpMethod.GET, "/user/updateForgotPassword**").permitAll()
				.antMatchers(HttpMethod.GET, "/user/forgotpassword**").permitAll()
				.antMatchers(HttpMethod.GET, "/admin**").hasRole("ADMIN")
                .anyRequest().hasRole("USER")
				.and()
		        .formLogin()
		        .successHandler(authenticationSuccessHandler)
		        .failureHandler(new SimpleUrlAuthenticationFailureHandler())
	            .and()
				.logout().logoutSuccessHandler(logOutHandler).permitAll().and().csrf().disable();
	}
	
    @Bean
    public CustomLogOutSuccessHandler logOutHandler(){
        return new CustomLogOutSuccessHandler();
    }
	
    @Bean
    public MySavedRequestAwareAuthenticationSuccessHandler mySuccessHandler(){
        return new MySavedRequestAwareAuthenticationSuccessHandler();
    }
    @Bean
    public SimpleUrlAuthenticationFailureHandler myFailureHandler(){
        return new SimpleUrlAuthenticationFailureHandler();
    }
}
