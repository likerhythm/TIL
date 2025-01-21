# 자바의 메모리 영역

자바 애플리케이션이 실행되어 JVM이 구동되면 운영체제로부터 메모리를 할당받는다. JVM은 할당 받은 메모리를 효율적으로 사용하기 위해 메모리 구역을 다음과 같이 나눈다.

 

1. 메소드 영역

2. 힙 영역

3. 스택 영역

 

## 메소드 영역
메소드 영역에는 간단하게 말해 클래스 정보가 저장된다. 클래스의 상수, static 변수, 코드가 저장된다. 이 영역은 프로그램이 시작할 때 로드되고 종료될 때 소멸된다.

## 힙 영역
힙 영역에는 객체가 저장된다. 힙 영역은 메소드 영역에 비해 복잡한데, 왜냐하면 객체를 얼마나 효율적으로 관리하냐에 따라 메모리의 효율성이 결정되기 때문이다.

 

JVM은 객체를 관리하기 위해 GC(Garbage Collection)를 사용한다. GC는 일정 주기마다 힙에 저장된 객체를 확인하여 참조되지 않는 객체를 소멸시키는 역할을 한다. 이런 과정을 통해 메모리를 효율적으로 관리할 수 있지만 GC의 주기가 짧아질수록 성능이 떨어진다.

### String Constant Pool
힙 영역 내부에 GC의 영향을 받지 않는 String Constant Pool이라는 영역도 존재한다. 엄밀히 말하면 '일반적으로' GC의 영향을 받지 않는다라고 말해야 하지만 그냥 GC가 관리하지 않는 영역이라고 생각하는게 편하다. String Constant Pool 영역에는 리터럴 String 객체가 보관된다. 리터럴 String이란 아래와 같이 new 키워드를 사용하지 않고 생성한 String 객체를 칭한다.

```
String str = "Hello";
```

이런 방식으로 생성된 String 객체는 힙 영역 내부의 String Constant Pool에 저장된다. 이 영역에 저장된 String 객체는 여러 리터럴 객체로부터 참조될 수 있다. 예를 들어 다음과 같이 str1, str2가 같은 문자열로 선언된 경우 str1과 str2는 같은 객체를 가리킨다.

```
String str1 = "Hello"; // 주소 a를 가리킴
String str2 = "Hello"; // 주소 a를 가리킴
```

그렇기 때문에 서로 다른 객체이지만 일반적인 객체와 다르게 "==" 연산 결과 true를 반환한다.

```
str1 == str2; // true
```

참고로 다음도 성립한다.

```
String noneLiteralStr = new String("Hello");
String literalStr = "Hello";

noneLiteralStr.equals(literalStr); // true;
literalStr.equals(noneLiteralStr); // true;

literalStr == noneLiteralStr; // false;
```

### String Constant Pool주의할 점
String Constant Pool은 GC의 관리를 받지 않는다. 그렇기 때문에 자칫하면 메모리 부족 문제를 야기할 수 있다. 특히, String 클래스는 intern이라는 메서드를 제공하는데 이는 String Constant Pool에 강제로 해당 객체를 집어 넣는 메서드이다. 이 메서드를 사용할 경우 메모리 부족 문제가 발생할 수 있기 때문에 **절대 사용하지 말아야 한다.**

참고로 같은 문자열에 대해 중복하여 intern을 호출하더라도 중복되어 주입되지는 않는다.

```
String internedStr = new String("Hello").intern(); // String Constant Pool로 강제 주입
newString("Hello").intern(); // 이미 주입된 같은 문자열이 존재하므로 주입하지 않음
```

## 스택 영역
스택 영역에는 프레임이 저장된다. 프레임은 자바의 코드 단위라고도 할 수 있는데, 중괄호로 감싸여진 부분을 프레임이라고 한다. 예를 들어 다음 코드에는 두 개의 프레임이 존재한다.

```
public void Frame() { // 첫 번째 프레임
    int a = 0;
        { // 두 번째 프레임
        	int b = 1;
        }
    System.out.println(b); // 예외 발생!
}
```

프레임 내부에는 로컬 변수 스택이 있다. 이 스택 안에서 기본 타입의 변수와 참조 타입의 변수가 생성되고 제거된다. 참고로 참조 타입 변수는 값 자체를 저장하지 않고 값이 보관된 위치를 저장한다.
