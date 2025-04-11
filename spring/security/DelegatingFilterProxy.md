###### [Spring Security 공식문서](https://docs.spring.io/spring-security/reference/servlet/getting-started.html)를 바탕으로 정리한 글입니다.

스프링의 ApplicationContext에는 보안에 필요한 여러 필터들이 스프링 빈으로 등록되어 있다.
**서블릿 컨테이너**는 표준 서블릿 필터를 동작시킨다. 스프링 빈으로 등록된 필터들이 어떻게 표준 서블릿 필터로서 서블릿 컨테이너에서 동작하게 할 수 있을까?

> 서블릿 컨테이너란 HTTP 요청을 받거나 응답을 보내고, 적절한 서블릿을 호출해서 요청을 처리하는 컨테이너이다.
스프링이 제공하고 서블릿 컨테이너가 호출하는 대표적인 서블릿으로는 `DispatcherServlet`이 있다.

스프링 빈으로 등록된 필터가 서블릿 컨테이너의 필터 체인에서 동작하도록 하기 위해선 `DelegatingFilterProxy`가 필요하다.
`DelegatingFilterProxy`는 스프링이 제공하는 필터 중 하나인데, 서블릿 컨테이너가 스프링 빈으로 등록된 필터를 호출할 수 있도록 해준다.



# 참조
https://docs.spring.io/spring-security/reference/servlet/architecture.html