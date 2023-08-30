package com.example.security1.config;

import com.example.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성, preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig {
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    //해당 메서드이 리턴되는 오브젝트를 IOC로 등록해준다
    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();

        http.authorizeHttpRequests().requestMatchers("/user/**").authenticated() // 인증만 되면 들어갈 수 있는 주소
                .requestMatchers("/manager/**").hasAnyAuthority("manager", "admin")
                .requestMatchers("/admin/**").hasAnyAuthority("admin")
                .anyRequest().permitAll()
                .and().formLogin().loginPage("/loginForm")
                .loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대시 로그인을 진행해준다 controller에 /login을 만들지 않아도 됨
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글로인이 완료된 뒤의 후처리가 필요함
        // 1. 코드받기(인증) 2.엑세스토큰(권한) 3.사용자프로필 정보를 가져와서
        // 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시킴
        // 4-2. (이메일, 전화번호, 이름 아이디) 쇼핑몰을 한다고 했을때 추가적으로 집주소,등급가 필요하다면
        // 추가적인 회원가입창이 필요하다
        // tip 코드x, (엑세스토큰+사용자프로필정보)
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
                ;
        // 401 Error 처리, Authorization 즉, 인증과정에서 실패할 시 처리
//        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

        // 403 Error 처리, 인증과는 별개로 추가적인 권한이 충족되지 않는 경우
//        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

        return http.build();
    }


}
