package com.springboot.auth;

import com.springboot.auth.utils.HelloAuthorityUtils;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.Member;
import com.springboot.member.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// 빈으로 등록하는 에노테이션



// 내가 궁금한 것 인메모리와 이것 중 둘중하나 사용한다해서
// 그걸 지우고 이골 쓰는데 선생님 말로는 그 인메모리가 디비에 저장되는게 아닌 인메모리..?
// 그냥 서버 끄면 데이터 사라지는거에 사용되는거 아닌가?
// 아니면 우리가 회원가입을 하면 h2에 저장되는데 여기서 h2는 디비역할을 하는건지 이게 맞는 것 같다
// 그래서 아까 인메모리 쩌구통해 회원가입하면 디비에 저장이 안됐던거
public class HelloUserDetailsManager implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final HelloAuthorityUtils helloAuthorityUtils;

    public HelloUserDetailsManager(MemberRepository memberRepository, HelloAuthorityUtils helloAuthorityUtils) {
        this.memberRepository = memberRepository;
        this.helloAuthorityUtils = helloAuthorityUtils;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember =memberRepository.findByEmail(username);
        Member findMember = optionalMember.orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
        );
// 구현체를 만들 때
        List<GrantedAuthority> authorities = helloAuthorityUtils
                .createAuthorities(findMember.getEmail());

        return new User(findMember.getEmail(), findMember.getPassword(), authorities);
    }
}
