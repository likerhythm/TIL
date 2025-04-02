###### 인프런 김영한 강사님의 '스프링 핵심 원리 - 기본편'을 기반으로 작성한 글입니다.

`@Qualifier({문자열})`은 문자열을 사용한다. 문자열은 컴파일 단계에서 올바른 값인지 판단할 수 없다.
그렇기 때문에 실수로 다른 문자열을 입력하면 원하는대로 동작하지 않을 수 있다.

이를 방지하기 위해서 커스텀 어노테이션을 활용해보자.
`@Qualifier` 어노테이션을 확인해보면 다음과 같은 구조다.

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Qualifier {
    String value() default "";
}
```

`@Qualifier`와 완전히 동일하게 동작하는 어노테이션을 만들기 위해 Qualifier에 달린 어노테이션을 그대로 커스텀 어노테이션에 달고
`@Qualifier` 자체도 달아둔다.
```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {
    
}
```

이렇게 만든 커스텀 어노테이션은 `@Qualifier("mainDiscountPolicy")`와 동일하게 동작하면서, 문자열을 잘못 입력하는 실수를 방지한다.
그리고 IDE의 기능에 따라 `MainDiscountPolicy`가 사용된 클래스를 한 번에 확인할 수도 있다.