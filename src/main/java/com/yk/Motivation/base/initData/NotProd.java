package com.yk.Motivation.base.initData;

import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.member.service.MemberService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class NotProd {
    @Bean
    public ApplicationRunner init(MemberService memberService) {

        return args -> {
            Member member1 = memberService.join("admin", "1234", "admin", "admin@test.com", null).getData();
            Member member2 = memberService.join("user1", "1234", "nickname1", "user1@test.com", null).getData();
            Member member3 = memberService.join("user2", "1234", "nickname2", "user2@test.com", null).getData();
            Member member4 = memberService.join("user3", "1234", "nickname3", "user3@test.com", null).getData();

            memberService.setEmailVerified(member1);
            memberService.setEmailVerified(member2);
            memberService.setEmailVerified(member3);
        };
    }



}