###### 인프런 김영한 강사님의 '스프링 핵심 원리 - 기본편'을 기반으로 작성한 글입니다.

`@AutoWired`로 빈 객체를 주입해야 하는데 해당 객체가 빈으로 등록되지 않았을 때에도 애플리케이션이 정상적으로 동작해야 하려면 어떻게 해야 할까
3가지 방법이 있다.

# `required = false` 옵션 사용
`@AutoWired`의 required 옵션을 false로 두면 빈으로 등록되지 않은 객체를 주입하려는 메서드는 아예 호출되지 않는다.
```java
@AutoWired(required = false)
public void method (Member member) {
    // ...
}
```

# `@Nullable` 어노테이션 사용
주입 받으려는 객체에 `@Nullable` 어노테이션을 붙이면 빈으로 등록되지 않은 경우 null이 주입된다.
```java
@AutoWired
public void method (@Nullable Member member) {
    // ...
}
```

# `Optional` 사용
Optional로 주입 객체를 감싸면 빈으로 등록되지 않은 경우 `Optional.empty`가 주입된다.
```java
@AutoWired
public void method (Optional<Member> member) {
    // ...
}
```
