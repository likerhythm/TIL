package org.example.springtest.why_configuration_annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppConfigClassNameTest {

    static ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    public static void main(String[] args) {
        AppConfig appConfigBean = ac.getBean(AppConfig.class);
        MemberService memberServiceBean = ac.getBean(MemberService.class);
        MemberRepository memberRepositoryBean = ac.getBean(MemberRepository.class);

        System.out.println("appConfigBean = " + appConfigBean.getClass());
        System.out.println("memberServiceBean = " + memberServiceBean.getClass());
        System.out.println("memberRepositoryBean = " + memberRepositoryBean.getClass());
    }
}
