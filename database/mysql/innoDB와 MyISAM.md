MySQL의 스토리지 엔진인 innoDB와 MyISAM은 무엇이 다를까요?
이번 글에서는 innoDB와 MyISAM을 비교해보았습니다.

# 스토리지 엔진
> 스토리지 엔진이 뭔가요?

스토리지 엔진은 디스크에 데이터를 어떻게 저장하고 접근할지에 대한 기능을 제공합니다.

> 그러면 DBMS와 스토리지 엔진은 뭐가 다른가요?

DBMS는 데이터베이스를 관리하는 전체적인 시스템으로 스토리지 엔진을 포함하여 보안, 동시성 제어, 질의 최적화 등의 기능을 제공합니다.

# MySQL의 스토리지 엔진
MySQL의 스토리지 엔진으로는 InnoDB와 MyISAM을 가장 많이 사용합니다. 
이 외에도 Archive, Cluster(NDB), Federated 등이 사용됩니다.

## InnoDB와 MyISAM
InnoDB는 MySQL 5.5 버전부터 기본으로 설정된 스토리지 엔진이고 MyISAM은 그 이전까지의 기본 스토리지 엔진입니다.

### 트랜잭션
InnoDB와 MyISAM의 가장 큰 차이점은 트랜잭션의 지원 유무입니다.

MyISAM은 트랜잭션 기능을 제공하지 않습니다. 그렇기 때문에 데이터 변경이 잦은 서비스에 MyISAM은 적합하지 않습니다.
하지만 MyISAM은 데이터를 굉장히 효율적으로 읽을 수 있도록 합니다. 
그렇기 때문에 READ 작업이 많은 서비스에서 효율적입니다.

예를 들어 한 사람이 쓰고 여러 사람이 읽는 웹사이트에 효율적이라고 할 수 있습니다.
트랜잭션을 지원하지 않기에 변경(쓰기) 작업을 수행하는 사용자는 최소화 되는 것이 좋습니다.

### 락
InnoDB는 row level의 락을 사용합니다. 그래서 성능적으로 우수하지만 데드락이 발생할 수 있습니다.
반대로 MyISAM은 테이블 전체에 락이 걸립니다.
그래서 MyISAM 엔진 사용시 데이터 변경 작업이 많아지면 성능이 굉장히 떨어집니다.
다만 MyISAM은 읽기 성능이 뛰어난 모습을 보입니다.

# 정리
## InnoDB 사용이 적합한 서비스
- 읽기, 쓰기 위주의 트랜잭션이 요구되는 table
- 민감한 정보를 갖는 table

## MyISAM 사용이 적합한 서비스
- 한 사람이 쓰고 여러 사람이 읽는 웹사이트
- data ware house
- 로그 table

이외에도 InnoDB는 외래키를 지원하는 반면 MyISAM은 지원하지 않는 등 차이점이 있습니다.
오라클은 두 엔진 중 InnoDB를 많이 밀고 있으며 MySQL 8.0 버전부터는 MyISAM의 흔적을 지우고 있기도 합니다.
하지만 InnoDB도 특정 상황에서는 아쉬운 성능을 나타낼 수 있기 때문에 InnoDB와 MyISAM의 장단점을 비교하여 상황에 맞는
스토리지 엔진을 사용하는 것이 중요할 것 같습니다.

# 참조
https://velog.io/@akfls221/InnoDB-vs-MyIsam-%EC%84%B8%EA%B8%B0%EB%8C%80%EA%B2%B0<br>
https://velog.io/@gillog/DBInnoDB-VS-MyISAM<br>
https://thefif19wlsvy.tistory.com/26<br>
