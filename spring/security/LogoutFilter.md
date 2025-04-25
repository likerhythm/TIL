# LogoutFilter
스프링 시큐리티의 LogoutFilter에 대해 알아보자. 아래는 LogoutFilter의 전문이다.
```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.security.web.authentication.logout;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

public class LogoutFilter extends GenericFilterBean {
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private RequestMatcher logoutRequestMatcher;
    private final LogoutHandler handler;
    private final LogoutSuccessHandler logoutSuccessHandler;

    public LogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers) {
        this.handler = new CompositeLogoutHandler(handlers);
        Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler cannot be null");
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.setFilterProcessesUrl("/logout");
    }

    public LogoutFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
        this.handler = new CompositeLogoutHandler(handlers);
        Assert.isTrue(!StringUtils.hasLength(logoutSuccessUrl) || UrlUtils.isValidRedirectUrl(logoutSuccessUrl), () -> {
            return logoutSuccessUrl + " isn't a valid redirect URL";
        });
        SimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        if (StringUtils.hasText(logoutSuccessUrl)) {
            urlLogoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
        }

        this.logoutSuccessHandler = urlLogoutSuccessHandler;
        this.setFilterProcessesUrl("/logout");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.requiresLogout(request, response)) {
            Authentication auth = this.securityContextHolderStrategy.getContext().getAuthentication();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(LogMessage.format("Logging out [%s]", auth));
            }

            this.handler.logout(request, response, auth);
            this.logoutSuccessHandler.onLogoutSuccess(request, response, auth);
        } else {
            chain.doFilter(request, response);
        }
    }

    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        if (this.logoutRequestMatcher.matches(request)) {
            return true;
        } else {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(LogMessage.format("Did not match request to %s", this.logoutRequestMatcher));
            }

            return false;
        }
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    public void setLogoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
        Assert.notNull(logoutRequestMatcher, "logoutRequestMatcher cannot be null");
        this.logoutRequestMatcher = logoutRequestMatcher;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.logoutRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }
}

```

## 생성자
`LogoutFilter`는 두 개의 생성자를 제공한다. 첫 번째 생성자는 개발자가 LogoutFilter를 커스터마이징 할 수 있도록 해주는 생성자이다.
```java
    // 개발자가 만든 LogoutSuccessHandler와 LogoutHandler를 주입 받아서 동작이 가능하다.
    public LogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers) {
        this.handler = new CompositeLogoutHandler(handlers);
        Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler cannot be null");
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.setFilterProcessesUrl("/logout");
    }
```
로그아웃 성공 후 개발자가 수행하고 싶은 로직이 있거나 로그아웃 과정에서 로그를 찍고 싶은 경우 LogoutSuccessHandler와 LogoutHandler를 만들어서 Config 파일에서 설정하면 된다.

두 번째 생성자는 스프링 시큐리티가 제공하는 로그아웃 기능만 수행하고 로그아웃 후 리다이렉트 될 URL만 개발자가 설정하는 생성자이다.
```java
    public LogoutFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
        this.handler = new CompositeLogoutHandler(handlers);
        Assert.isTrue(!StringUtils.hasLength(logoutSuccessUrl) || UrlUtils.isValidRedirectUrl(logoutSuccessUrl), () -> {
            return logoutSuccessUrl + " isn't a valid redirect URL";
        });
        SimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        if (StringUtils.hasText(logoutSuccessUrl)) {
            urlLogoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
        }

        this.logoutSuccessHandler = urlLogoutSuccessHandler;
        this.setFilterProcessesUrl("/logout");
    }
```

매개변수로 전달된 logoutSuccessUrl이 유효한지 확인하고(문자열의 길이가 1 이상인지, 문자열이 공백으로만 이루지지 않았는지 확인) 유효하다면 urlLogoutSuccessHandler의 defaultTargetUrl로 등록한다.

## doFilter
서블릿의 `Filter` 인터페이스를 구현하기 때문에 `ServletRequest`, `ServletResponse`를 받는 메서드를 구현하고 `HttpServletRequest`, `HttpServletResponse`로 동작하는 메서드에 역할을 위임한다.

```java
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.requiresLogout(request, response)) {
            Authentication auth = this.securityContextHolderStrategy.getContext().getAuthentication();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(LogMessage.format("Logging out [%s]", auth));
            }

            this.handler.logout(request, response, auth);
            this.logoutSuccessHandler.onLogoutSuccess(request, response, auth);
        } else {
            chain.doFilter(request, response);
        }
    }
```

요청이 로그아웃 요청인지 확인하고 로그아웃 요청이 아니라면 다음 필터로 넘긴다.
만약 로그아웃 요청이라면 LogoutHandler와 LogoutSuccessHandler로 로그아웃 과정을 밟는다.