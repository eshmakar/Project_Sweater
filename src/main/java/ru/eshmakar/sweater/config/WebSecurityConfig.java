package ru.eshmakar.sweater.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import ru.eshmakar.sweater.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() //включаем авторизацию
                    .antMatchers("/", "/registration", "/static/**", "/activate/*").permitAll() //для главной странице разрешаем доступ всем
                    .anyRequest().authenticated() //для остальных запросов требуем авторизацию
                .and()
                    .formLogin()    //включаем форму логина
                    .loginPage("/login") //указываем, что логин форма находится по такому адресу
                    .permitAll() //разрешаем этим пользоваться всем
                .and()
                    .logout() //разрешаем выход из системы
                    .permitAll(); //разрешаем этим пользоваться всем
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance()); //шифрование паролей для их записи в бд
    }
}