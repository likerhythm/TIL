###### 인프런 김영한 강사님의 '스프링 핵심 원리 - 기본편'을 기반으로 작성한 글입니다.

1. ApplicationContext를 직접 주입 받아 해결하기
2. ObjectFactory, ObjectProvider로 해결하기
3. 자바 표준 JSR-330 Provider로 해결하기

여기서 1번 해결법은 [이 글](./싱글톤%20빈%20내부에서%20프로토타입%20스코프%20객체를%20사용할%20경우%20문제점.md)의 마지막 부분에 설명이 있다.
이제 Provider를 사용해서 해결하는 법을 알아보자.

## ObjectFactory, ObjectProvider로 해결하기
ObjectFactory, ObjectProvider는 스프링 컨테이너에서 객체를 가져오는 역할을 수행한다.
스프링 컨테이너에서 직접 가져오기 때문에 가져올 객체가 프로토타입 스코프이면 가져올 때마다 새로운 객체가 생성되는 것이 보장된다.

ObjectFactory가 먼저 등장하고 여러 편의 기능이 추가되어 ObjectProvider가 만들어졌다.
이렇게 역할을 분리하면 테스트 시 ObjectProvider를 대체하는 mock 객체를 만들기 쉬워진다.
또한 `getIfAvailable()`, `ifAvailable()` 등 편리한 기능도 제공한다.

사용법은 다음과 같다.
```java
public class Client {
    
    @Autowired
    private ObjectProvider<PrototypeBean> objectProvider; // 프로토타입 스코프 빈을 가져오는 Provider
    
    public void logic() {
        Prototype prototype = objectProvider.getObject();
        // ...
    }
}
```

직접 `ApplicationContext`를 다루는 것보다 쉬운 방법이지만 `ObjectProvider`는 스프링에서 제공하기 때문에 스프링에 의존적인 코드가 작성된다.
하지만 의존적이더라도 컨테이너를 스프링 컨테이너가 아닌 다른 컨테이너로 바꾸지 않는다면 크게 상관이 없다.

만약 컨테이너를 바꿔야 할 수도 있다면 어떻게 하면 좋을까?
자바 표준 Provider로 해결하는 법을 알아보자.

## 자바 표준 JSR-330 Provider로 해결하기
자바 표준도 Provider를 제공한다. `javax.inject.Provider` 패키지에 속해있다.
사용법은 아래와 같다.

```java
public class Client {
    
    @Autowired
    private Provider<PrototypeBean> provider; // 프로토타입 스코프 빈을 가져오는 Provider
    
    public void logic() {
        Prototype prototype = provider.get();
        // ...
    }
}
```

자바 표준을 사용하면 컨테이너가 바뀌어도 그대로 동작한다는 장점이 있다.
한 가지 단점은 별도의 라이브러리가 필요하다.
스프링부트 3.0 미만 버전은 `javax.inject:javax.inject:1`,
스프링부트 3.0 이상 버전은 `javax.inject:jakarta.inject-api:2.0.1` 라이브러리를 gradle에 추가해야 한다.

컨테이너를 바꿀 일이 없다면 웬만해선 스프링이 제공하는 `ObjectProvider`를 사용하는 게 좋다.

하지만 항상 새로운 객체를 생성한다는 프로토타입 스코프 객체가 사용될 일이 잘 없고, 있다고 하더라도 싱글톤 객체로 대부분 해결이 가능하기 때문에 Provider를 사용할 일은 거의 없을 것이다.