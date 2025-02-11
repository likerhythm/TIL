# Execution Engine
JVM의 Execution Engine은 Runtime Data Area에 올라온 목적파일(`.class` 파일, 바이트 코드로 이루어짐)을
운영체제가 이해할 수 있는 기계어 수준으로 변환하는 작업을 수행한다.
기계어로 번역하는 역할은 Interpreter와 JIT 컴파일러가 수행한다.

자바 애플리케이션이 실행되면 javac 컴파일러는 `.java`파일을 컴파일하여 `.class`파일을 생성한다.
클래스 로더는 `.class`파일을 Method Area에 로드한다. 이때 클래스의 정보를 담고 있는 Constant Pool과
클래스의 동작을 정의한 Byte Code가 저장된다. Execution Engine은 Byte Code의 명령어를 읽고 
애플리케이션이 정상적으로 실행되도록 한다.

JVM의 Execution Engine은 세 구성요소로 나뉜다.
1. Interpreter: Byte Code를 읽고 기계어로 번역한다.
2. JIT Compiler: 여러번 실행되는 코드를 캐시로 저장하여 실행 속도를 향상시킨다.
3. Garbage Collector: 사용하지 않는 메모리를 해제한다.

## Interpreter
Interpreter는 Byte Code를 기계어로 번역한다. 이 과정이 대표적으로 Java의 실행을 느리게 만드는 요인이다.
반복적으로 실행되는 코드를 매번 번역해야 하기 때문이다.
이를 좀 더 효율적으로 하기 위해 JIT Compiler를 도입했다.

## JIT Compiler
JIT Compiler도 Byte Code를 기계어로 번역하는 컴파일러이다. 하지만 매번 번역을 하진 않고
자주 반복되는 부분은 Native Code 형태로 캐시(Native Method Stack)에 저장해 두었다가 필요할 때
캐싱을 통해 사용할 수 있게 한다. 

JVM의 Execution Engine은 Byte Code를 Interpreter 방식으로 동작하다가
특정 **컴파일 임계치**를 넘으면 Byte Code 전체를 Native Code로 변환하여 캐시에 저장한다.
그 다음부터는 Interpreter는 Native Code를 그대로 가져가서 사용한다. 
Native Code를 사용하는 것이 Interpreter 방식보다 빠르고 한 번 번역된 Native Code는 캐시에 보관 되기 때문에
빠르게 수행된다.

### JIT Compiler는 자주 반복되는 부분을 어떻게 찾을까?
1. 처음 모든 코드는 Interpreter에서 번역된다.
2. 메서드의 호출 횟수를 기록한다.
3. 반복되는 코드 블럭을 '핫(hot)'이라고 한다.
4. 핫코드가 일정 횟수 이상 실행되면 JIT는 해당 코드를 기계어로 번역하여 캐시에 저장한다.

# 참조
https://wonit.tistory.com/591
https://velog.io/@impala/JAVA-JVM-Execution-Engine
https://devomni.tistory.com/entry/JIT-%EC%BB%B4%ED%8C%8C%EC%9D%BC%EB%9F%AC%EA%B0%80-%EB%B0%98%EB%B3%B5%EB%90%98%EB%8A%94-%EC%BD%94%EB%93%9C%EB%A5%BC-%EC%B2%98%EB%A6%AC%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95