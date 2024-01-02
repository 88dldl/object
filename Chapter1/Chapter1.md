# 1. 객체, 설계

## 01. 티켓 판매 애플리케이션 구현하기

- 티켓 이벤트에 당첨된 관람객 : 초대장을 티켓으로 교환한 후에 입장 가능
- 그냥 관람객 : 티켓 구매 후 입장 가능

  ⇒ 입장 전 이벤트 당첨 여부 판단
###

<br><br>


## 02. 무엇이 문제일까

**모듈**
: 크기와 상관없이 클래스나 패키지, 라이브러리와 같이 프로그램을 구성하는 임의의 요소

**소프트웨어 모듈의 세가지 목적**
1. 실행중에 제대로 동작하는 것
2. 목적은 변경을 위해 존재하는것 : 변경하기 어려운 모듈은 제대로 동작하더라도 개선해야한다
3. 코드를 읽는 사람과 소통하는 것 : 모듈은 특별한 훈련 없이도 개발자가 쉽게 읽고 이해할 수 있어야 한다.

01의 코드는 1번 조건만을 만족시키고, 다른 두 조건은 만족시키지 못한다.

<br>

### 예상을 빗나가는 코드

Theater클래스의 enter메서드가 수행하는 일은 다음과 같다.
```java 
public void enter(Audience audience){
        if(audience.getBag().hasInvitation()){ //초대장이 있는 경우
            Ticket ticket=ticketSeller.getTicketOffice().getTicket();
            audience.getBag().setTicket(ticket); //가방에 티켓 넣어줌
        }
        else{
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().minusAmount(ticket.getFee()); // 돈 지불
            ticketSeller.getTicketOffice().plusAmount(ticket.getFee()); // 셀러 입장에서는 돈을 벌었음
            audience.getBag().setTicket(ticket); // 티켓 넣어줌
        }
    }
```

**[문제 1]** :
**관람객**과 **판매원**이 소극장의 통제를 받는 **수동적인 존재**이다.

1. 관람객 입장에서는 소극장이라는 제 3자가 초대장을 확인하기 위해 관람객의 가방을 마음대로 열어본다.

2. 판매원 입장에서는 소극장이 허락없이 매표소에 보관중인 티켓과 현금에 마음대로 접근한다.

3. 티켓을 꺼내 관람객에 가방에 집어넣고 관람객에서 받은 돈을 매표소에소에 적립하는 일을 소극장에서 수행한다.

⇒ 현실에서는 관람객이 직접 돈을 꺼내 판매원에게 지불하고,<br>
판매원은 매표소에 있는 티켓을 직접꺼내 관람객에게 건내고,<br>
관람객에게서 직접 돈을 받아 매표소에 보관한다.
<br>
<br>

**[문제2]** : 코드를 이해하기 위해서는 여러가지 세부적인 내용들을 한번에 기억하고 있어야한다 .

<br>

### 변경에 취약한 코드

관람객이 가방을 들고다니지 않는다면?<br>
판매원이 매표소 이외의 곳에서 티켓을 판매한다면?

⇒ Theater도 변경해야한다.<br>
⇒ **의존성**이 높은 관계이다

> 최소한의 의존성만 유지하고 불필요한 의존성을 제거하자!

<br><br>


## 03. 설계 개선하기

**변경**과 **의사소통**이라는 문제가 엮여있다. Theater가 관람객의 가방과 판매원의 매표소에 직접 접근하기 때문이다.

=> Audience와 TicketSeller가 변경될 때, Theater도 변경되어야한다.(의존성이 높다.)
<br>

> **해결방법** : 역할을 나누어 자율성을 높이자

<br>

### 자율성을 높이자

1. Teater의 enter메서드에서 TicketOffice에 접근하는 모든 코드를 TicketSeller 내부로 숨기자
    ```java
    public class Theater {
    private TicketSeller ticketSeller;
    
        public Theater(TicketSeller ticketSeller){
            this.ticketSeller=ticketSeller;
        }
        public void enter(Audience audience){
            ticketSeller.sellTo(audience);
        }
    }
    ```
    
    ```java
    public class TicketSeller {
        private TicketOffice ticketOffice;
        public TicketSeller(TicketOffice ticketOffice){
            this.ticketOffice=ticketOffice;
        }
    
        public void sellTo(Audience audience){
            if(audience.getBag().hasInvitation()){
                Ticket ticket = ticketOffice.getTicket();
                audience.getBag().setTicket(ticket);
            }else{
                Ticket ticket=ticketOffice.getTicket();;
                audience.getBag().minusAmount(ticket.getFee());
                ticketOffice.plusAmount(ticket.getFee());
                audience.getBag().setTicket(ticket);
            }
        }
    }
    ```

**[캡슐화]**

: 객체 내부의 세부사항을 감추는 것

<br>


TicketSeller에서 getTicketOffice메서드가 제거 되었다. (의존성이 제거되었다.) 

ticketOffice에 대한 접근은 오직 TicketSeller안에서만 존재하게 된다.

결론적으로 Theater은 오직 TicketSeller의 인터페이스에만 의존하게 된다.<br> ( TicketSeller가 내부에 TicketOffice 인터페이스를 포함하고 있는 사실은 구현의 영역이다)

> 인터페이스만을 공개하는 것은 객체사이의 결합도를 낮추고 변경하기 쉬운 코드를 작성하기 위해 따라야하는 기본적인 설계원칙이다.

<br>
 
1. TicketSeller는 Audience의 getBag메서드를 호출해 내부의 Bag인스턴스에 직접 접근한다.

   : 위와 동일한 방법으로 Bag에 접근하는 모든 로직을 Audience내부로 감춘다.

    ```java
    public class Audience {
        private Bag bag;
        public Audience(Bag bag){
            this.bag=bag;
        }
    
        public Long buy(Ticket ticket){
            if(bag.hasInvitation()){
                bag.setTicket(ticket);
                return 0L;
            }else{
                bag.setTicket(ticket);
                bag.minusAmount(ticket.getFee());
                bag.setTicket(ticket);
                return ticket.getFee();
            }
        }
    }
    ```

    ```java
    public class TicketSeller {
        private TicketOffice ticketOffice;
        public TicketSeller(TicketOffice ticketOffice){
            this.ticketOffice=ticketOffice;
        }
    
        public void sellTo(Audience audience){
            ticketOffice.plusAmount(audience.buy(ticketOffice.getTicket()));
        }
    }
    ```


Audience 는 스스로 자신의 가방안에 초대장이 있는지 확인하고 외부에서의 가방에 대한 접근을 허용하지 않는다.

외부에서 Audience가 가방을 소유하고 있는지에 대한 사실은 알필요가 없기 때문에 getBag메서드를 지울 수 있다.

최종적으로 TicketSeller 와 Audience 사이의 결합도가 낮아졌다.

<br>

### 캡슐화와 응집도

핵심은 객체 내부의 상태를 캡슐화 하고 객체간에 오직 메시지를 통해서만 상호작용하도록 만들었다.

>밀접하게 연관된 작업만을 수행하고 연관성 없는 작업은 다른 객체에게 위임하는 객체를 가리켜 **응집도**가 높다고 한다.

 자신의 데이터를 스스로 처리하는 자율적인 객체를 만들면 결합도를 낮추고 응집도를 높일수있다.

<br>

### 절차지향과 객체지향

수정전 코드 : 절차지향적 프로그래밍

: 이처럼 프로세스와 데이터를 별도의 모듈에 위치시키는 방식을 절차지향적 프로그래밍이라고 한다.

: 변경하기 쉬운 설계는 한번에 하나의 클래스만 변경할 수 있는 설계다. 절차적 프로그래밍은 프로세스가 필요한 모든 데이터에 의존해야한다는 문제점 때문에 변경에 취약하다.

###
수정 후 코드 : 객체지향 프로그래밍

: 데이터와 프로세스가 동일한 모듈 내부에 위치하도록하는 프로그래밍 방식이다.

<br>

### 더 개선될수있다

1. bag과 Audience

    ```java
    public class Audience {
        private Bag bag;
        public Audience(Bag bag){
            this.bag=bag;
        }
    
        public Long buy(Ticket ticket){
            return bag.hold(ticket);
        }
    }
   ```

    ```java
    public class Bag {
        private Long amount;
        private Invitation invitation;
        private Ticket ticket;
    
        public Long hold(Ticket ticket){
            if(hasInvitation()){
                setTicket(ticket);
                return 0L;
            }else{
                setTicket(ticket);
                minusAmount(ticket.getFee());
                setTicket(ticket);
                return ticket.getFee();
            }
        }
        public Bag(long amount){
            this(null,amount);
        }
        public Bag(Invitation invitation,long amount){
            this.invitation=invitation;
            this.amount=amount;
        }
        public boolean hasTicket(){
            return ticket!=null;
        }
        private void setTicket(Ticket ticket){
            this.ticket=ticket;
        }
        private void minusAmount(Long amount){
            this.amount-=amount;
        }
        public void plusAmount(Long amount){
            this.amount+=amount;
        }
        private boolean hasInvitation(){
            return invitation!=null;
        }
    }
    ```
<br>

2. ticketSeller와 ticketOffice
    ```java
    public class TicketOffice {
        private Long amount;
        private List<Ticket> tickets=new ArrayList<>();
    
        public TicketOffice(Long amount,Ticket ... tickets){
            this.amount=amount;
            this.tickets.addAll(Arrays.asList(tickets));
        }
        private Ticket getTicket(){
            return tickets.remove(0);
        }
    
        private void plusAmount(Long amount){
            this.amount+=amount;
        }
        public void sellTicketTo(Audience audience){
            plusAmount(audience.buy(getTicket()));
    
        }
    }
    ```
    
    ```java
    public class TicketSeller {
        private TicketOffice ticketOffice;
        public TicketSeller(TicketOffice ticketOffice){
            this.ticketOffice=ticketOffice;
        }
    
        public void sellTo(Audience audience){
            ticketOffice.sellTicketTo(audience);
        }
    
    }
    ```

ticketoffice랑 audience간의 의존성이 추가되었다. <br>->
ticketoffice의 자율성은 높였지만 전체 설계의 관점에서는 결합도가 상승했다.

=> 트레이드 오프 필요

> 1. 어떤 기능을 설계하는 방법은 한가지 이상일수있다.<br> 
> 2. 동일한 기능을 한가지 이상의 방법으로 설계할수있기 때문에 결국 설계는 트레이드오프의 산물이다.
     
<br>
 
### 의인화

실생활에서 Theather나 bag은 자율적인 존재가 아니다. 극장에 관람객이 입장하기 위해서는 누군가 극장의 문을 열고 입장을 허가해줘야한다. 또한 가방을 여는것은 관람객이지 가방이 아니다.

비록 현실에서는 수동적인 존재라고 하더라도 일단 객체지향의 세계에 들어오면 모든 것이 능동적이고 자율적인 존재가 된다

> 레베가 워프스브록은 이처럼 능동적이고 자율적인 존재로 소프트웨어 객체를 설계하는 원칙을 가리켜 **의인화**라고 부른다.