package ru.forsh.springsecuiritytest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.forsh.springsecuiritytest.model.Role;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Аннотация включает аннотации PreAuthorized В контроллерах.
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
                .anyRequest()
                .authenticated() // Каждый запрос должен проходить аутентификацию
                .and()
                .formLogin()// Здесь мы прописываем что будет собственная форма логина, html форма в templates
                .loginPage("/auth/login").permitAll()
                .defaultSuccessUrl("/auth/success")
                .and()
                .logout()// Настраиваем LOGOUT
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))// Обработка метода логаут - методом POST. это важно, т.к. по умолчанию стоит GET, чего не должно быть
                .invalidateHttpSession(true)// инвалидация текущей сессии
                .clearAuthentication(true)// очищение сущности, которая содержит информацию про тебя
                .deleteCookies("JSESSIONID") // очистка куки
                .logoutSuccessUrl("/auth/login"); // форма логина с её адресом. доступ разрешён всем.
    }


    // Встроенный юзер в Спринг, нужен для аутентификции админа.
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin"))
                        .authorities(Role.ADMIN.getAuthorities())
                        .build(),
                User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("user"))
                        .authorities(Role.USER.getAuthorities())
                        .build());
    }

    // Этот бин кодирует пароль, сила - 12, чем больше - тем сложнее пароль. Аналогичен
    // Сайту https://bcrypt-generator.com/
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
