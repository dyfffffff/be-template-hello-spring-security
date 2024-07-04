package com.springboot.member;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryMemberService implements MemberService {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public InMemoryMemberService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Member createMember(Member member) {
        List<GrantedAuthority> authorities =
                // dto가 평문이라 암호화 거쳐야함(멤버에 권한을 넣어주는거)
                createAuthorities(Member.MemberRole.ROLE_USER.name());
        // 그래서 암호화로 거치게 만듬
        String encrytedpassword = passwordEncoder.encode(member.getPassword());
        // 만든 걸 이제 보내야함
        // 아이디(이메일은) 암호화하지 않았으니까 그냥 가져와도 되지만,
        // 패스워드는 위에서 암호화한걸 만들었으니 이걸 가져와야함,
        // 그리고 여제껏 만든 이것들을 authorities에 담는거임
        UserDetails userDetails = new User(member.getEmail(), encrytedpassword, authorities);
        // 뭐였더리.. 만든걸 userDetailsManager에 등록하는거임(그게 크리덴셜)??
        userDetailsManager.createUser(userDetails);

        return member;

    }
    // 문자열을 요소로 가지는 어레이를 받는다는 뜻
    // List<GrantedAuthority> 개개인의 권한을 조회해서 요소로 갖는다는 것
    // 이거의 반환값인 new SimpleGrantedAuthority(role))권한을 갖고 있는 객체를 받음
    // 근데 왜 리스트로 권한을 받을까? 한 유저가 하나의 롤만 가지지않음,
    // 즉 인가 처리할 때 여러가지 롤을 받을 수있기 때문에 리스트로 받는게 기본임
    private List<GrantedAuthority> createAuthorities(String... roles) {
        return Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }
}
