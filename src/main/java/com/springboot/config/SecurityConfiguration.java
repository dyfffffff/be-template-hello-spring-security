package com.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static javax.management.Query.and;


@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 프레임태그가 달려있는걸 조회하겠다는뜻
                .headers().frameOptions().sameOrigin()
                .and()
                // csrf disable 웹기반 취약 공격을 비활성화 하고 있음.
                // 이거를 설정하지 않으면 활성화되있음
                .csrf().disable()
                // 폼로그인을 쓴다
                .formLogin()
                .loginPage("/auths/login-form")
                // 인증요청을 수행할 주소
                .loginProcessingUrl("/process_login")
                .failureUrl("/auths/login-form?error")
                .and()
                // 로그아웃 전체도 시큐리티한테 넘기는 코드
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                // 권한이 없을 때 accessDeniedPage 이거를 만듬
                .exceptionHandling().accessDeniedPage("/auths/access-denied")
                .and()
                .authorizeHttpRequests(authorize ->authorize
                        //antMatchers url매칭하는 문법 오더의 하위의 모든 주소가 외도 어드민을 허용하겠다는 뜻
                        // **와일드카드 하나는 뎁스가 1뎁스만 허용하겠다는 뜻 두개는 모두허용
                        .antMatchers("/orders/**").hasRole("ADMIN")
                        .antMatchers("/members/my-page").hasRole("USER")
                        .antMatchers("/**").permitAll()
                );


        return http.build();
    }
//    @Bean
//    //UserDetails의 구현체가 user임
//    public UserDetailsManager userDetailsManager() {
//
//        UserDetails userDetails =
//                //withDefaultPasswordEncoder() 이걸통해 패스워트를 암호화하는걸
//                // 통해 user객체를 만들 때 패스워드를 암호화 한다는 뜻
//                // 그래서 "rr2312"이게 암호화됨
//                // 근데 안에 들어가면 @Deprecated가 있는데,
//                // 이거는 실제 서비스 환경에 쓰지 말라는뜻 근데 왜? 디폴트한 방식 로우수준이라 그렇다네
//                User.withDefaultPasswordEncoder()
//                        .username("fnffn@naver.com")
//                        .password("rr2312")
//                        // roles은 인가정보임
//                        .roles("USER")
//                        .build();
//
//        UserDetails admin =
//                //withDefaultPasswordEncoder() 이걸통해 패스워트를 암호화하는걸
//                // 통해 user객체를 만들 때 패스워드를 암호화 한다는 뜻
//                // 그래서 "rr2312"이게 암호화됨
//                // 근데 안에 들어가면 @Deprecated가 있는데,
//                // 이거는 실제 서비스 환경에 쓰지 말라는뜻 근데 왜? 디폴트한 방식 로우수준이라 그렇다네
//                User.withDefaultPasswordEncoder()
//                        .username("admin@naver.com")
//                        .password("1111")
//                        // roles은 인가정보임
//                        .roles("ADMIN")
//                        .build();
//
//
//
//        // 왜 반환타입이 다른걸까. UserDetailsManager얘가 추상화되어 있기 때문에
//        // InMemoryUserDetailsManager를 사용해서 구현하고 있는거임
//        // 반환하는 객체를 빈으로 등록하기 때문에 InMemoryUserDetailsManager(userDetails)가 빈인데
//        // 반환하는 객체를 빈으로 등록될때는 메서드 명으로 등록됨. 즉, userDetailsManager()얘이름으로 등록됨
//        return new InMemoryUserDetailsManager(userDetails, admin);
//    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
