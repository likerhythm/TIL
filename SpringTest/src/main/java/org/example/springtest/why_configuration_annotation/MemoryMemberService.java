package org.example.springtest.why_configuration_annotation;

public class MemoryMemberService implements MemberService {

    private final MemberRepository memberRepository;

    public MemoryMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
}
