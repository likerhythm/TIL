###### 인프런 김영한 강사님의 '스프링 핵심 원리 - 기본편'을 기반으로 작성한 글입니다.

request 스코프는 웹 클라이언트로부터 요청이 들어오면 생성되어서 서버가 응답을 하면 소멸하는 생존 범위를 의미한다.
request 스코프 객체는 프로토타입 스코프 객체와 다르게 객체가 소멸할 때까지 스프링 컨테이너가 관리한다. 즉, `@PreDestroy`를 호출할 수 있다.
클라이언트 별로 로그를 찍고 싶을 때 사용하기 적당하다.
코드로 그 예시를 알아보자

```java
@Component
@Scope("request")
public class MyLogger {

    private String uuid;
    private String requestURL;

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void log(String message) {
        System.out.println("[" + uuid + "]" + "[" + requestURL + "]" + message);
    }

    @PostConstruct
    public void init() {
        uuid = UUID.randomUUID().toString();
    }
    
    public void close() {
        System.out.println("close");
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class LogDemoService {
    
    private final MyLogger myLogger;
    
    public void logic(String id) {
        myLogger.log("service id = " + id);
    }
}
```

```java
@Controller
@RequiredArgsConstructor
public class LogDemoController {
    
    private final LogDemoService logDemoService;
    private final MyLogger myLogger;
    
    @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        myLogger.setRequestURL(requestURL);
        myLogger.log("controller test");
        logDemoService.logic("testId");
        return "OK";
    }
}
```

`MyLogger`는 request 스코프로 관리되는 빈이고, Controller와 Service가 각각 MyLogger를 주입받고 있다.
잘 보면 Controller가 Service에게 매개변수로 MyLogger를 전달하지 않는다.
왜냐하면 주입받는 myLogger가 같은 요청 흐름 안에서는 같은 객체를 가리키기 때문이다.

하지만 이 코드는 정상적으로 동작하지 않는다. 왜냐하면 MyLogger는 요청이 들어왔을 때 생성되는데, 여기서는 스프링 컨테이너가 뜰 때 MyLogger가 Controller와 Service로 주입되는 방식이기 때문이다.
이는 Provider를 통해 해결할 수 있다. 아래와 같이 ObjectProvider를 사용해서 요청이 들어온 이후에 객체를 가져오면 앞서 언급한 문제가 발생하지 않는다. 
Controller에서도 마찬가지로 ObjectProvider를 사용해서 객체를 가져오면 되고, 이 객체는 같은 요청 내에서는 같은 객체임이 보장된다.
```java
@Service
@RequiredArgsConstructor
public class LogDemoService {
    
    private final ObjectProvider<MyLogger> myLoggerProvider;
    
    public void logic(String id) {
        MyLogger myLogger = myLoggerProvider.getObject();
        myLogger.log("service id = " + id);
    }
}
```

## request 스코프의 프록시 모드
개발자들은 request 스코프를 사용할 때 `ObjectProvider`를 매번 추가하기 귀찮았나보다. 기어이 '프록시 모드'라는 아주 편리한 기능이 추가됐다.
일단 코드를 보자
```java
@Component
@Scope(value = "request", proxyMode = "ScopedProxyMode.TARGET_CLASS")
public class MyLogger {
    // ...
}
```

`@Scope` 설정 값으로 `proxyMode`라는게 추가됐다. 이걸 추가하면 스프링이 이 클래스의 가짜 프록시 클래스를 만들고 웹 요청의 유무와 관계 없이 미리 의존관계 주입을 해 놓는다.
프록시 객체가 진짜 객체인 것처럼 미리 주입되어 있다가 웹 요청이 들어오고 프록시 객체를 호출하게 되면, 그때가 되어서 진짜 객체의 메서드를 프록시 객체가 호출하는 방식으로 동작한다.

이 프록시 객체는 CGLIB 라이브러리가 만들어서 주입한다.
여기서 주의할 점은 코드 상 마치 싱글톤처럼 동작하는 걸로 보이지만 실제로는 request 스코프로 동작하기 때문에 웹 요청이 들어온 이후에 사용해야 한다는 점이다.
프로토타입이나 request 스코프처럼 특별한 스코프는 정말 필요한 순간이 아니라면 사용하지 않는게 유지보수 면에서 좋다.