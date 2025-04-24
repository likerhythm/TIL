# SecurityContextHolderFilter의 등장 배경
SecurityFilterChain에서 사용되던 필터 중 `SecurityContextPersistenceFilter`가 있었는데 Spring Security 6부터 deprecated되고 `SecurityContextHolderFilter`로 대체되었다.
[공식 문서](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/web/context/SecurityContextHolderFilter.html)에 따르면
대체 된 이유는 SecurityContextRepository에 SecurityContext가 자동으로 저장되는 것이 유연하지 못하기 때문이라고 한다.
개발자가 SecurityContext를 저장할 시점에 직접 Repository에 저장할 수 있도록 수정된 것이 SecurityContextHolderFilter라고 보면 된다.

아래는 각각 `SecurityContextPersistenceFilter`와 `SecurityContextHolderFilter`의 코드 전문이다.
코드를 자세히 보면 `SecurityContextPersistenceFilter`에서는 `repo.saveContext`로 SecurityContext를 저장하는 메서드가 자동으로 호출된다.
하지만 `SecurityContextHolderFilter`는 `saveContext`를 자동으로 호출하는 코드가 없다.

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.security.web.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

/** @deprecated */
@Deprecated
public class SecurityContextPersistenceFilter extends GenericFilterBean {
    static final String FILTER_APPLIED = "__spring_security_scpf_applied";
    private SecurityContextRepository repo;
    private SecurityContextHolderStrategy securityContextHolderStrategy;
    private boolean forceEagerSessionCreation;

    public SecurityContextPersistenceFilter() {
        this(new HttpSessionSecurityContextRepository());
    }

    public SecurityContextPersistenceFilter(SecurityContextRepository repo) {
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        this.forceEagerSessionCreation = false;
        this.repo = repo;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getAttribute("__spring_security_scpf_applied") != null) {
            chain.doFilter(request, response);
        } else {
            request.setAttribute("__spring_security_scpf_applied", Boolean.TRUE);
            if (this.forceEagerSessionCreation) {
                HttpSession session = request.getSession();
                if (this.logger.isDebugEnabled() && session.isNew()) {
                    this.logger.debug(LogMessage.format("Created session %s eagerly", session.getId()));
                }
            }

            HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
            SecurityContext contextBeforeChainExecution = this.repo.loadContext(holder);
            boolean var10 = false;

            try {
                var10 = true;
                this.securityContextHolderStrategy.setContext(contextBeforeChainExecution);
                if (contextBeforeChainExecution.getAuthentication() == null) {
                    this.logger.debug("Set SecurityContextHolder to empty SecurityContext");
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", contextBeforeChainExecution));
                }

                chain.doFilter(holder.getRequest(), holder.getResponse());
                var10 = false;
            } finally {
                if (var10) {
                    SecurityContext contextAfterChainExecution = this.securityContextHolderStrategy.getContext();
                    this.securityContextHolderStrategy.clearContext();
                    this.repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
                    request.removeAttribute("__spring_security_scpf_applied");
                    this.logger.debug("Cleared SecurityContextHolder to complete request");
                }
            }

            SecurityContext contextAfterChainExecution = this.securityContextHolderStrategy.getContext();
            this.securityContextHolderStrategy.clearContext();
            this.repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
            request.removeAttribute("__spring_security_scpf_applied");
            this.logger.debug("Cleared SecurityContextHolder to complete request");
        }
    }

    public void setForceEagerSessionCreation(boolean forceEagerSessionCreation) {
        this.forceEagerSessionCreation = forceEagerSessionCreation;
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }
}

```

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.security.web.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Supplier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

public class SecurityContextHolderFilter extends GenericFilterBean {
    private static final String FILTER_APPLIED = SecurityContextHolderFilter.class.getName() + ".APPLIED";
    private final SecurityContextRepository securityContextRepository;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public SecurityContextHolderFilter(SecurityContextRepository securityContextRepository) {
        Assert.notNull(securityContextRepository, "securityContextRepository cannot be null");
        this.securityContextRepository = securityContextRepository;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(request, response);
        } else {
            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
            Supplier<SecurityContext> deferredContext = this.securityContextRepository.loadDeferredContext(request);

            try {
                this.securityContextHolderStrategy.setDeferredContext(deferredContext);
                chain.doFilter(request, response);
            } finally {
                this.securityContextHolderStrategy.clearContext();
                request.removeAttribute(FILTER_APPLIED);
            }

        }
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }
}

```

# SecurityContextHolderFilter의 역할
코드를 하나하나 뜯어보며 SecurityContextHolderFilter가 어떤 역할을 수행하는지 알아보자

우선 코드 전문을 스윽 살펴보고 넘어가자
```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.security.web.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Supplier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

public class SecurityContextHolderFilter extends GenericFilterBean {
    private static final String FILTER_APPLIED = SecurityContextHolderFilter.class.getName() + ".APPLIED";
    private final SecurityContextRepository securityContextRepository;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public SecurityContextHolderFilter(SecurityContextRepository securityContextRepository) {
        Assert.notNull(securityContextRepository, "securityContextRepository cannot be null");
        this.securityContextRepository = securityContextRepository;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(request, response);
        } else {
            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
            Supplier<SecurityContext> deferredContext = this.securityContextRepository.loadDeferredContext(request);

            try {
                this.securityContextHolderStrategy.setDeferredContext(deferredContext);
                chain.doFilter(request, response);
            } finally {
                this.securityContextHolderStrategy.clearContext();
                request.removeAttribute(FILTER_APPLIED);
            }

        }
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }
}

```

## 생성자
```java
    public SecurityContextHolderFilter(SecurityContextRepository securityContextRepository) {
        Assert.notNull(securityContextRepository, "securityContextRepository cannot be null");
        this.securityContextRepository = securityContextRepository;
    }
```
생성자는 `SecurityContextRepository`를 전달 받는데 이게 null이면 에러가 발생하도록 설계되어 있다.

## doFilter 메서드
핵심 로직이다. 차근차근 알아보자
### 왜 두 개의 doFilter 메서드를 사용할까
아래와 같이 매개변수로 ServletRequest와 ServletReponse를 전달 받은 doFilter 메서드가 매개변수를 HttpServletRequest와 HttpServletResponse로 캐스팅하고 오버로딩된 doFilter 메서드를 호출한다.
```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
}
```
이렇게 doFilter가 두 번 호출되게 설계된 이유는 다음과 같다
1. 서블릿 필터의 인터페이스는 ServletRequest와 ServletResponse를 사용한다.
2. Spring Security Web은 Http 기반으로 설계되었다.

첫 번째 이유를 자세히 알아보자.
`SecurityContextHolderFilter`는 `GenericFilterBean`를 상속 받는데, `GenericFilterBean`는 `Filter` 인터페이스를 구현한다.
아래는 `Filter`의 전문이다. doFilter 메서드가 매개변수로 `ServletRequest`와 `ServletResponse`를 사용하는 걸 알 수 있다.
그래서 `SecurityContextHolderFilter`도 이에 맞춰서 개발되어야 한다.
```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package jakarta.servlet;

import java.io.IOException;

public interface Filter {
    default void init(FilterConfig filterConfig) throws ServletException {
    }

    void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;

    default void destroy() {
    }
}

```

두 번째 이유를 자세히알아보자.
Spring Security Web은 Http를 기반으로 설계되었기 때문에 `HttpServletRequest`와 `HttpServletResponse`를 사용한다.
그래서 이걸로 캐스팅된 doFilter를 오버로딩하여 사용하는 것이다.

> Http 기반이 아닌 애플리케이션에는 Security를 어떻게 적용하지?

WebSocket이나 다른 프로토콜을 사용한다면 Spring Security Core를 직접 다루어야 한다.
Spring Security Core로는 웹에 한정되지 않고 인증과 인가에 필요한 범용적인 설정을 할 수 있다.

### doFilter의 역할
아래는 실제로 실행되는 doFilter 로직이다. 코드를 읽으며 차근차근 알아보자.
```java
private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    if (request.getAttribute(FILTER_APPLIED) != null) {
        chain.doFilter(request, response);
    } else {
        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
        Supplier<SecurityContext> deferredContext = this.securityContextRepository.loadDeferredContext(request);

        try {
            this.securityContextHolderStrategy.setDeferredContext(deferredContext);
            chain.doFilter(request, response);
        } finally {
            this.securityContextHolderStrategy.clearContext();
            request.removeAttribute(FILTER_APPLIED);
        }

    }
}
```

if문에서 request에 `FILTER_APPLIED`라는 속성이 존재하는지 확인한다. 만약 존재한다면(null이 아니라면) 다음 필터로 넘어간다.
```java
        if (request.getAttribute(FILTER_APPLIED) != null) {
        chain.doFilter(request, response);
        }
```
여기서 `FILTER_APPLIED`는 현재 필터가 중복 적용되지 않도록 하기 위해 사용하는 일종의 flag다.
`FILTER_APPLIED`는 클래스 상단에 이렇게 상수로 정의되어 있다.
```java
private static final String FILTER_APPLIED = SecurityContextHolderFilter.class.getName() + ".APPLIED";
```

request에 `FILTER_APPLIED` 속성이 없다면, 즉 현재 필터가 아직 실행되지 않았다면 아래와 같이 메인 로직이 실행된다.
```java
} else {
    request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
    Supplier<SecurityContext> deferredContext = this.securityContextRepository.loadDeferredContext(request);

    try {
        this.securityContextHolderStrategy.setDeferredContext(deferredContext);
        chain.doFilter(request, response);
    } finally {
        this.securityContextHolderStrategy.clearContext();
        request.removeAttribute(FILTER_APPLIED);
    }
}
```

우선 request에 `FILTER_APPLIED` 속성을 추가하여 현재 필터가 중복 실행되지 않도록 한다.
```java
request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
```

`SecurityContextRepository`로부터 SecurityContext를 가져온다.
Supplier에는 객체가 아니라 객체의 생성 방법이 담기기 때문에 필요할 때 객체를 생성할 수 있는 지연 로딩이 가능하다.
여기서는 SecurityContext가 정말 필요할 때 이를 생성하기 위해 Supplier를 사용한 모습이다.
그래서 변수 이름도 `deferredContext(연기된 Context)`이다.
```java
Supplier<SecurityContext> deferredContext = this.securityContextRepository.loadDeferredContext(request);
```

`deferredContext`를 `securityContextHolderStrategy`에게 전달한다. strategy는 구현체에 맞는 전략을 취하고 리턴한다.
그리고 다음 필터로 넘어간다. 여기까지가 try문에서 수행되는 일이다.
finally문은 모든 필터가 수행되었을 때, 즉 `chain.doFilter`가 리턴되었을 때 실행된다.
strategy의 context를 지우고 request에서 FILTER_APPLIED 속성을 지운다.
요청이 들어오기전 상태로 만드는 것으로 생각하면될 것 같다.
```java
    try {
        this.securityContextHolderStrategy.setDeferredContext(deferredContext);
        chain.doFilter(request, response);
    } finally {
        this.securityContextHolderStrategy.clearContext();
        request.removeAttribute(FILTER_APPLIED);
    }
```

마지막 메서드는 SecurityContextHolderStrategy 구현체를 바꿔 끼울 수 있도록 해주는 setter이다.
```java
    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }
```

## 역할 정리
`SecurityContextHolderFilter`의 역할을 정리해보자
- FilterChain의 진입점으로서 SecurityContext를 지연 로딩 방식으로 공급한다.
- 요청이 끝난 후에 SecurityContext를 정리하는 역할을 한다.

# 번외
`SecurityContextHolderStrategy`가 `deferredContext`를 넘겨 받고 무슨 일을하는지 궁금해져서 간단하게 알아보려고 한다.
`SecurityContextHolderStrategy`는 인터페이스이고, 따로 설정하지 않으면 `ThreadLocalSecurityContextHolderStrategy` 구현체가 사용된다.
이 구현체는 SecurityContext를 ThreadLocal에 저장한다.
아래는 `ThreadLocalSecurityContextHolderStrategy`의 전문이다.
```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.security.core.context;

import java.util.function.Supplier;
import org.springframework.util.Assert;

final class ThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
    private static final ThreadLocal<Supplier<SecurityContext>> contextHolder = new ThreadLocal();

    ThreadLocalSecurityContextHolderStrategy() {
    }

    public void clearContext() {
        contextHolder.remove();
    }

    public SecurityContext getContext() {
        return (SecurityContext)this.getDeferredContext().get();
    }

    public Supplier<SecurityContext> getDeferredContext() {
        Supplier<SecurityContext> result = (Supplier)contextHolder.get();
        if (result == null) {
            SecurityContext context = this.createEmptyContext();
            result = () -> {
                return context;
            };
            contextHolder.set(result);
        }

        return result;
    }

    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(() -> {
            return context;
        });
    }

    public void setDeferredContext(Supplier<SecurityContext> deferredContext) {
        Assert.notNull(deferredContext, "Only non-null Supplier instances are permitted");
        Supplier<SecurityContext> notNullDeferredContext = () -> {
            SecurityContext result = (SecurityContext)deferredContext.get();
            Assert.notNull(result, "A Supplier<SecurityContext> returned null and is not allowed.");
            return result;
        };
        contextHolder.set(notNullDeferredContext);
    }

    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}

```

우선 `contextHolder`로 ThreadLocal을 사용한다.
```java
private static final ThreadLocal<Supplier<SecurityContext>> contextHolder = new ThreadLocal();
```

`setDeferredContext` 메서드만 살펴보면, 우선 Supplier를 전달 받아서 null 검증을 수행한다
그리고 Supplier에서 SecurityContext를 생성하는 방식을 수정한다.
수정된 방식은 간단하게 SecurityContext 객체를 생성해서, 해당 객체가 null이면 에러를 발생시키도록 한다.
```java
    public void setDeferredContext(Supplier<SecurityContext> deferredContext) {
        Assert.notNull(deferredContext, "Only non-null Supplier instances are permitted");
        Supplier<SecurityContext> notNullDeferredContext = () -> {
            SecurityContext result = (SecurityContext)deferredContext.get();
            Assert.notNull(result, "A Supplier<SecurityContext> returned null and is not allowed.");
            return result;
        };
        contextHolder.set(notNullDeferredContext);
    }
```

정리해보면, `ThreadLocalSecurityContextHolderStrategy`는 supplier의 객체 생성 방식에 null 검사 로직을 추가하여 `contextHolder`에게 전달한다.
이때 궁금했던 점은, contextHolder가 deferredContext 변수에 접근할 수 있냐였다. 알아보니 자바의 람다는 캡쳐링이라는 기능을 제공하는데,
이는 람다 밖에서 선언된 변수를 람다 내부에 저장할 수 있다는 기능이다. 그래서 contextHolder에서 deferredContext를 호출할 수 있다.