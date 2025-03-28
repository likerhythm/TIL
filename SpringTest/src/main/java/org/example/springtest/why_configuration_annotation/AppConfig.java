package org.example.springtest.why_configuration_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        System.out.println("MemberService 등록");
        return new MemoryMemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("MemberRepository 등록");
        return new MemoryMemberRepository();
    }
}
