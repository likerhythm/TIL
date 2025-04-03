package org.example.springtest.scope;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.Annotation;

@SpringBootTest
public class PrototypeScopeTest {

    @Autowired
    ApplicationContext ac;

    @Test
    public void componentScanTest() {
//        PrototypeScopeBean bean = ac.getBean(PrototypeScopeBean.class);
    }
}
