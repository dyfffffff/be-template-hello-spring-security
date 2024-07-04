package com.springboot.config;

import com.springboot.auth.utils.HelloAuthorityUtils;
import com.springboot.member.DBMemberService;
import com.springboot.member.InMemoryMemberService;
import com.springboot.member.MemberRepository;
import com.springboot.member.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class JavaConfiguration {
    // 서비스(에노테이션)는 대체가 가능
    // 여기서는 컴포넌트와 빈을 사용하는 것ㄷ중 빈으로 등록해서 사용하는 방법을 택한거
    @Bean
    public MemberService inMemoryMemberService(MemberRepository memberRepository,
                                               PasswordEncoder passwordEncoder,
                                               HelloAuthorityUtils helloAuthorityUtils) {
        return new DBMemberService(memberRepository, passwordEncoder, helloAuthorityUtils);
    }
}
