package com.springboot.auth.utils;

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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// 빈으로 등록하는 에노테이션
@Component


// 내가 궁금한 것 인메모리와 이것 중 둘중하나 사용한다해서
// 그걸 지우고 이골 쓰는데 선생님 말로는 그 인메모리가 디비에 저장되는게 아닌 인메모리..?
// 그냥 서버 끄면 데이터 사라지는거에 사용되는거 아닌가?
// 아니면 우리가 회원가입을 하면 h2에 저장되는데 여기서 h2는 디비역할을 하는건지 이게 맞는 것 같다
// 그래서 아까 인메모리 쩌구통해 회원가입하면 디비에 저장이 안됐던거
public class HelloUserDetailsManager2 implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final HelloAuthorityUtils helloAuthorityUtils;

    public HelloUserDetailsManager2(MemberRepository memberRepository, HelloAuthorityUtils helloAuthorityUtils) {
        this.memberRepository = memberRepository;
        this.helloAuthorityUtils = helloAuthorityUtils;
    }



    // 얘로 크리덴셜 관리
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember =memberRepository.findByEmail(username);
        Member findMember = optionalMember.orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
        );


        // 기존에는 멤버 정보를 가지고 이메일과 패스워드만 담음.
        // 근데 여기서는 findmember을 통해 더 많은 정보를 한번에 담는거? 왜 어썰라이즈안씀?
        // HelloUserDetails 여기서 유저정보 만듬그리고 findMember만 보냄
        // 여기 안에 Authorities가 구현되어 있는거임
        // findMember는 디비에서 이메일 기준으로 찾아온 정보라 롤이 담겨있음
        return new HelloUserDetails(findMember);
    }
    // 인터페이스 구현하면서 엔티티도 상속하는 코드 시작
    // 둘다 같이 구현할 수 있음
    private final class HelloUserDetails extends Member implements UserDetails {

        HelloUserDetails(Member member) {
            // HelloUserDetails는 멤버엔티티를 상속함
            // 그래서 다 오버라이드를 하지 않아도
            // HelloUserDetails는 Member의 정보들을 다 가지고 있어서
            // 필드에 있는거 다 쓸 수 있음 프라이빗 빼고
            setMemberId(member.getMemberId());
            setFullName(member.getFullName());
            setEmail(member.getEmail());
            setPassword(member.getPassword());
            // 앞서 말한 findMember는 디비에서 이메일 기준으로 찾아온 정보라
            // 롤이 담겨있음 그래서 여기서 setRoles 사용 가능한거
            setRoles(member.getRoles());
        }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // 왜 티스 앞에 꼭 붙이라는 거지?
            return helloAuthorityUtils.createAuthorities(this.getRoles());
        }

        @Override
        public String getUsername() {
            return this.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
