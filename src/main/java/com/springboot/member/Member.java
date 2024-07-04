package com.springboot.member;

import com.springboot.audit.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 인가가 엔티티에 담겨야함  그래서 권한 관련된 테이블을 따로 만듬
// 그래서 테이블을 만들고 조인시켜줘야함
// 하나의 권한은 여러명의 유저한테 사용 가능 그래서 다대다인데 일다대로 한쪽에서 이어버림
// 왤까. 한명의 회원이 여러개의 롤을 가질 수 있기 때문에 회원이 다
// 스프링시큐리티가 인가를 관리하기 때문에 반대편에서는 이어주지 않는거임!

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 100, nullable = false)
    private String fullName;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    // 암호화되기 때문에 길이를 100으로 길게 잡은 것
    // 그래서 8자에서 14자 이하 이런식으로 잡으면 해시함수로 암호화 안되서 안됨
    @Column(length = 100, nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

    // 권한과 관련된 롤테이블을 자동으로 생성해주는 에노테이션
    // 로딩에 관련된 문제들이 있어서 패치타입이거 넣어주는거임
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();


    public Member(String email) {
        this.email = email;
    }

    public Member(String email, String fullName, String password) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }

    public enum MemberStatus {
        MEMBER_ACTIVE("활동중"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("탈퇴 상태");

        @Getter
        private String status;

        MemberStatus(String status) {
           this.status = status;
        }
    }

    // 추가된 코드 이게 권한임
    // 권한 설정 넣을 때 스프링 시큐리티로 매핑되기 위해서
    // 디비에 저장할 때는 ROLE_는 엔티티 앞에 항상 붙여주기!!
    public enum MemberRole {
        ROLE_USER,
        ROLE_ADMIN
    }
}
