package ru.forsh.springsecuiritytest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import ru.forsh.springsecuiritytest.model.Role;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //Для разграничения ролей Админ/Юзер - создаём кастомную конфигурация HTTP
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() /*
                cross-site request forgery — «межсайтовая подделка запроса», также известна как XSRF)
                — вид атак на посетителей веб-сайтов, использующий недостатки протокола HTTP.
                Если жертва заходит на сайт, созданный злоумышленником, от её лица тайно
                отправляется запрос на другой сервер (например, на сервер платёжной системы),
                осуществляющий некую вредоносную операцию (например, перевод денег на счёт злоумышленника).
                */
                .authorizeRequests()
                .antMatchers("/") // здесь мы указываем что на страницу приложения / - доступ имеют все
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/**")
                .hasAnyRole(Role.USER.name(), Role.ADMIN.name()) // здесь мы указываем, что доступ к ГЕТ запросам имеют все
                .antMatchers(HttpMethod.POST, "/api/**") // к пост - только админы
                .hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/**") // к Delete - только админы
                .hasRole(Role.ADMIN.name())
                .anyRequest()
                .authenticated() // Каждый запрос должен проходить аутентификацию
                .and()
                .httpBasic(); // Кодировка с помощью BASE64


    }


    // Встроенный юзер в Спринг, нужен для аутентификции админа.
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles(Role.ADMIN.name())
                .build(),
                User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("user"))
                        .roles(Role.USER.name())
                        .build());
    }

    // Этот бин кодирует пароль, сила - 12, чем больше - тем сложнее пароль. Аналогичен
    // Сайту https://bcrypt-generator.com/
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
