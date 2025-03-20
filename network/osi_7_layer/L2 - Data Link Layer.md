##### 본 글은 유튜브 채널 'AWS 강의실' 영상 자료를 참고하여 작성하였습니다.

## Data Link Layer
물리적인 통신을 제어함으로써 디바이스 간의 통신 및 전송을 안정화 하기 위한 계층이다. 
Data Link Layer에서 사용되는 용어인 MAC Address, CSMA/CD, Frame, Switch를 익혀보자.

### MAC Address
MAC Address는 네트워크를 공부하지 않았다면 사실 쉽게 알 수 있는 용어는 아니다. 
그래서 처음 배울 때 IP Address와 헷갈렸던 기억이 난다. 
MAC Address는 주민등록번호와 같다. 
각각의 장치에 고유하게 부여된 번호이며 **변하지 않는다**. 
반면 IP Address는 집주소와 같다. 
IP Address는 고유하지 않으며 변동될 수 있다. 
IP Address는 다음 계층인 Network Layer에서 사용되므로 여기서는 MAC Address에 대해 알아보자.

MAC Address는 총 6바이트(48비트)로 이루어져 있다. 
그 중 첫 세 바이트는 OUI(Organizationally Unique Identifier)이며 장치를 만든 제조사에 부여된 고유 식별자이다. 
즉, 서로 다른 장치라도 같은 제조사에서 만든 장치라면 MAC Address의 첫 세 바이트는 같다. 
나머지 세 바이트는 NIC(Network Interface Controller)이며 제조사가 같더라도 장치가 다르면 다른 값을 가진다.
예를 들어 00:1A:2B:3C:4D:5E라는 MAC 주소를 가진 장치라면 00:1A:2B는 OUI를 뜻하고 3C:4D:5E는 NIC를 뜻한다.

### CSMA/CD
공유 링크(회선)에 여러 장치가 데이터를 동시에 보내면 충돌이 발생하게 된다. 
이를 방지하기 위해 장치 간에 약속을 하는데 그런 약속 중 Random Access 방식에 속하는 프로토콜 중 하나가 CSMA/CD이다.
Random Access를 사용하는 다른 프로토콜로는 CSMA/CA, ALOHA 등이 있다.
참고로 CSMA/CD는 유선 방식, CSMA/CA는 무선 방식의 통신에서 사용되는 Random Access 프로토콜이다.

CSMA/CD에서 CSMA는 Carrier Sense Multiple Access의 준말이고 CD는 with Collision Detection의 준말이다. 
풀어서 말하면 '다중 접속 환경에서 발생하는 문제를 반송파(Carrier Sense)를 사용하여 해결하며 충돌을 판단할 수 있는 프로토콜'이다. 
여기서 반송파란 각 장치가 데이터를 보내기 전에 회선이 이미 점유 중인지 확인하는 신호이다. 
장치는 다른 장치들에게 이 신호를 보내고 회선이 점유 중이 아니라면 다른 장치들은 데이터를 전송해도 좋다는 반송파를 보냄으로써 충돌을 피할 수 있다.

충돌을 피할 수 있는데 왜 CD(Collision Detection)라는 이름이 붙은걸까? 
반송파를 사용하더라도 충돌은 발생할 수 있기 때문이다. 
회선이 점유되지 않은 특정 시기에 여러 장치가 동시에 점유 확인 신호를 보내는 경우 충돌이 발생할 수 있다.

장치는 본인이 보낸 데이터가 목적지에 도착하기 전에 다른 장치의 bit가 감지되면 충돌이 일어난 것으로 판단한다. 
(만약 충돌이 없다면 장치는 본인이 보낸 신호만 받는다) 
충돌이 발생했다면 각 장치는 랜덤 시간을 기다리고(Random Access) 다시 데이터를 전송한다. 
만약 이 경우에도 충돌이 발생한다면 좀 더 오래 기다린 후 재전송을 시도한다.

데이터의 충돌이 일어났을 때 이러한 프로토콜을 도입하여 문제를 해결할 수 있다. 
하지만 여기엔 또 다른 문제가 존재한다. 
이 문제는 조금 있다가 Switch 목차에서 자세히 알아보자.

### 주요 단위 - Frame
Data Link Layer의 주요 단위는 Frame이다. Frame은 아래와 같은 구조를 가진다.
![](https://velog.velcdn.com/images/likerhythm/post/35f176f2-92fe-4eb9-9e17-3e7b41a5ddc1/image.png)

여기서는 Frame이 대상 MAC(목적지 MAC)과 소스 MAC(출발지 MAC) 주소를 가지고 있다는 것만 알고 넘어가도록 하자.

### Switch
Switch는 Data Link Layer를 이해하고 있는 대표적인 장치이다. 
여기서 '이해하고 있는'의 뜻은 CSMA/CD와 같은 프로토콜을 수행할 수 있다는 의미이다.
![](https://velog.velcdn.com/images/likerhythm/post/c9b61afc-77fb-4bf0-a5ac-faa95196a8cb/image.png)

Switch의 내부를 좀 더 자세히 알아보자. 
아래 사진을 보면 Switch 내부에는 Frame Storage, 그리고 MAC 주소와 PORT를 매핑한 테이블이 존재한다.
MAC-PORT 테이블을 사용하면 Frame이 가지고 있던 목적지 MAC 주소를 통해 데이터(Frame)가 이동해야 할 PORT를 정할 수 있다. 
그렇다면 Frame Storage는 왜 사용하는 걸까?
![](https://velog.velcdn.com/images/likerhythm/post/aac5bf5c-a2b9-407e-90aa-622385090b7a/image.png)

앞서 CSMA/CD 목차의 마지막 문단에서 '또 다른 문제'가 있다고 했는데 이를 해결하기 위해 Switch가 도입됐으며 해결 방법으로 Frame Storage를 사용한 것이다.

문제 상황을 간단하게 설명하면, N개의 장치들이 Switch와 같은 base station 없이 공유 회선을 통해 연결되어 있다고 생각해보자. 
이런 경우 하나의 장치가 데이터를 보내기 위해선 다른 N-1개의 장치가 모두 데이터를 보내지 않는 순간을 기다려야 한다. 
즉, 장치들이 많아질수록 데이터를 보내기 힘들어지게 된다.

Switch는 일종의 버퍼 역할을 해줌으로써 이 문제를 해결해준다.
Switch에 연결된 각 장치들은 자신과 Switch 사이의 회선의 점유 유무만 확인하여 점유 상태가 아니라면 Switch에게 데이터를 보낸다. 
(Switch를 사용하지 않으면 공유 회선에 연결된 다른 모든 장치들이 보내는 데이터가 자신에게도 전달되기 때문에 충돌을 피하려고 하면 할수록 데이터 전송이 어려워진다) 
Switch는 받은 데이터(Frame)를 Frame Storage에 저장한다. Frame Storage가 버퍼 역할을 해주는 것이다.
그 후 Switch는 Frame의 목적지 MAC을 확인하여 Frame을 올바른 PORT로 전달한다. 
만약 Frame이 PORT를 출발하여 목적지로 이동하는 도중에 충돌이 발생한다면, Switch는 충돌이 발생한 Frame을 Frame Storage에 기억해 두었다가 랜덤 시간 이후에 재전송한다. 
충돌이 발생한 목적지 장치도 마찬가지로 랜덤 시간 이후에 재전송한다.

Switch는 Frame이 목적지로만 이동하도록 함으로써 장치들이 불필요한 충돌로 인해 데이터를 전송하지 못하는 상황을 피하게 해준다.

### Network Layer의 필요성
Data Link Layer는 각 장치들 사이에 전송되는 데이터를 제어할 수 있게 해준다.
하지만 서로 다른 네트워크 사이의 데이터 전송은 불가능하다. 
예를 들어 서로 다른 Switch에 연결된 장치는 MAC 주소를 알 수 없기 때문에 통신이 불가능하다. 
Network Layer는 대표적으로 IP 주소를 사용하여 이를 해결한다. 
다음 글에서 Network Layer에 대해 알아보자.