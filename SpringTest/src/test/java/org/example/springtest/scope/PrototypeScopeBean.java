package org.example.springtest.scope;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class PrototypeScopeBean {

    @PostConstruct
    public void init() {
        System.out.println("PrototypeScopeBean.init");
    }
}
