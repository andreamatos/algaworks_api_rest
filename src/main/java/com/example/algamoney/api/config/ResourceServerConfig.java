package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

//esta classe configura entrada de usuario e senha
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)// faz a seguranca em cima dos métodos
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	// primeiro teste -> não informa usuario e senha
	// URL: localhost:8080/lancamentos?size=3&page=2
	// RESULTADO:
	//	{
	//	    "timestamp": 1565609202974,
	//	    "status": 401,
	//	    "error": "Unauthorized",
	//	    "message": "Full authentication is required to access this resource",
	//	    "path": "/lancamentos"
	//	}	
	
	// segundo teste -> não informa usuario e senha
	//	{
	// URL: localhost:8080/categorias
	// RESULTADO:
	//	[
	//	    {
	//	        "codigo": 1,
	//	        "nome": "Lazer"
	//	    },
	//	    {
	//	        "codigo": 2,
	//	        "nome": "Alimentação"
	//	    },
	//	    {
	//	        "codigo": 3,
	//	        "nome": "Supermercado"
	//	    },
	//	    {
	//	        "codigo": 4,
	//	        "nome": "Farmácia"
	//	    },
	//	    {
	//	        "codigo": 5,
	//	        "nome": "Outros"
	//	    }
	//	]
	// terceiro teste -> informa usuario e senha correto
	//	{
	// URL: localhost:8080/lancamentos?size=3&page=2
	// na opcao Authorization marcar type = Basic Auth e informar usuario e senha da opcao configura abaixo.
	
	@Autowired
	private UserDetailsService userDetailService;
	
	//méotodo para verficiar password do banco de dados
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ROLE");
 		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
	}
	//.antMatchers("/categorias").permitAll() -> autoriza para a url categorias
	//.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) -> 
	//não guarda nada na sessão, eh apenas pra teste
	//.csrf().disable(); -> não permite injeção de javascript

	//decriptografar senha do banco de dados 
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests()
			.antMatchers("/categorias").permitAll()
			.anyRequest().authenticated().
			and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.csrf().disable();
	}
	
	private void configurer(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}
	
	//faz a seguranca dos métodos em conjunto com a anotacao da classe  @EnableGlobalMethodSecurity(prePostEnabled = true)
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
}