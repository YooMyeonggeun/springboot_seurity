package com.example.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    //해당 메서드이 리턴되는 오브젝트를 IOC로 등록해준다
    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();


//        http.authorizeHttpRequests(request -> request
//                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
//                        .anyRequest("/user/**").authenticated()
//                       .anyRequest().authenticated() // 어떠한 요청이라도 인증필요
//        ).formLogin(login -> login.defaultSuccessUrl("", true).permitAll())
//                .logout(Customizer.withDefaults());


        http.authorizeHttpRequests().requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasRole("admin or manager")
                .requestMatchers("/admin/**").hasRole("admin")
                .anyRequest().permitAll()
                .and().formLogin().loginPage("/loginForm")
                .loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대시 로그인을 진행해준다 controller에 /login을 만들지 않아도 됨
                .defaultSuccessUrl("/");

        // 401 Error 처리, Authorization 즉, 인증과정에서 실패할 시 처리
//        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

        // 403 Error 처리, 인증과는 별개로 추가적인 권한이 충족되지 않는 경우
//        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

        return http.build();
    }


}