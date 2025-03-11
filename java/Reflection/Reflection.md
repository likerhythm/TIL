# Reflection이란?
Reflection은 heap 영역에 생성된 객체로 클래스를 다룰 수 있게 해주는 자바 api이다.
클래스의 생성자를 호출하여 인스턴스를 만들거나, 메서드, 필드 변수 등에 접근할 수 있다.
특히 런타임에 메서드와 필드 변수의 이름이나 타입 등 클래스의 구체적인 정보를 다룰 수 있게 해준다.

## Class class
Reflection을 위해선 클래스에 대한 정보가 필요하고 이는 `Class` 클래스가 가지고 있다. 
자바 21 api 문서에서 [Class 클래스](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html)에 대한 내용을 참고해 보면,
Class 클래스는 public 생성자가 없는 대신 JVM이 자동으로 생성한다고 한다.
생성되는 시기는 다음 메서드 중 하나가 호출된 경우이다.
- [ClassLoader::defineClass](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ClassLoader.html#defineClass(java.lang.String,byte%5B%5D,int,int))
- [java.lang.invoke.MethodHandles.Lookup::defineClass](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#defineClass(byte%5B%5D))
- [java.lang.invoke.MethodHandles.Lookup::defineHiddenClass](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#defineHiddenClass(byte%5B%5D,boolean,java.lang.invoke.MethodHandles.Lookup.ClassOption...))

JVM의 클래스 로더가 클래스를 로드할 때 바이트 코드를 읽어와서 Class 객체를 생성한다.

# 사용법
- {클래스 이름}.class
- {인스턴스 이름}.getClass()
- Class.forName({클래스 이름})

위의 세 가지 방법으로 Class 클래스를 가져올 수 있다.
이때 각각의 방법으로 가져온 Class는 모두 같은 참조이다.

# 장점
- 런타임에 접근 제한자와 관계 없이 클래스의 필드 변수와 메서드에 접근할 수 있기 때문에 유연한 작업 수행이 가능하다.

# 단점
- 남발할 경우 캡슐화를 저해한다.
- 리플렉션으로 필드와 메서드로 접근하는 것은 일반적인 방법으로 접근하는 것보다 성능적으로 느리다.

# 그럼에도 사용하는 이유
대표적으로 Spring Framework의 어노테이션 예시를 들 수 있다.
@Controller, @Service, @Repository 등 어노테이션을 붙이면 스프링이 알아서 관리해준다.
그럴 수 있는 이유는 Reflection을 통해 어노테이션 정보를 확인할 수 있기 때문이다.

# 참조
https://velog.io/@alsgus92/Java-Reflection%EC%9D%80-%EB%AC%B4%EC%97%87%EC%9D%B4%EA%B3%A0-%EC%96%B8%EC%A0%9C%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-%EA%B2%83%EC%9D%B4-%EC%A2%8B%EC%9D%84%EA%B9%8C<br>
https://steady-coding.tistory.com/609#google_vignette