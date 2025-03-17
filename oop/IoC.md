# IoC(Inversion of Control)
**제어의 역전**이라는 의미인 IoC는 프로그램의 실행 흐름을 개발자가 아닌 
프레임워크 또는 다른 무언가가 관리하도록 하는 **디자인 패턴**을 의미합니다.

여기서는 단순하게 프레임워크로 제한하겠습니다.
이렇게만 들으면 다소 추상적으로 느껴집니다.
코드를 통해 좀 더 구체적으로 알아보겠습니다.

다음과 같이 `Service`와 `Repository` 인터페이스와 그 구현체가 있습니다.

```java
interface Service {
    
}

interface Repository {
    
}

class ServiceImpl implements Service {
    
    private final Repository repository = new RepositoryImpl();
}

class RepositoryImpl implements Repository {
    
}
```

`ServiceImpl`은 기능 구현을 위해 `Repository`를 참조하고 그 구현체로 `RepositoryImpl`을 사용하기로 **결정**했습니다.
여기서 '결정했다'라는 말은 개발자 프로그램의 실행 흐름을 개발자가 관리한다는 의미이기도 합니다.

하지만 `ServiceImpl`을 이런식으로 설계하면 구체 클래스인 `RepositoryImpl`에 대한 참조 때문에 SOLID 원칙 중
DIP 원칙을 위반하게 됩니다. 객체지향과는 거리가 먼 코드가 작성되는 것입니다.
DIP 원칙은 어떤 클래스를 참조해야 할 때 그 클래스를 바로 참조하는 것이 아니라 상위 요소(인터페이스, 추상클래스 등)를
참조해야 하는 원칙입니다.

DIP 원칙을 지키기 위해 아래와 같이 생성자를 통해 주입 받는 형태로 설계할 수 있습니다.

```java
class ServiceImpl implements Service {
    
    private final Repository repository;
    
    public ServicImpl(Repository repository) {
        this.repository = repository;
    }
}
```

이렇게 설계하면 개발자가 `ServiceImpl` 클래스 코드를 작성할 때 `Repository`가 정확히 어떤 역할을 하는지 알 수 없습니다.

Spring 프레임워크가 Service와 Repository를 빈으로 관리한다면
Spring 프레임워크가 개발자 대신 Repository 객체를 외부에서 주입해줍니다.
즉 개발자가 아니라 프레임워크가 프로그램의 실행 흐름을 관리하게 됩니다.

이러한 특징을 IoC(Inversion of Control, 제어의 역전)이라고 합니다.

# IoC Container
IoC는 디자인 패턴입니다. 이를 구현한 것을 IoC Container라고 하며 Spring에서는 이를 BeanFactory라는 이름으로 구현하였고
Spring IoC라고도 부릅니다.
위 예제 코드에서 객체를 외부에서 주입 받는데, 실제로 그 객체를 생성하고 주입해주는 역할을 담당합니다.

# IoC가 필요한 이유
> 그래서 IoC를 왜 사용하는거지?

개인적으로 IoC를 사용하는 이유는 DI(Dependency Injection)의 필요성과 연관된다고 생각합니다.
객체지향 설계를 위해 DI를 도입하다 보니 외부에서 객체를 생성하고 주입하는 역할을 하는 장치가 필요해졌고,
그 장치를 도입한 패턴을 IoC라고 이름 붙였다고 생각합니다. 

이 부분은 개인적인 생각이니 참고만 하시면 좋을 것 같습니다.
더 명확한 이유가 있다면 언제든지 알려주세요.

ps. DI(Dependency Injection)는 IoC의 구현 방법 중 하나라고 합니다.
그래서 IoC의 필요성이 DI와 연관되는 것 같습니다.
IoC의 필요성은 결국 SOLID 원칙을 지키기 위해서이고
객체를 생성하고 관리하는 역할을 가진 장치를 따로 두는 것으로 SOLID 원칙을 위배하지 않게 할 수 있스니다.  

# 참조
https://velog.io/@slolee/IoC-DI-%EC%97%90-%EB%8C%80%ED%95%9C-%EC%98%A4%ED%95%B4
