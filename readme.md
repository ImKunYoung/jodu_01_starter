### 01 인텔리제이로 스프링 부트 시작하기

-----

#### 1.4 그레이들 프로젝트를 스프링 부트 프로젝트로 변경하기

<br/>

- 프로젝트의 플러그인 의존성 관리를 위한 설정

```
buildscript {
    ext {
        springBootVersion = '2.1.7.RELEASE'
    }
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

plugins {
    id 'java'
}

group 'org.example'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}

test {
    useJUnitPlatform()
}
```

| 키워드 | 내용                           |
|:----|:-----------------------------|
| ext | build.gradle 에서 사용하는 전역변수 설정 |

<br/>

- 앞서 선언한 프러그인 의존성 적용

```
apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'
```

| 키워드 | 내용                      |
|:----|:------------------------|
| io.spring.dependency-management | 스프링 부트의 의존성을 관리해주는 플러그인 |


- 각종 의존성 (라이브러리) 들 추가를 위한 리포지터리 (원격저장소) 추가

```
repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation('org.springframework.boot:spring-boot-starter-web')
    testImplementation('org.springframework.boot:spring-boot-starter-test')
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}
```

> 최근엔 업로드 난이도 때문에 jcenter 도 많이 이용함 (직접 만든 라이브러리 업로드할 때 복잡함) <br/>
> dependencies 선언부에서 버전을 명시하지 않아야만  ``org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}`` 따라감

| 키워드 | 내용                      |
|:----|:------------------------|
| dependencies | 프로젝트 개발에 필요한 의존성 선언하는 곳 |

----
### 02 스프링 부트에서 테스트 코드를 작성하자

- 단위 테스트

| 키워드   | 내용                                                                                                                                                                                     |
|:------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 단위테스트 | 기능 단위의 테스트 코드를 작성하는 것                                                                                                                                                                  |
| 장점    | - 개발단계 초기에 문제를 발견하게 도와준다. <br/> - 나중에 리팩토링이나 라이브러리 업그레이드 등에서 기존 기능이 올바르게 작동하는지 확인할 수 있다 (예, 회귀 테스트) <br/> - 기능에 대한 불확실성을 감소시킨다. <br/> - 시스템에 대한 실제 문서를 제공한다. (단위 테스트 자체를 문서로 사용할 수 있음) |

> 단위 테스트는 새로운 기능이 추가될 때, 기존 기능이 잘 작동되는 것을 보장해줌

<br/>

- 대표적 테스트 프레임워크

| 키워드     | 내용   |
|:--------|:-----|
| JUnit   | JAVA |
| DBUnit  | DB   |
| CppUnit | C++  |
| NUnit   | .net |

-----

#### 2.2 Hello Controller 테스트 코드 작성하기

- Application (메인)클래스 추가
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

| 키워드     | 내용                                                                                                   |
|:--------|:-----------------------------------------------------------------------------------------------------|
| @SpringBootApplication   | - 스프링 부트 자동 설정, 스프링 Bean 읽기와 생성을 모두 자동으로 설정함<br/> - 해당 어노테이션이 선언된 위치부터 설정을 읽어가기 때문에 프로젝트 최상단에서 선언할 것 |
|     SpringApplication.run | 내장 WAS 실행  (서버에 별도의 톰켓 설치 작업을 안해도 됨, 서버 실행 시 JAR Runnable File 만 클릭하면 실행할 수 있음)                      |

<br/>

- 간단한 컨트롤러 구현

```java
package org.example.jodu_01_Starter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

}
```

> /hello 로 요청이 오면 문자열 hello 를 반환함

| 키워드             | 내용                                                                                                                                               |
|:----------------|:-------------------------------------------------------------------------------------------------------------------------------------------------|
| @RestController | - 컨트롤러를 JSON 을 반환하는 컨트롤러로 만들어줌 <br/> - 예전에 @ResponseBody 를 각 메소드마다 선언했던 것을 한번에 사용할 수 있게 해줌                                                       |
| @GetMapping     | - HTTP Method 인 Get 의 요청을 받을 수 있는 API 를 만들어줌 <br/> - 예전에 @RequestMapping(method = RequestMethod.GET) 으로 사용되던 거                                   |


<br/>

- 테스트 코드 구현

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HelloController.class)
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void hello() throws Exception {

        String hello = "hello";

        mvc.perform(MockMvcRequestBuilders.get("/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(hello));
    }

}
```

|키워드| 내용                                                                                                                                                        |
|:---|:----------------------------------------------------------------------------------------------------------------------------------------------------------|
|@RunWith(SpringRunner.class)| - 테스트를 진행할 때 JUnit에 내장된 실행자 외에 파라메타에 선언한 실행자를 실행 <br/> - SpringRunner 는 스프링 실행자를 사용 <br/> - 스프링부트 테스트와 JUnit 사이에 연결자 역할                                   |
| @WebMvcTest  | - 여러 스프링 테스트 어노테이션 중, Web (Spring MVC)에 집중할 수 있는 어노테이션 <br/> - 선언할 경우 @Controller, @ControllerAdvice 등 사용 가능 (@Service, @Component, @Repository 등은 사용 불가) |
| @Autowired  | - 스프링이 관리하는 빈 (Bean)을 주입 받음                                                                                                                               |
| private MockMvc mvc  | - 웹 API 를 테스트할 때 사용 <br/> - 스프링 MVC 테스트의 시작점 <br/> - 이 클래스를 통해 HTTP GET, POST 등에 대한 API 테스트를 할 수 있다.                                                      |
|  mvc.perform(MockMvcRequestBuilders.get("/hello")) | - MockMvc 를 통해 /hello 주소로 HTTP GET 요청을 함 <br/> - 체이닝이 지원되어 여러 검증 기능을 이어서 선언 가능                                                                            |
| .andExpect(MockMvcResultMatchers.status().isOk())  | - mvc.perform 의 결과를 검증 <br/> - HTTP Header 의 Status 를 검증 <br/> - 200, 404, 500 등의 상태 검증 <br/> - 여기선 OK (200) 인지 아닌지를 검중                                   |
| .andExpect(MockMvcResultMatchers.content().string(hello))  | - mvc.perform 의 결과를 검증 <br/> - 응답 본문의 내용을 검증 <br/> - Controller 에서 "hello" 를 리턴하는 지 검증                                                                    |

<br/>
---

#### 2.3 롬복 소개

> 롬복이란? 개발 시 자주 사용하는 Getter, Setter, 기본생성자, toString 등을 어노테이션으로 자동 생성해줌

     implementation 'org.projectlombok:lombok:'

프로젝트마다 build.gradle 라이브러리 추가, Enable annotation processing 체크하기

<br/>

---

#### 2.4 Hello Controller 코드를 롬복으로 전환하기

- HelloResponseDto 추가

```java
package org.example.jodu_01_Starter.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HelloResponseDto {

    private final String name;

    private final int amount;
    
}
```

|keyword| description                                                          |
|:---|:---------------------------------------------------------------------|
|@Getter| - 선언된 모든 필드의 get 메소드를 생성해줌                                           |
|@RequiredArgsConstructor| - 선언된 모든 final 필드가 포함된 생성자를 생성해줌 <br/> - final 이 없는 필드는 생성자에 포함되지 않음 |

<br/>

- HelloResponseDto 테스트 코드 작성

```java
package org.example.jodu_01_Starter.dto;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HelloResponseDtoTest {

    @Test
    public void lombokFunctionTest() {

        /*given*/
        String name = "test";

        int amount = 1000;


        /*when*/
        HelloResponseDto dto = new HelloResponseDto(name, amount);


        /*then*/
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);

    }
}
```
|keyword| description                                                                                                                 |
|:---|:----------------------------------------------------------------------------------------------------------------------------|
|assertThat| - assertj 라는 테스트 검증 라이이브러리의 검증 메소드임. <br/> - 검증하고 싶은 대상을 매서드 인자로 받음 <br/> - 메소드 체이닝이 지원되어 isEqualTo 와 같이 메소드를 이어서 사용할 수 있다. |
|isEqualTo| - assertj 의 동등 비교 메소드임. <br/> - assertThat 에 있는 값과 isEqaulTo 의 값을 비교해서 같을 때만 성공!                                            |

> JUnit 과 비교했을 때 assertj 의 장점
> - CoreMatchers 와 달리 추가적으로 라이브러리가 필요하지 않다.
>   - Junit 의 assertThat 을 쓰게 되면 is() 와 같이 CoreMatchers 라이브러리가 필요하다.
> - 자동완성이 좀 더 확실하게 지원된다.
>   - IDE 에서는 CoreMatchers 와 같은 Mather 라이브러리의 자동완성 지원이 약하다.

- 실행결과

![](readmeImage/img.png)

<br/>

- HelloController 에서 ResponseDTO 사용

```java
    @GetMapping("/hello/dto")
    public HelloResponseDto HelloDto(@RequestParam("name") String name, @RequestParam("amount") int amount) {
        return new HelloResponseDto(name, amount);
    }
```
|keyword| description                                                                                                                            |
|:---|:---------------------------------------------------------------------------------------------------------------------------------------|
|@ReqeustParam| - 외부에서 API 로 넘긴 파라미터를 가져오는 어노테이션임 <br/> - 여기서는 외부에서 name (@RequestParam("name")) 이란 이름으로 넘긴 파라미터를 메소드 파라미터 name(String name) 에 저장하게 됨. |

<br/>

- HelloController API 테스트 

```java
package org.example.jodu_01_Starter.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HelloController.class)
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void hello() throws Exception {

        String hello = "hello";

        mvc.perform(MockMvcRequestBuilders.get("/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(hello));
    }

    @Test
    public void HelloDto_return() throws Exception {

        String name = "hello";

        int amount = 1000;

        mvc.perform(MockMvcRequestBuilders.get("/hello/dto").param("name", name).param("amount", String.valueOf(amount)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.amount", is(amount)));


    }

}
```

| keyword  | description                                                                                                             |
|:---------|:------------------------------------------------------------------------------------------------------------------------|
| param    | - API 테스트할 때 사용될 요청 파라미터를 설정한다. <br/> - 값은 String 만 허용됨 <br/> - 따라서 숫자/날짜 등의 데이터를 등록할 때는 문자열로 변경해야 함                    |
| jsonPath | - JSON 응답값을 필드별로 검증할 수 있는 메서드이다. <br/> - $ 을 기준으로 필드명을 명시한다. <br/> - 여기서는 bane 과 amount 를 검증하니 $.name, $.amount 로 검증한다. |

- api 테스트 결과

![](readmeImage/img_1.png)


----
### 03 스프링부트에서 JPA로 데이터베이스 다뤄보자

> - MyBatis, iBatis: SQL 매퍼
> - JPA : ORM (Object Relational Mapping)

#### JPA 소개

|키워드| 내용                                                                          |
|:---|:----------------------------------------------------------------------------|
|RDB| 어떻게 데이터를 저장할지에 초점                                                           |
|객체지향 프로그래밍 언어| 메시지를 기반으로 기능과 속성을 한 곳에서 관리하는 기술 -> 패러다임 불일치                                 |
|JPA| 객체지향적으로 프로그래밍하고 SQL을 대신 생성 -> SQL 독립적으로 개발 가능                               |
|Spring Data JPA| JPA <- Hibernate <- Spring Data JPA  <br/> - 구현 교체의 용이성 <br/> - 저장소 교체의 용이성 |

> - 구현교체의 용이성: 유행이 지나면 Hibernate 외에 다른 구현체로 쉽게 교체하기 위함 <br/>
> - 저장소 교체의 용이성: 관계형 데이터베이스 외에 다른 저장소로 쉽게 교체하기 위함 <br/>
> -> 서비스 초기엔 RDB로 모든 기능 처리 가능하지만 트래픽 커질 수록 RDB로 감당 안됨 (MongoDB로 교체 필요성 spring data mongodb로 의존성 교체)
> - save(), findAll().. 등의 인터페이스를 사용하는 경우 저장소가 교체되어도 기본적인 기능은 변경할 것이 없다.

---

#### 요구사항 분석

| 게시판 기능 | 회원 기능            |
|:-------|:-----------------|
| 게시글 조회 | 구글/네이버 로그인       |
| 게시글 등록 | 로그인한 사용자 글 작성 권한 |
| 게시글 수정 | 본인 작성 글에 대한 권한 관리 |
| 게시글 삭제 |                  |

<br/>

![](readmeImage/img_2.png)

![](readmeImage/img_3.png)


------

#### 3.2 프로젝트에 Spring Data Jpa 적용하기

- spring-boot-starter-data-jpa, com.h2database:h2 의존성 등록
```
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'com.h2database:h2'
```

|키워드| 내용                                                                                                                     |
|:---|:-----------------------------------------------------------------------------------------------------------------------|
|spring-boot-starter-data-jpa| - 스프링 부트용 Spring Data Jpa 추상화 라이브러리 <br/> - 스프링 부트 버전에 맞춰 자동으로 JPA 관련 라이브러리들의 버전을 관리해줌                                 |
|h2| - 인메모리 RDB <br/> - 별도의 설치가 필요 없이 프로젝트 의존성만으로 관리 가능 <br/> - 메모리에서 실행되기 때문에 애플리케이션 재시작할 때마다 초기화된다는 점을 이용하여 테스트 용도로 주로 사용 |

<br/>

- Domain (Entity) 클래스 작성

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

}
```

| 키워드                | 내용                                                                                                                                                                            |
|:-------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Entity             | - 데이블과 링크될 클래스임을 나타냄 <br/> - 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍 (_) 으로 데이블 이름을 매칭 <br/> - ex) SalesManager.java -> sales_manager table                                            |
| @Id                | - 해당 테이블의 PK 필드를 나타냄                                                                                                                                                          |
| @GeneratedValue    | - PK의 생성 규칙을 나타냄 <br/> - 스프링부트 2.0 에서는 GenerationType.IDENTITY 옵션을 추가해야만 auto_increment 가 됨                                                                                   |
| @Column            | - 테이블의 칼럼을 나타내며 굳이 선언하지 않더라도 해당 클래스의 필드는 모두 칼럼이 됨 <br/> - 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용 <br/> - 문자열의 경우 VARCHAR(255) 가 기본값인데, 사이즈를 500으로 늘리고 싶거나 타입을 TEXT로 변경하고 싶거나 등의 경우에 사용 |
| @NoArgsConstructor | - 기본 생성자 자동 추가 <br/> - Constructor 와 같은 효과                                                                                                                                    |
| @Getter            | - 클래스 내 모든 필드의 Getter 메소드를 자동 생성                                                                                                                                              |
|@Builder| - 해당 클래스의 빌더 패턴 클래스를 생성 <br/> - 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함                                                                                                              |

> Entity 클래스에서 절대 Setter 메소드를 만들지 마라. 대신, 해당 필드의 값 변경 필요시 명확히 그 목적과 의도를 나타낼 수 있는 메소드를 추가해라

<br/>

- 잘못된 사용 예

```java
public class Order {
    public void setStatus (boolean status) {
        this.status = status;
    }
    
    public void 주문서비스의_취소이벤트() {
        order.setStatus(false);
    }
}
```


- 올바른 사용 에
```java
public class Order {
    public void cancelOrder () {
        this.status = false;
    }
    
    public void 주문서비스의_취소이벤트() {
        order.cancelOrder();
    }
}
```

<br/>

- JpaRepository 생성

|키워드| 내용                     |
|:---|:-----------------------|
|JpaRepository| Posts 클래스로 DB에 접근하게 해줌 |

> JPA 에선 Repository 라고 부르며 인터페이스로 생성됨. 반면 ibatis 나 MyBatis 등에서 DAO 라고 불림 <br/>
> JpaRepository<Entity, PK Type> 을 상속하면 기본적인 CRUD 메소드가 자동으로 생성됨 <br/>
> 나중에 프로젝트 규모가 커져 도메인별로 프로젝트를 분리해야 한다면 Entity 클래스와 기본 Repository 는 함께 움직여야 하므로 도메인 패키지에서 함께 관리할 것

---

#### 3.3 Spring Data JPA 테스트 코드 작성하기

```java
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {

        // given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("jojodu@gmail.com")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);

    }

}
```

| 키워드    | 내용                                                                                                                                                                      |
|:-------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @After | - Junit 에서 단위 테스트가 끝날 때마다 수행되는 메소드를 지정 <br/> - 보통은 배포 전 전체 테스트를 수행할 때 테스트간 데이터 침범을 막기 위해 사용 <br/> - 여러 테스트가 동시에 수행되면 DB 인 H2 에 데이터가 그대로 남아 있어 다음 테스트 실행 시 테스트가 실패할 수 있다 |
| postsRepository.save       | - 테이블 posts 에 insert/update 쿼리를 실행한다 <br/> - id 값이 있다면 update 가 없다면 insert 쿼리가 실행된다                                                                                     |
|  postsRepository.findAll      | - 테이블 posts 에 잇는 모든 데이터를 조회해오는 메소드이다                                                                                                                                    |

> - 실행된 쿼리 로그 확인: ```spring.jpa.show-sql=true```

> - 출력되는 쿼리 로그를 MySQL 버전으로 변경 : ```spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect```

---

#### 3.4 등록/수정/조회 API 만들기

- API 만들기 위해 총 3개의 클래스 필요
> - Request 데이터를 받을 DTO
> - API 요청을 받을 Controller
> - 트랜잭션, 도메인 기능 간의 순서를 보장하는 Service

![](readmeImage/img_4.png)

> - Web Layer
>     - 흔히 사용하는 컨트롤러 (@Controller)와 JPS/Freemaker 등의 뷰 템플릿 영역임.
>     - 이외에도 필터 (@Filter), 인터셉터, 컨트롤러 어드바이스 (@Controller/Advice) 등 외부 요청과 응답에 대한 전반적인 영역을 이야기함
> - Service Layer
>   - @Service 에 사용되는 서비스 영역이다.
>   - 일반적으로 Controller 와 DAO 의 중간 영역에서 사용됨.
>   - @Transactional 이 사용되어야 하는 영역이기도 함
> - Repository Layer
>   - Database 와 같이 데이터 저장소에 접근하는 영역임
>   - 기존에 DAO (Data Access Object) 영역으로 이해하면 됨
> - Dtos
>   - Dto (Data Transfer Object)는 계층 간에 데이터 교환을 위한 객체를 이야기하며 Dtos 는 이들의 영역을 애기함
>   - 예를 들어 뷰 템플릿 엔진에서 사용될 객체나 Repository Layer 에서 결과로 넘겨준 객체 등이 이들을 말함.
> - Domain Model
>   - 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것을 도메인 모델이라고 한다
>   - 예를 들어 택시 앱의 경우 배차, 탑승, 요금 등이 모두 도메인이 될 수 있다
>   - @Entity 가 사용된 영역 역시 도메인 모델이라고 이해하면 됨
>   - 다만, 무조건 데이터베이스의 테이블과 관계가 있어야 하는 것은 아니다
>   - VO 처럼 값 객체들도 이 영역에 해당됨

<br/>

- 도메인 계층에서 로직을 처리하는 이유 <br/> 
 -ex) 주문 취소 로직

- 슈도 코드
```java
@Transactional
public Order cancelOrder(int orderId){
        // 데이터베이스로부터 주문정보, 결제정보, 배송정보 조회
        // 배송 취소 해야 하는지 확인
        // 배송중이라면 취소로 변경
        // 각 테이블에 취소 상태 Update
}
```

- 서비스 계층에서 모든 로직을 처리할 경우
```java
@Transactional
public Order cancelOrder(int orderId){
    
    OrderDto order = ordersDao.selectOrders(orderId);
    BillingDto billing = billingDao.selectBilling(orderId);
    DeliveryDto delivery = deliveryDao.selectDelivery(orderId);
        
    String deliveryStatus = delivery.getStatus();
    
    if("IN_PROGRESS".equals(deliveryStatus)) {
        delivery.setStatus("CANCEL");
        deliveryDao.update(delivery);
    }
    
    order.setStatus("CANCEL");
    ordersDao.update(order);
    
    billing.setStatus("CANCEL");
    deliveryDao.update(billing);
    
    return order;
}
```

> 모든 로직이 서비스 클래스 내부에서 처리됨. 그러다 보니 서비스 계층이 무의미하며, 객체란 단순 데이터 덩어리 역할만..

- 로직을 도메인 모델에서 처리할 경우

```java
@Transactional
public Order cancelOrder(int orderId) {
    
    Orders order = ordersRepository.findById(orderId);
    Billing billing = billingRepository.findByOrderId(orderId);
    Delivery delivery = deliveryRepository.findByOrderId(orderId);
    
    delivery cancel();
    
    order.cancel();
    billing.cancel();
    
    return order;
    
}
```

> order, billing, delivery 가 각자 본인의 취소 이벤트 처리를 하며, 서비스 메소드는 트랜잭션과 도메인 간의 순서만 보장해 준다.

<br/>

---

###### 등록, 수정, 삭제 기능 만들기

---

#### - 등록 기능
- PostApiController

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.service.posts.PostsService;
import org.example.jodu_01_Starter.web.dto.PostSaveRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostsService postsService;

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostSaveRequestDto requestDto) {

        return postsService.save(requestDto);

    }

}
```

- PostsService
```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.domain.posts.PostsRepository;
import org.example.jodu_01_Starter.web.dto.PostSaveRequestDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostSaveRequestDto requestDto) {

        return postsRepository.save(requestDto.toEntity()).getId();

    }

}
```

> @RequiredArgsConstructor: final 이 선언된 모든 필드를 인자값으로 하는 생성자를 생성해줌 (bean 으로 등록)

- PostsSaveRequestDto

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jodu_01_Starter.domain.posts.Posts;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSaveRequestDto {

    private String title;

    private String content;

    private String author;


    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
```

- PostsApiControllerTest

```java
import org.example.jodu_01_Starter.domain.posts.Posts;
import org.example.jodu_01_Starter.domain.posts.PostsRepository;
import org.example.jodu_01_Starter.web.dto.PostsSaveRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception {

        // given
        String title = "title";
        String content = "content";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

    }
}
```

> Api Controller 테스트를 하는 경우 @WebMvcTest 를 사용하지 않는데 @Web..의 경우 JPA 기능이 작동하지 않음, 외부 연동과 관련된 부분만 활성화됨

---

#### 수정 / 조회 기능

- PostApiController

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.service.posts.PostsService;
import org.example.jodu_01_Starter.web.dto.PostsResponseDto;
import org.example.jodu_01_Starter.web.dto.PostsSaveRequestDto;
import org.example.jodu_01_Starter.web.dto.PostsUpdateRequestDto;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostsService postsService;


    /*등록한다*/
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {

        return postsService.save(requestDto);

    }


    /*수정한다*/
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {

        return postsService.update(id, requestDto);

    }


    /*조회한다*/
    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id) {

        return postsService.findById(id);

    }

}
```

![](readmeImage/img_5.png)

|키워드| 내용              |
|:---|:----------------|
|@PathVariable| url의 {?} 부분에 접근 |

<br/>

- PostsResponseDto

```java
import lombok.Builder;
import lombok.Getter;
import org.example.jodu_01_Starter.domain.posts.Posts;

@Getter
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;

    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
    
}
```

<br/>

- PostsUpdateRequestDto

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsUpdateRequestDto {

    private String title;
    private String content;

}
```

<br/>

- Posts

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
```

<br/>

- PostsService

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.domain.posts.Posts;
import org.example.jodu_01_Starter.domain.posts.PostsRepository;
import org.example.jodu_01_Starter.web.dto.PostsResponseDto;
import org.example.jodu_01_Starter.web.dto.PostsSaveRequestDto;
import org.example.jodu_01_Starter.web.dto.PostsUpdateRequestDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {

        return postsRepository.save(requestDto.toEntity()).getId();

    }


    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {

        Posts posts = postsRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("해당 게시글이 없습니다. id="+ id));

        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;

    }


    public PostsResponseDto findById(Long id) {

        Posts entity = postsRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("해당 게시글이 없습니다. id="+ id));

        return new PostsResponseDto(entity);
        
    }

}
```

> update 기능에서 데이터베이스에 쿼리를 날리는 부분이 없음 -> JPA 의 영속성 컨텍스트 때문 <br/>
> 영속성 컨텍스트란, 엔티티를 영구 저장하는 환경임. 트랜잯션이 끝나는 시점에 해당 테이블에 변경분을 반영함.

<br/>

- 수정 테스트 PostsApiControllerTest

```java
import org.example.jodu_01_Starter.domain.posts.Posts;
import org.example.jodu_01_Starter.domain.posts.PostsRepository;
import org.example.jodu_01_Starter.web.dto.PostsSaveRequestDto;
import org.example.jodu_01_Starter.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }


    // ...
 
    @Test
    public void Posts_수정된다() throws Exception {
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/"+ updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);

    }
}
```

---

<br/>

#### H2 데이터베이스를 로컬 환경에서 접근

- properties 추가
```
spring.h2.console.enabled=true
```

- http://localhost:8080/h2-console 로 접속

![](readmeImage/img_7.png)

![](readmeImage/img_6.png)

- 데이터 입력

![](readmeImage/img_8.png)

- API 요청

![](readmeImage/img_9.png)

<br/>

----

<br/>

#### 3.5 JPA Auditing 으로 생성시간 / 수정시간 자동화하기

|키워드| 내용                                                  |
|:---|:----------------------------------------------------|
|JPA Auditing| 엔티티에 특정 작업이 insert, update 될 때마다 반복되는 코드를 자동화할 때 사용 |

- LocalDate 사용

```java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
```

> BaseTimeEntity 클래스는 모든 Entity 의 상위 클래스가 되어 Entity 들의 createdDate, modifiedDate 를 자동으로 관리하는 역할을 한다.

| 키워드                                            | 내용                                                                                       |
|:-----------------------------------------------|:-----------------------------------------------------------------------------------------|
| @MappedSuperclass                              | JPA Entity 클래스들이 BaseTimeEntity 을 상속할 경우 필드들 (createdDate, modifiedDate) 도 칼럼으로 인식하도록 한다 |
| @EntityListeners(AuditingEntityListener.class) | BaseTimeEntity 클래스에 Auditing 기능을 포함시킨다                                                   |
| @CreatedDate                                   | Entity 가 생성되어 저장될 때 시간이 자동 저장된다                                                          |
| @LastModifiedDate                              | 조회한 Entity 의 값을 변경할 때 시간이 자동 저장된다                                                        |


- Posts entity 클래스에 상속해줌
```java
// ...
public class Posts extends BaseTimeEntity {
    // ...
}
// ...
```

- Application 클래스에서 JPA Auditing 어노테이션을 활성화
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

```

<br/>

- JPA Auditing 테스트 코드 작성하기 (PostRepositoryTest)

```java
    @Test
    public void BaseTimeEntity_등록() {

        // given
        LocalDateTime now = LocalDateTime.of(2022,8,22,0,0,0);
        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = postsList.get(0);

        System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVvv");
        System.out.println(">>>>>> createDate="+posts.getCreatedDate());
        System.out.println(">>>>>> modifiedDate="+posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);

    }
```

<br/>

---

<br/>

### 04 머스테치로 화면 구성하기

> 템플릿 엔진이란? 지정된 템플릿 양식과 데이터가 합쳐져서 HTML 문서를 출력하는 소프트웨어를 말한다. <br/>
> - 예를 들어 (서버 템플릿 엔진) JSP, Freemarker 와 (클라이언트 템플릿 엔진) React, Vue 등의 View 파일이 이에 해당  

> Q: 그렇다면 자바스크립트에서 JSP 나 Freemarker 처럼 자바 코드를 사용할 수 있는지?

![](readmeImage/img_10.png)

> A: no! - if 문과 관계없이 test 를 콘솔에 출력함. 서버 템플릿 엔진을 이용한 화면 생성은 서버에서 Java 코드로 
> 문자열을 만든 뒤 이 문자열을 HTML 로 변환하여 브라우저로 전달한다. 앞선 코드는 HTML 생성과정 syso("test"); 를 실행할 뿐이며,
> 이때의 자바 스크립트 코드는 단순한 문자열임. (이를 서버 사이드 렌더링, SSR)

![](readmeImage/img_34.png)

<br/>

> 반면 자바스크립트는 브라우저 위에서 작동하며 브라우저에서 작동될 때에는 서버 템플릿 엔진의 손을 벗어나 제어할 수 없음 <br/>
> 예시로 Vue.js 나 React.js 를 이용한 SPA (Single Page Application) 은 브라우저에서 화면을 생성함. 즉, 서버에서 이미 코드가 벗어난 경우.. <br/>
> 따라서 서버에서는 Json 혹은 Xml 형식의 데이터만 전달하고 클라이언트에서 조립해야 함. (이를 클라이언트 사이드 렌더링, CSR... https://sudalkim.tistory.com/22 참조) 

![](readmeImage/img_35.png)

> 클라이언트 템플릿 엔진에서 SSR 을 적용하기 위해선 즉, 자바스크립트 프레임워크의 화면 생성 방식을 서버에서 실행하기 위해선 Nashorn, J2V8 이 있다.


---
<br/>

#### 머스테치

머스테치 (http://mustache.github.io/)는 수많은 언어를 지원하는 가장 심플한 템플릿 엔진으로 루비, 자바스크립트, 파이썬, PHP, 자바,
펄, Go, ASP 등 현존하는 대부분 언어를 지원하고 있다. 따라서 자바 환경에서 사용될 때는 서버 템플릿 엔진으로, 자바스크립트에서 사용될 때는
클라이언트 템플릿 엔진으로 모두 사용될 수 있음.

- 자바 진영에서의 서버 템플릿 엔진

| 키워드           | 내용                                                                                                                                                         |
|:--------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| JSP, Velocity | - 스프링 부트에서는 권장하지 않는 템플릿 엔진임                                                                                                                                |
| Freemarker    | - 템플릿 엔진으로는 과하게 많은 기능을 지원 <br/> - 높은 자유도로 인해 숙련도가 낮을 수록 Freemarker 안에 비즈니스 로직이 추가될 확률이 높다                                                                  |
| Thymeleaf     | - 스프링 진영에서 적극적으로 밀고 있지만 문법이 어려움 <br/> - HTML 태그에 속성으로 템플릿 기능을 사용하는 방식                                                                                      |
| Mustache      | - 문법이 다른 템플릿 엔진보다 심플 <br/> - 로직 코드를 사용할 수 없어 View 의 역할과 서버의 역할이 명확하게 분리됨 <br/> - Mustache.js 와 Mustache.java 2가지가 다 있어, 하나의 문법으로 클라이언트/서버 템플릿을 모두 사용할 수 있다 |



---
<br/>

#### 머스테치 플러그인 설치


---
<br/>

#### 4.2 기본 페이지 만들기

- 머스테치 스타터 의존성 등록

``` implementation 'org.springframework.boot:spring-boot-starter-mustache ```

- 머스테치 파일 생성 (index.mustache)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" http-equiv="refresh" content="5">
	<title>스프링부트 웹서비스</title>
</head>
<body>
	<h1>스프링 부트로 시작하는 웹 서비스</h1>
</body>
</html>
```

|키워드| 내용       |
|:---|:---------|
|`<meta charset="UTF-8" http-equiv="refresh" content="5">`| 5초마다 리로딩 |



- index.mustache 에 URL 매핑

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}

```

- 테스트 (Postman)

![](readmeImage/img_13.png)

- 테스트 (junit)

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IndexControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void 메인페이지_로딩() {
        // when
        String body = this.restTemplate.getForObject("/", String.class);

        // then
        assertThat(body).contains("스프링 부트로 시작하는 웹 서비스");
    }

}
```

> TestRestTemplate 을 통해 "/" 로 호출했을 때 index.mustache 에 포함된 코드들이 있는지 확인 ("스프링 부트로 시작하는 웹 서비스" 문자열이 포함되어 있는지 확인)


- 결과

![](readmeImage/img_14.png)

--- 

<br/>

#### 4.3 게시글 등록 화면 만들기

부트스트랩 라이브러리 외부 CDN 사용 (레이아웃 방식으로, 공통 영역을 별도의 파일로 분리하여 필요한 곳에서 가져다 쓰는)


- header.mustache

```html
<!DOCTYPE HTML>
<html>
<head>
	<title>스프링부트 웹서비스</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
```

- footer.mustache

```html
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

<!--index.js 추가-->
<script src="/js/app/index.js"></script>
</body>
</html>
```

> - HTML 은 위에서 부터 코드가 실행됨 (head 가 다 실행되고서야 body 가 실행된다) <br/>
>   - 따라서 페이지 로딩속도를 높이기 위해 css 는 header 에 js 는 footer 에 주로 위치 <br/>
> - head 가 다 불러지지 않으면 백지 화면만 노출 <br/>
>  - js 의 용량이 클수록 body 부분의 실행이 늦어짐.
>  - css 는 화면을 그리는 역할이고 늦게 불러온다면 css 가 적용되지 않은 깨진 화면을 사용자가 보게됨
> - bootstrap.js 의 경우 제이쿼리가 꼭 있어야만 한다.
>   - 따라서 제이쿼리를 호출한 뒤에 bootstrap.js 를 호출하도록 한다

- index.mustache 에 레이아웃 적용

```html
{{>layout/header}}

	<h1>스프링 부트로 시작하는 웹 서비스</h1>

{{>layout/footer}}
```

|키워드| 내용                                                    |
|:---|:------------------------------------------------------|
|{{>layout/header}}| {{>}} 는 현재 머스테치 파일 (index.mustache) 을 기준으로 다른 파일을 가져옴 |


<br/>

- index.mustache 에 글 등록 버튼 추가

```html
{{>layout/header}}

	<h1>스프링 부트로 시작하는 웹 서비스</h1>

	<div class="col-md-12">
		<div class="row">
			<div class="col-md-6">
				<a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
			</div>
		</div>
	</div>

{{>layout/footer}}
```

<br/>

- 글 등록 버튼 api 호출

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // ...
 
    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

}
```

> /posts/save 호출 시 posts-save.mustache 파일을 호출

<br/>

- 등록 버튼의 API 호출을 위한 자바스크립트 코드 추가

```javascript
var main = {
    init : function () {
        let _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });

    },
    save : function () {
        let data = {
            title: $('#title').val(),
            author: $('#author').val(),
            content: $('#content').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 등록되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

};

main.init();
```

|키워드| 내용                                                                         |
|:---|:---------------------------------------------------------------------------|
|JSON.stringify(data)| Converts a JavaScript value to a JavaScript Object Notation (JSON) string. |
|window.location.href = '/'| 글 등록이 성공하면 메인페이지 (/) 로 이동                                                  |

브라우저의 스코프는 공용 공간으로 쓰이기 때문에 나중에 로딩된 js 의 init, save 가 먼저 로딩된 js 의 function 을 덮어쓸 수도 있다.
즉, 함수명이 중복되는 경우가 발생함으로 index.js 만의 유효범위를 만들어 사용하도록 한다. (main.init)

<br/>

- footer 에 index.js 추가

```html
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

<!--index.js 추가-->
<script src="/js/app/index.js"></script>
</body>
</html>
```

| 키워드                              | 내용                                                                                                                              |
|:---------------------------------|:--------------------------------------------------------------------------------------------------------------------------------|
| <script src="/js/app/index.js"\> | - 절대 경로 (/) 로 바로 시작함을 확인할 수 있다. <br/> - 스프링 부트는 기본적으로 src/main/resources/static 에 위치한 자바스크립트, css, 이미지 등 정적 파일들은 URL 에서 / 로 설정됨 |

![](readmeImage/img_15.png)

- 실행

![](readmeImage/img_16.png)
![](readmeImage/img_17.png)
![](readmeImage/img_18.png)

---

<br/>

#### 4.4 전체 조회 화면 만들기

- index.mustache

```html
{{>layout/header}}

	<h1>스프링 부트로 시작하는 웹 서비스</h1>

	<div class="col-md-12">
		<div class="row">
			<div class="col-md-6">
				<a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
			</div>
		</div>

	<br>

		<!--목록 출력 영역-->
		<table class="table table-horizontal table-bordered">
			<thead class="thead-strong">
			<tr>
				<th>게시글번호</th>
				<th>제목</th>
				<th>작성자</th>
				<th>최종수정일</th>
			</tr>
			</thead>
			<tbody id="tbody">
			{{#posts}}
				<tr>
					<td>{{id}}</td>
					<td><a href="/posts/update/{{id}}">{{title}}</a></td>
					<td>{{author}}</td>
					<td>{{modifiedDate}}</td>
				</tr>
			{{/posts}}
			</tbody>
		</table>
	</div>

{{>layout/footer}}
```

| 키워드        | 내용                                                       |
|:-----------|:---------------------------------------------------------|
| {{#posts}} | - posts 라는 List 를 순회한다 <br/> - Java 의 for 문과 동일하게 생각하면 됨 |
| {{id}}     | - List 에서 뽑아낸 객체의 필드를 사용한다                               |



- repository

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    @Query("SELECT p FROM Posts p ORDER BY p.id DESC")
    List<Posts> findAllDesc();
    
}
```

> 규모가 있는 프로젝트에서의 FK 의 조인, 복잡한 조건 등으로 인해 Entity 클래스만으론 처리하기 어려워
> 조회용 프레임워크를 추가로 사용함. ex) querydsl, jooq, MyBatis 등이 있음. 조회는 위 3가지 프레임워크
> 중 하나를 통해 조회하고 등록/수정/삭제 등은 SpringDataJpa 를 통해 진행함. 그러나 Querydsl 을 추천
> <br/>
> 
> 1. 타입 안정성이 보장됨
>    - 단순 문자열로 쿼리를 생성하는 것이 아니라, 메소드를 기반으로 쿼리를 생성하기 때문에 오타나 존재하지 않는 컬럼명을 명시할 경우 IDE 에서 자동으로 검출됨. 이 장점은 Jooq 에서도 지원하는 장점이지만, MyBatis 에서는 지원하지 않는다.
> 2. 국내 많은 회사에서 사용 중
>    - 쿠팡, 배민 등 JPA 를 적극적으로 사용하는 회사에서는 Querydsl 를 적극적으로 사용 중이다.
> 3. 래퍼런스가 많음
>    - 많은 회사와 개발자들이 사용하다보니 그만큼 국내 자료가 많다.

<br/>

- PostsService 에 findAllDesc() 추가

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.domain.posts.Posts;
import org.example.jodu_01_Starter.domain.posts.PostsRepository;
import org.example.jodu_01_Starter.web.dto.PostsListResponseDto;
import org.example.jodu_01_Starter.web.dto.PostsResponseDto;
import org.example.jodu_01_Starter.web.dto.PostsSaveRequestDto;
import org.example.jodu_01_Starter.web.dto.PostsUpdateRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("해당 게시글이 없습니다. id="+ id));
        posts.update(requestDto.getTitle(), requestDto.getContent());
        return id;
    }

    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("해당 게시글이 없습니다. id="+ id));
        return new PostsResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllDesc().stream().map(PostsListResponseDto::new).collect(Collectors.toList());
    }

}
```

|키워드| 내용                                                             |
|:---|:---------------------------------------------------------------|
|@Transactional(readOnly = true)| - (readOnly = true) 를 주면 트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도가 개선됨 |

> - 람다식 <br/>
> `.map(PostsListResponseDto::new)` 의 코드는 `map(posts -> new PostListResponseDto(posts)` 와 같음 <br/>
> postsRepository 의 결과로 넘어온 Posts 의 Stream 을 map 을 통해 PostListResponseDto 로 변환 -> List 로 반환하는 메소드임

<br/>

- PostsListResponseDto 생성

```java
import lombok.Getter;
import org.example.jodu_01_Starter.domain.posts.Posts;

import java.time.LocalDateTime;

@Getter
public class PostsListResponseDto {

    private Long id;
    private String title;
    private String author;
    private LocalDateTime modifiedDate;

    public PostsListResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.modifiedDate = entity.getModifiedDate();
    }

}
```

<br/>

- IndexController 수정

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.service.posts.PostsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postsService.findAllDesc());
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

}
```

| 키워드                      | 내용                                                                                                             |
|:-------------------------|:---------------------------------------------------------------------------------------------------------------|
| @RequiredArgsConstructor | 생성자 주입                                                                                                         |
| Model                    | - 서버 템플릿 엔진에서 사용할 수 있는 객체를 저장할 수 있다 <br/> - 여기서는 postsServiceAllDesc() 로 가져온 결과를 posts 로 index.mustache 에 전달한다 |


- 결과

![](readmeImage/img_19.png)


---

<br/>

#### 4.5 게시글 수정, 삭제 화면 만들기


- 게시글 수정 API

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.service.posts.PostsService;
import org.example.jodu_01_Starter.web.dto.PostsResponseDto;
import org.example.jodu_01_Starter.web.dto.PostsSaveRequestDto;
import org.example.jodu_01_Starter.web.dto.PostsUpdateRequestDto;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostsService postsService;

    // ...

    /*수정한다*/
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }

    // ...
 
}
```

<br/>

- 게시글 수정 화면 (posts-update.mustache)

```html
{{>layout/header}}

<h1>게시글 수정</h1>

<div class="col-md-12">
	<div class="col-md-4">
		<form>
			<div class="form-group">
				<label for="title">글 번호</label>
				<input type="text" class="form-control" id="id" value="{{post.id}}" readonly>
			</div>
			<div class="form-group">
				<label for="title">제목</label>
				<input type="text" class="form-control" id="title" value="{{post.title}}">
			</div>
			<div class="form-group">
				<label for="author"> 작성자 </label>
				<input type="text" class="form-control" id="author" value="{{post.author}}" readonly>
			</div>
			<div class="form-group">
				<label for="content"> 내용 </label>
				<textarea class="form-control" id="content">{{post.content}}</textarea>
			</div>
		</form>

		<a href="/" role="button" class="btn btn-secondary">취소</a>
		<button type="button" class="btn btn-primary" id="btn-update">수정 완료</button>
		<button type="button" class="btn btn-danger" id="btn-delete">삭제</button>

	</div>
</div>

{{>layout/footer}}
```

|키워드| 내용                                                                                      |
|:---|:----------------------------------------------------------------------------------------|
|{{post.id}}| - 머스테치는 객체의 필드 접근 시 점 (Dot) 으로 구분한다 <br/> - 즉, Post 클래스의 id 에 대한 접근은 post.id 로 사용할 수 있다 |
|readonly| - input 태그에 읽기 기능만 허용하는 속성이다. <br/> - id 와 author 는 수정할 수 없도록 읽기만 허용하도록 추가함             |

<br/>

- update function 추가

```javascript
var main = {
    init : function () {

        // ...
     
        $('#btn-update').on('click', function () {
            _this.update();
        });

    },
 
    // ...
 
    update : function () {
        let data = {
            title: $('#title').val(),
            content: $('#content').val()
        };

        let id = $('#id').val();

        $.ajax({
            type: 'PUT',
            url: '/api/v1/posts/'+id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 수정되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

};

main.init();
```

| 키워드                          | 내용                                                                                                                                           |
|:-----------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------|
| $('#btn-update').on('click') | - btn-update 라는 id 를 가진 HTML 요소에 click 이벤트가 발생할 때 update function 을 실행하도록 이벤트를 등록                                                            |
| update: function()           | - 신규로 추가될 update function 이다.                                                                                                                |
| type:'PUT'                   | - 여러 HTTP Method 중 PUT 메소드를 선택한다 <br/> - PostsAPIController 에 있는 API 에서 이미 @PutMapping 으로 선언했기 때문에 PUT 을 사용해야 한다 (REST 규약에 맞게 설정된 것임)  <br/> |
| url: '/api/v1/posts/'+id                    | - 어느 게시글을 수정할지 URL Path 로 구분하기 위해 Path 에 id를 추가한다                                                                                            |

> REST 에서 CRUD 는 다음과 같이 HTTP Method 에 매핑된다
> - 생성 (Create) - POST
> - 읽기 (Read) - GET
> - 수정 (Update) - PUT
> - 삭제 (Delete) - DELETE

<br/>

- 수정 페이지 이동 기능 추가

```html
{{>layout/header}}

	<h1>스프링 부트로 시작하는 웹 서비스</h1>

     
     <!--...-->

		<!--목록 출력 영역-->
		<table class="table table-horizontal table-bordered">
			<thead class="thead-strong">
			<tr>
				<th>게시글번호</th>
				<th>제목</th>
				<th>작성자</th>
				<th>최종수정일</th>
			</tr>
			</thead>
			<tbody id="tbody">
			{{#posts}}
				<tr>
					<td>{{id}}</td>
					<td><a href="/posts/update/{{id}}">{{title}}</a></td>
					<td>{{author}}</td>
					<td>{{modifiedDate}}</td>
				</tr>
			{{/posts}}
			</tbody>
		</table>
	</div>

{{>layout/footer}}
```

| 키워드                                   | 내용                                                                 |
|:--------------------------------------|:-------------------------------------------------------------------|
| <a href="/posts/update/{{id}}"\></a\> | - 타이틀 {title} 에 a tag 를 추가한다 <br/> - 타이틀을 클릭하면 해당 게시글의 수정화면으로 이동한다 |

<br/>

- 수정화면 GET (read) 메소드 추가

```java

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    // ...
 
    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {

        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }

}
```

<br/>

- 결과

![](readmeImage/vi_1.gif)


---

<br/>

#### 게시글 삭제

- 수정화면에 삭제 버튼 추가 (posts-update.mustache)

```html
<a href="/" role="button" class="btn btn-secondary">취소</a>
<button type="button" class="btn btn-primary" id="btn-update">수정 완료</button>
<button type="button" class="btn btn-danger" id="btn-delete">삭제</button>
```

|키워드| 내용                                                               |
|:---|:-----------------------------------------------------------------|
|btn-delete| - 삭제 버튼을 수정 완료 버튼 앞에 추가한다 <br/> - 해당 버튼 클릭 시 JS 에서 이벤트를 수신할 예정.. |



- 삭제 이벤트를 진행할 JS 코드 추가 (index.js)
```javascript
 var main = {
 init: function () {
  let _this = this;
  
  //...
  
  $('#btn-delete').on('click', function () {
   _this.delete();
  });
 },
 
 // ...
 
 delete: function () {
  let id = $('#id').val();

  $.ajax({
   type: 'DELETE',
   url: '/api/v1/posts/' + id,
   dataType: 'json',
   contentType: 'application/json; charset=utf-8',
  }).done(function () {
   alert('글이 삭제되었습니다.');
   window.location.href = '/';
  }).fail(function (error) {
   alert(JSON.stringify(error));
  });
 },

};

main.init();
```

<br/>

- 삭제 API 생성 (PostsService)

```java
@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    // ...

    @Transactional
    public void delete(Long id) {
        Posts posts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id="+id));
        postsRepository.delete(posts);
    }

}
```

|키워드| 내용                                                                                                                                                                    |
|:---|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|postsRepository.delete(posts)| - JpaRepository 에서 이미 delete 메소드를 지원하고 있으니 이를 활용해도 됨 <br/> - 엔티티를 파라미터로 삭제할 수도 있고, deleteById 메소드를 이용하면 id 로 삭제할 수도 있다 <br/> - 존재하는 Posts 인지 확인을 위해 엔티티 조회 후 그대로 삭제한다 |

<br/>

- 삭제 API 생성 (PostsApiController)

```java
@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostsService postsService;

    // ...

    /*삭제한다*/
    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id) {
        postsService.delete(id);
        return id;
    }

}
```

- 결과

![](readmeImage/img_20.png)



---

### 05 스프링 시큐리티와 OAuth 2.0 으로 로그인 기능 구현하기

스프링 시큐리티는 막강한 인증 (Authentication) 과 인가 (Authorization) 혹은 권한 부여 기능을 가진
프레임워크로 사실상 스프링 기반의 애플리케이션에서는 보안을 위한 표준이다. 인터셉터, 필터 기반의 보안 기능을 
구현하는 것보다 스프링 시큐리티를 통해 구현하는 것을 적극적으로 권장하고 있다.

---
<br/>

#### 5.1 스프링 시큐리티와 스프링 시큐리티 Oauth2 클라이언트

> #### 로그인 기능을 직접 구현할 경우 다음을 전부 구현해야 함
>
> - 로그인 시 보안
> - 회원가입 시 이메일 혹은 전화번호 인증
> - 비밀번호 찾기
> - 비밀번호 변경
> - 회원정보 변경

OAuth 로그인 구현 시 앞선 목록의 것을 구글, 페이스북, 네이버 등에 맡기고 서비스 개발에 집중할 수 있다.

<br/>

- spring-security-oauth2-autoconfigure vs Spring Security Oauth2 Client
  
  - spring-security-oauth2-autoconfigure
    - 스프링부트 2에서도 1.5에서 쓰던 설정을 그대로 사용할 수 있다
    - 기존에 안전하게 작동하던 코드를 그대로 사용할 수 있음
  - Spring Security Oauth2 Client
    - 스프링 팀에서 기존 1.5 에서 사용되던 spring-security-oauth 프로젝트는 유지 상태로 결정했으며 더는 신규 기능의 추가는 없고 버그 수정 정도의 기능만 추가될 예정. 신규 기능은 새 oauth2 라이브러리에서만 지원하겠다고 선언
    - 스프링 부트용 라이브러리 (starter) 출시
    - 기존에 사용되던 방식은 확장 포인트가 적절하게 오픈되어 있지 않아 직접 상속하거나 오버라이딩 해야 하고 신규 라이브러리의 경우 확장 포인트를 고려해서 설계된 상태

<br/>

- 스프링 부트2 방식의 spring-security-oauth2-autoconfigure 의 경우 다음 두 가지를 확인해라

![](readmeImage/img_21.png)

> 스프링 부트 1.5 방식에서는 url 주소를 모두 명시해야함. 반면 2.0 에서는 client 인증 정보만 입력
> 1.5 에서 직접 입력했던 값들은 2.0에선 모두 enum 으로 대체됨

- CommonOAuth2Provider 라는 enum 이 새롭게 추가되어 구글, 깃허브, 페이스북, 옥타의 기본 설정값은 모두 여기서 제공됨

```java
import java.sql.ClientInfoStatus;

public enum CommonOAuth2Provider {

 Google {
  @Override
  public Builder getBuilder(String registrationId) {
   ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.BASIC, DEFAULT_REDIRECT_URI);
   builder.scope("openid", "profile", "email");
   builder.authorizationUri("https://accounts.google.com/o/oauth2/auth");
   builder.tokenUri("https://accounts.google.com/o/oauth2/token");
   builder.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
   builder.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo");
   builder.userNameAttributeName("name");
   builder.clientName("Google");
   return builder;
  }
  
 },
 // ...
}
```

> 이외에 다른 소셜 로그인 (네이버, 카카오 등)을 추가한다면 직접 다 추가해주어야 함

---

<br/>

### 5.2 구글 서비스 등록

- 구글 서비스에 신규 서비스 생성 (https://console.cloud.google.com)

![](readmeImage/img_23.png)

![](readmeImage/img_22.png)

![](readmeImage/img_24.png)

![](readmeImage/img_25.png)


| 키워드             | 내용                                                |
|:----------------|:--------------------------------------------------|
| 애플리케이션 이름       | 구글 로그인 시 사용자에게 노추로딜 애플리케이션 이름을 말함                 |
| 담당자 이메일 주소      | 사용자 동의 화면에서 노출될 이메일 주소로 보통은 서비스의 help 이메일 주소를 사용함 |
| Google API 의 범위 | 프로젝트에 등록할 구글 서비스에서 사용할 범위 목록임.                    |

- OAuth 클라이언트 ID 만들기

![](readmeImage/img_27.png)

![](readmeImage/img_26.png)


> ##### 승인된 리디렉션 URI
> - 서비스에서 파라미터로 인증 정보를 주었을 때 인증이 성공하면 구글에서 리다이렉트할 URL 이다
> - 스프링 부트 2버전의 시큐리티에서는 기본적으로 {도메인}/login/oauth2/code/{소셜서비스코드}로 리다이렉트 URL을 지원하고 있다
> - 사용자가 별도로 리다이렉트 URL 을 지원하는 Controller 를 만들 필요가 없으며 시큐리티에서 이미 구현해 놓은 상태이다
> - 현재는 개발 단계이므로 http://localhost:8080/login/oauth2/code/google 로만 등록한다
> - AWS 서버에 배포하게 되면 localhost 외에 추가로 주소를 추가해야하며, 이는 이후 단계에서 진행한다

<br/>

- application-oauth 등록 (application-oauth.properties  --- .gitignore 처리)

```
spring.security.oauth2.client.registration.google.client-id=클라이언트 ID
spring.security.oauth2.client.registration.google.client-secret=클라이언트 보안 비밀
spring.security.oauth2.client.registration.google.scope=profile,email
```

|키워드| 내용                                                                                                                                                                                                                                                                                                                            |
|:---|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|scope=profile,email| - 많은 예제에서는 이 scope 를 별도로 등록하지 않고 있다 <br/> - 기본값이 openid,profile,email 이기 때문 <br/> - 강제로 profile,email 를 등록한 이유는 openid 라는 scope 가 있으면 Open id Provider 로 인식하기 때문 <br/> - 이렇게 되면 OpenId Provider 인 서비스 (구글)와 그렇지 않은 서비스 (네이버/카카오 등)로 나눠서 각각OAuthService를 만들어야 한다 <br/> - 하나의 OAuth2Service로 사용하기 위해 일부로 openid scope 를 빼고 등록한다 |


> 스프링 부트에선 properties 의 이름을 application-xxx.properties 로 만들면 xxx 라는 이름의 profile 이 생성되어 이를 통해 관리할 수 있다
> 즉, profile=xxx 라는 식으로 호출하면 해당 properties 의 설정을 가져올 수 있음.


- application.properties 에 다음 코드를 추가

``spring.profiles.include=oauth``


---

<br/>

#### 5.3 구글 로그인 연동하기


- 사용자 정보 담당 도메인 User 클래스 생성

```java
package org.example.jodu_01_Starter.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jodu_01_Starter.domain.BaseTimeEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

}
```

|키워드| 내용                                                                                                                                                                                       |
|:---|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|@Enumerated(EnumType.STRING)| - JPA 로 데이터베이스로 저장할 때 Enum 값을 어떦 형태로 저장할지를 결정한다 <br/> - 기본적으로 int 로 된 숫자가 저장됨 <br/> - 숫자로 저장되면 데이터베이스로 확인할 때 그 값이 무슨 코드를 의미하는지 알 수가 없슴 <br/> - 따라서 문자열 (EnumTyoe.STRING) 으로 저장될 수 있도록 선언 |

<br/>

- 각 사용자 권한 관리할 Enum 클래스 생성 (Role)

```java
package org.example.jodu_01_Starter.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;

}
```

> 스프링 시큐리티에서는 권한 코드에 항상 ROLE_ 이 앞에 있어야만 ROLE_GUEST, ROLE_USER 등으로 지정한다

<br/>

- User 의 CRUD 를 책임질 리포지터리 생성 (UserRepository)

```java
package org.example.jodu_01_Starter.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
```
| 키워드         | 내용                                                                      |
|:------------|:------------------------------------------------------------------------|
| Optional<?> | Optional 객체를 사용하면 예상치 못한 NullPointerException 예외를 제공되는 메소드로 간단히 회피할 수 있다 |
|findByEmail| - 소셜 로그인으로 반환되는 값 중 email 을 통해 이미 생성된 사용자인지 처음 가입하는 사용자인지 판단하기 위한 메소드이다 |


---

<br/>

#### 스프링 시큐리티 설정

- 스프링 시큐리티 관련 의존성 추가

``implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'``

|키워드| 내용                                                                                                                           |
|:---|:-----------------------------------------------------------------------------------------------------------------------------|
|spring-boot-starter-oauth2-client| - 소셜 로그인 등 클라이언트 입장에서 소셜 기능 구현 시 필요한 의존성이다 <br/> - spring-security-oauth2-client 와 spring-sercurity-oauth2-jose 를 기본으로 관리해준다 |


- OAuth 라이브러리를 이용한 소셜 로그인 설정 코드 저장 (SecurityConfig)

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.domain.user.Role;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

 private final CustomOAuth2UserService customOAuth2UserService;

 @Override
 protected void configure(HttpSecurity http) throws Exception {
  http.csrf().disable().headers().frameOptions().disable().and()
          .authorizeRequests()
          .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()
          .antMatchers("/api/v1/**").hasRole(Role.USER.name())
          .anyRequest().authenticated()
          .and().logout().logoutSuccessUrl("/")
          .and().oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
 }

}
```

| 키워드                                                 | 내용                                                                                                                                                                      |
|:----------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @EnableWebSecurity                                  | - Spring Security 설정들을 활성화시킨다                                                                                                                                           |
| csrf().disable().headers().frameOptions().disable() | - h2-console 화면을 사용하기 위해 해당 옵션들을 disable 한다                                                                                                                             |
| authorizeRequests                                   | - URL 별 권한 관리를 설정하는 옵션의 시작접이다 <br/> - authorizeRequests 가 선언되어야만 antMatchers 옵션을 사용할 수 있음                                                                               |
| antMatchers                                         | - 권한 관리 대상을 지정하는 옵션임 <br/> - URL, HTTP 메소드별로 관리가 가능하다 <br/> - "/" 등 지정된 URL 들은 permitAll() 옵션을 통해 전체 열람 권한을 줌 <br/> - "/api/v1/**" 주소를 가진 API 는 USER 권한을 가진 사람만 가능하도록 함 |
| anyRequest                                          | - 설정된 값들 이외 나머지 URL 등을 나타냄  <br/> - 여기서는 authenticated() 을 추가하여 나머지 URL 들은 모두 인증된 사용자들에게만 허용하게 함 <br/> - 인증된 사용자 즉, 로그인한 사용자들을 말함                                       |
| logout().logoutSuccessUrl("/")                      | - 로그아웃 기능에 대한 여러 설정의 진입점이다 <br/> - 로그아웃 성공 시 / 주소로 이동함                                                                                                                  |
|userInfoEndpoint| - OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당                                                                                                                               |
|userService| - 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록 <br/> - 리소스 서버 (즉, 소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있음                                            |

<br/>

- CustomOAuth2UserService 클래스 생성

구글 로그인 이후 가져온 사용자의 정보 (email, name, picture) 등을 기반으로 가입 및 정보수정, 세션 저장 등의 기능을 지원


```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.config.auth.dto.OAuthAttributes;
import org.example.jodu_01_Starter.config.auth.dto.SessionUser;
import org.example.jodu_01_Starter.domain.user.User;
import org.example.jodu_01_Starter.domain.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;


@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeKey, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );

    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
```

| 키워드                  | 내용                                                                                                                                                              |
|:---------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| registrationId       | - 현재 로그인 진행 중인 서비스를 구분하는 코드 <br/> - 지금은 구글만 사용하는 불필요한 값이지만, 이후 네이버 로그인 연동 시에 네이버 로그인인지, 구글 로그인인지 구분하기 위해 사용함                                                    |
| userNameAttributeKey | - OAuth2 로그인 진행 시 키가 되는 필드값을 말하며 PK 와 같은 의미임 <br/> - 구글의 경우 기본적으로 코드를 지원하지만, 네이버 카카오 등은 기본 지원하지 않음. (구글의 기본 코드는 "sub") <br/> - 이후 네이버 로그인과 구글 로그인을 동시 지원할 때 사용됨 |
| OAuthAttributes      | - OAuth2UserService 를 통해 가져온 OAuth2User 의 attribute 를 담을 클래스임 <br/> - 이후 네이버 등 다른 소셜 로그인도 이 클래스를 사용                                                             |
| SessionUser          | - 세션에 사용자 정보를 저장하기 위한 DTO 클래스임 <br/> - 사용자의 이름이나 프로필 사진이 변경되면 User 엔티티에도 반영되어야 하기에 DTO 하나 만들어줌                                                                  |


<br/>

- OAuthAttributes (dto)

```java
import lombok.Builder;
import lombok.Getter;
import org.example.jodu_01_Starter.domain.user.Role;
import org.example.jodu_01_Starter.domain.user.User;

import java.util.Map;

@Builder
@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    public static OAuthAttributes of(String registrationId, String userNameAttributeKey, Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeKey, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeKey)
                .build();
    }


    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}
```

| 키워드  | 내용                                                                                                                                                                         |
|:-----|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| of() | - OAuth2User 에서 반환하는 사용자 정보는 Map 이기 때문에 값 하나하나를 변환해야 함                                                                                                                     |
|toEntity()| - User 엔티티를 생성함 <br/> - OAuthAttributes 에서 엔티티를 생성하는 시점은 처음 가입할 때임 <br/> - 가입할 때의 기본 권한을 Role.GUEST 를 사용 <br/> - OAuthAttributes 클래스 생성이 끝났으면 같은 패키지에 SessionUser 클래스를 생성함 |


<br/>

- SessionUser (dto) 추가

```java
import lombok.Getter;
import org.example.jodu_01_Starter.domain.user.User;

@Getter
public class SessionUser {

    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }

}
```

> SessionUser 에는 인증된 사용자 정보만 필요하다



---

<br/>

- 로그인 테스트

```html
{{>layout/header}}

<h1>스프링 부트로 시작하는 웹 서비스</h1>

<div class="col-md-12">

	<!--로그인 기능 영역-->
	<div class="row">
		<div class="col-md-6">
			<a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
			{{#userName}}
				Logged in as: <span id="user">{{userName}}</span>
				<a href="/logout" class="btn btn-info active" role="button">Logout</a>
			{{/userName}}
			{{^userName}}
				<a href="/oauth2/authorization/google" class="btn btn-success active" role="button">Google Login</a>
				<a href="/oauth2/authorization/naver" class="btn btn-secondary active" role="button">Naver Login</a>
			{{/userName}}
		</div>
	</div>

	<br>

	<!--목록 출력 영역--> 
    <!--....-->
</div>

{{>layout/footer}}
```

|키워드| 내용                                                                                                                                                                 |
|:---|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|{{#userName}}| - 머스테치는 다른 언어와 같은 if 문을 (if userName !=null) 제공하지 않음 <br/> - true/false 여부만 판단함 <br/> - 따라서 머스테치에서는 항상 최종값을 넘겨준다 <br/> - 여기서도 userName 이 있다면 userName 을 노출시키도록 구성함 |
|a href="/logout"| - 스프링 시큐리티에서 기본적으로 제공하는 로그아웃 url 이다 <br/> - 개발자가 별도로 저 URL 에 해당하는 컨트롤러를 만들 필요 없다 <br/> - SecurityConfig 클래스에서 URL 을 변경할 순 있지만 기본 URL 을 사용해도 충분함                    |
|{{^userName}}| - 머스테리에서 해당 값이 존재하지 않는 경우에 ^ 를 사용함 <br/> - 여기선 userName 이 없다면 로그인 버튼을 노출시키도록 구성함                                                                                   |
|a href="/oauth2/authorization/google"| - 스프링 시큐리티에서 기본적으로 제공하는 로그인 URL 임 <br/> - 로그아웃 URL 과 마찬가지로 개발자가 별도의 컨트롤러를 생성할 필요가 없다                                                                               |

<br/>

- IndexController (index.mustache 에서 세션 사용을 위한 코드 추가)

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.config.auth.dto.SessionUser;
import org.example.jodu_01_Starter.service.posts.PostsService;
import org.example.jodu_01_Starter.web.dto.PostsResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;


@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postsService.findAllDesc());
        
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if(user !=null) {
            model.addAttribute("userName", user.getName());
        }

        return "index";
    }

    // @GetMapping("/posts/save")
    // ...

}
```

|키워드| 내용                                                                                                                                           |
|:---|:---------------------------------------------------------------------------------------------------------------------------------------------|
|(SessionUser) httpSession.getAttribute("user")| - 앞서 작성된 CustomOAuth2UserService 에서 로그인 성공 시 세션에 SessionUser 를 저장하도록 구성함 <br/> - 즉, 로그인 성공 시 httpSession.getAttribute("user") 에서 값을 가져올 수 있다 |
|if(user !=null)| - 세션에 저장된 값이 있을 때만 model 에 userName 으로 등록한다 <br/> - 세션에 저장된 값이 없으면 model 엔 아무런 값이 없는 상태이니 로그인 버튼이 보임                                         |


- 비로그인 시 

![](readmeImage/img_31.png)

- 로그인 시

![](readmeImage/img_28.png)

- 로그아웃 시

![](readmeImage/img_29.png)

- h2 db 에 사용자 정보가 입력되었는 지 확인

![](readmeImage/img_30.png)

- role 이 guest 로 설정되어 있기에 글 등록이 안되는 것 또한 확인할 수 있다.

![](readmeImage/img_32.png)

- ROLE 을 USER 로 변경

![](readmeImage/img_36.png)

- 글 등록

![](readmeImage/img_33.png)

---

<br/>

#### 5.4 어노테이션 기반으로 개선하기

> 반복되는 코드 ``SessionUser user = (SessionUser) httpSession.getAttribute("user");`` 를 어노테이션을 통해 메소드 인자로 세션값을 바로 받을 수 있게 변경해보자!

- @LoginUser 생성

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
```

|키워드| 내용                                                                                                                         |
|:---|:---------------------------------------------------------------------------------------------------------------------------|
|@Target(ElementType.PARAMETER)| - 이 어노테이션이 생성될 수 있는 위치를 지정함 <br/> - PARAMETER 로 지정했으니 메소드의 파라미터로 선언된 객체에서만 사용 가능 <br/> - 이 외에도 클래스 선언문에 쓸 수 있는 TYPE 등이 있다! |
|@interface| - 이 파일을 어노테이션 클래스로 지정한다 <br/> - LoginUser 라는 이름을 가진 어노테이션이 생성되었다고 보면 됨                                                     |
|@Retention| - 메모리를 어느시점에 할당할 것인지 지정하는 어노테이션임                                                                                           |


- @Retention

| 키워드     | 내용                                                                |
|:--------|:------------------------------------------------------------------|
| SOURCE  | 컴파일러가 컴파일할때 해당 어노테이션의 메모리를 버럼 (사실상 어노테이션을 주석처럼 사용하는 것)            |
| CLASS   | 컴파일시에는 어노테이션의 메모리를 가져가지만 런타임시에는 사라진다.                             |
| RUNTIME | JVM 이 자바 바이트코드가 담긴 class 파일에서 런타임 환경을 구성하고 런타임을 종료할 때까지 메모리는 살아있따 |


<br/>

- LoginUserArgumentResolver

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.config.auth.dto.SessionUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class)!=null;
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user");
    }
}
```

| 키워드                 | 내용                                                                                                                      |
|:--------------------|:------------------------------------------------------------------------------------------------------------------------|
| supportsParameter() | - 컨트롤러 메서드의 특정 파라미터를 지원하는지 판단 <br/> - 여기선 파라미터에 @LoginUser 어노테이션이 붙어 있고, 파라미터 클래스 타입이 SessionUser.class 인 경우 true 를 반환함 |
| resolveArgument()   | - 파라미터에 전달할 객체를 생성함 <br/> - 여기선 세션에 객체를 가져옴                                                                             |
|@Component| - Bean Configuration 파일어 Bean 을 따로 등록하지 않아도 사용할 수 있다                                                                    |


<br/>

- LoginUserArgumentResolver 가 스프링에서 인식될 수 있도록 설정 (WebConfig)

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.config.auth.LoginUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
    }

}
```

|키워드| 내용   |
|:---|:-----|
|@Configuration| 빈 등록 |


- 스프링 MVC 패턴

![](readmeImage/img_37.png)

| 키워드     | 내용                                                                                                                   |
|:--------|:---------------------------------------------------------------------------------------------------------------------|
| 핸들러 매핑  | - Dispatcher Servlet 이 요청 URI 가 어떤 핸들러와 매핑되는지 찾는 과정이다 <br/> - 핸들러 매핑은 요청과 알맞은 핸들러 객체를 Dispatcher Servlet 에 리턴한다      |
| 핸들러 어댑터 | - 핸들러 매핑에서 리턴받은 핸들러 객체를 가지고 이에 맞는 어댑터를 찾는 과정이다 <br/> - 어댑터란 2개 이상의 인터페이스에 스펙이 맞지 않을 때 중간에 이 스펙을 맞도록 변환해주는 역할을 하는 객체임 |


<br>

- IndexController 에 적용

```java
import lombok.RequiredArgsConstructor;
import org.example.jodu_01_Starter.config.auth.LoginUser;
import org.example.jodu_01_Starter.config.auth.dto.SessionUser;
import org.example.jodu_01_Starter.service.posts.PostsService;
import org.example.jodu_01_Starter.web.dto.PostsResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {

 private final PostsService postsService;

 @GetMapping("/")
 public String index(Model model, @LoginUser SessionUser user) {
  model.addAttribute("posts", postsService.findAllDesc());

  if (user != null) {
   model.addAttribute("userName", user.getName());
  }

  return "index";
 }

 // ...

}
```

|키워드| 내용                                                                                                                         |
|:---|:---------------------------------------------------------------------------------------------------------------------------|
|@LoginUser SessionUser user| - 기존에 (User) httpSession.getAttribute("user") 로 가져오던 세션 정보 값이 개선됨 <br/> - 이제는 어느 컨트롤러든지 @LoginUser 만 사용하면 세션 정보를 가져올 수 있다! |


--- 

#### 5.5 세션 저장소로 데이터베이스 사용하기

- 내장 톰캣의 문제점
1. 현재 서비스는 세션이 내장 톰캣의 메모리에 저장되기 때문에 애플리케이션을 재실행하면 세션이 날라감.
즉, 배포 시마다 톰캣이 재시작됨
2. 두대 이상의 서버에서 서비스를 하고 있다면 톰캣마다 세션 동기화 설정을 해야 한다.


<br/>

해결책

> 1. 톰캣 세션을 사용한다.
>    - 일반적으로 별다른 설정을 하지 않을 때 기본적으로 선택되는 방식이다
>    - 이렇게 될 경우 톰캣 (WAS)에 세션이 저장되기 때문에 2대 이상의 WAS 가 구동되는 환경에서는 톰캣들 간의 세션 공유를 위한 추가 설정이 필요하다
> 2. MySQL 과 같은 데이터베이스를 세션 저장소로 사용한다
>    - 여러 WAS 간의 공용 세션을 사용할 수 있는 가장 쉬운 방법이다.
>    - 많은 설정이 필요 없지만, 결국 로그인 요청마다 DB IO 가 발생하여 성능상 이슈가 발생할 수 있다
>    - 보통 로그인 요청이 많이 없는 백오피스, 사내 시스템 용도에서 사용한다.
> 3. Redis, Memcached 와 같은 메모리 DB를 세션 저장소로 사용한다
>    - B2C 서비스에서 많이 사용하는 방식임
>    - 실제 서비스로 사용하기 위해선 Embedded Redis 와 같은 방식이 아닌 외부 메모리 서버가 필요함

이번 시간엔 데이터베이스를 세션 저장소로 사용하는 방식에 대해 알아볼 것이며 이유는 설정이 간단하고 사용자가 많은 서비스가 아니며 비용 절감을 위해서임.
이후 AWS 에서 이 서비스를 배포하고 운영할 때를 생각하면 레디스와 같은 메모리 DB를 사용하기엔 부담. 레디스와 같은 서비스 (엘라스틱 캐시)에 별도의 사용료를 지불해야함.

<br/>

- spring-session-jdbc 등록

``implementation 'org.springframework.session:spring-session-jdbc'``

- application.properties 에 세션 저장소를 jdbc 로 선택하도록 코드 추가

``spring.session.store-type=jdbc``

<br/>

- 결과 

![](readmeImage/img_38.png)

> 물론 현재는 H2 기반으로 스프링이 재실행될 때 H2 도 재시작되기 때문에 이후 AWS 로 배포하게 되면 AWS 의 데이퍼베이스 서비스인 RDS (Relational Database Service) 를 사용하게 됨

---

<br/>

#### 5.6 네이버 로그인

- 네이버 API 등록

https://developers.naver.com/products/login/api/api.md

![](readmeImage/img_39.png)

![](readmeImage/img_40.png)

![](readmeImage/img_41.png)


- application-oauth.properties 등록

```
# registration
spring.security.oauth2.client.registration.naver.client-id=Qla8COYfh6X8oFyb961D
spring.security.oauth2.client.registration.naver.client-secret=y15mFFg40L
spring.security.oauth2.client.registration.naver.authorization_grant_type=authorization_code
spring.security.oauth2.client.registration.naver.scope=name,email,profile_image
spring.security.oauth2.client.registration.naver.client-name=Naver

# Provider
spring.security.oauth2.client.provider.naver.authorization_uri=https://nid.naver.com/oauth2.0/authorize
spring.security.oauth2.client.provider.naver.token_uri=https://nid.naver.com/oauth2.0/token
spring.security.oauth2.client.provider.naver.user-info-uri=https://openapi.naver.com/v1/nid/me
spring.security.oauth2.client.provider.naver.user_name_attribute=response
```

> 네이버에서는 스프링 시큐리티를 공식 지원하지 않기 때문에 Common-OAuth2Provider 에서 해주던 값들도 전부 수동으로 입력해야 함.

|키워드| 내용                                                                                            |
|:---|:----------------------------------------------------------------------------------------------|
|user_name_attribute| - 기준이 되는 user_name 의 이름을 네이버에서는 response 로 해야함 <br/> - 네이버의 회원 조회 시 반환되는 JSON 의 형태가 아래와 같아서.. |

![](readmeImage/img_42.png)


스프링 시큐리티에선 하위 필드를 명시할 수 없다. 최상위 필드들만 user_name 으로 지정 가능ㅎ다. 그러나 네이버의
응답값 최상위 필드는 resultCode, message, response 이기에 스프링 시큐리티에서 인식 가능한 필드는 3개 중에서 골라야 한다.
본문에서 담고 있는 response 를 user_name 으로 지정하고 이후 자바 코드로 response 의 id 를 user_name 으로 지정할 것임


<br/>

#### 스프링 시큐리티 설정 등록


- OAuthAttributes 에 네이버인지 판단하는 코드와 네이버 생성자 추가

```java
@Builder
@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    public static OAuthAttributes of(String registrationId, String userNameAttributeKey, Map<String, Object> attributes) {
        if("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        return ofGoogle(userNameAttributeKey, attributes);
    }

    private static OAuthAttributes ofNaver(String userNameAttributeKey, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeKey)
                .build();
    }
    
    // ...

}
```

<br/>

- index.mustache 에 네이버 로그인 버튼 추가

```html
	<div class="row">
		<div class="col-md-6">
			<a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
			{{#userName}}
				Logged in as: <span id="user">{{userName}}</span>
				<a href="/logout" class="btn btn-info active" role="button">Logout</a>
			{{/userName}}
			{{^userName}}
				<a href="/oauth2/authorization/google" class="btn btn-success active" role="button">Google Login</a>
				<a href="/oauth2/authorization/naver" class="btn btn-secondary active" role="button">Naver Login</a>
			{{/userName}}
		</div>
	</div>
```

| 키워드 | 내용                                                                                                                                                    |
|:----|:------------------------------------------------------------------------------------------------------------------------------------------------------|
| /oauth2/authorization/naver | - 네이버 로그인 URL 은 application-oauth.properties 에 등록한 redirect-uri 값에 맞춰 자동으로 등록됨 <br/> - /oauth2/authorization/ 까지는 고정이고 마지막 Path 만 각 소셜 로그인 코드를 사용하면 됨 |

- 결과

![](readmeImage/img_43.png)

---

<br/>

#### 5.7 기존 테스트에 시큐리티 적용으로 문제가 되는 부분 해결

![](readmeImage/img_44.png)

<br/>

- 문제 1

``Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.example.jodu_01_Starter.config.auth.CustomOAuth2UserService' available: ``

> 소셜 로그인 관련 설정값들일 없기 때문에 발생하는 문제임. src/main 환경과 src/test 환경은 차이가 있으며 둘은 본인만의 환경 구성을 가짐
> <br/> 다만 src/main/resources/application.properties 가 테스트 코드를 수행할 때도 적용되는 이유는 test 에 application.properties 가 없으면 main 의 설정을 그대로 가져오기 때문이다.
> <br/> 반면, application-oauth.properties 의 경우는 안 가져옴

<br/>

- 테스트용 application.properties 추가

```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.h2.console.enabled=true
spring.profiles.include=oauth
spring.session.store-type=jdbc

# Test OAuth
spring.security.oauth2.client.registration.google.client-id=test
spring.security.oauth2.client.registration.google.client-secret=test
spring.security.oauth2.client.registration.google.scope=profile,email
```

<br/>

- 문제 2

```
Expected :200 OK
Actual   :302 FOUND
```

스프링 시큐리티로 인해 인증되지 않은 사용자의 요청을 이동시킴. 따라서 임의로 인증된 사용자를 추가하여 API 만 테스트

- 스프링 시큐리티 테스트를 위한 여러 도구를 지원하는 spring-security-test 를 build.gradle 에 추가

``implementation 'org.springframework.security:spring-security-test'``

- 임의의 사용자 인증 추가

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {
        //...
    @Test
    @WithMockUser(roles = "USER")
    public void Posts_등록된다() throws Exception {
        //...
    }

    @Test
    @WithMockUser(roles = "USER")
    public void Posts_수정된다() throws Exception {
        //...
    }
}
```

|키워드| 내용                                                                                                                                |
|:---|:----------------------------------------------------------------------------------------------------------------------------------|
|@WithMockUser(roles = "USER")| - 인증된 모의 (가짜) 사용자를 만들어서 사용 <br/> - roles 에 권한 추가할 수 있음. <br/> - 즉, 이 어노테이션으로 인해 ROLE_USER 권한을 가진 사용자가 API 를 요청하는 것과 동일한 효과를 가지게 됨 |


- PostsApiControllerTest 에 MockMvc 추가

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void Posts_등록된다() throws Exception {

        // given
        String title = "title";
        String content = "content";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        // when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

    }

    @Test
    @WithMockUser(roles = "USER")
    public void Posts_수정된다() throws Exception {
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/"+ updateId;

        // when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);

    }
}
```

| 키워드         | 내용                                                                                                 |
|:------------|:---------------------------------------------------------------------------------------------------|
| @Before     | - 매번 테스트가 시작되기 전에 MockMvc 인스턴스를 생성함                                                                |
| mvc.perform | - 생성된 MockMvc 를 통해 API 를 테스트함. <br/> - 본문 (Body) 영역은 문자열로 표현하기 위해 ObjectMapper 를 통해 문자열 JSON 으로 반환 |

<br/>

- 문제3 <br/>
@WebMvcTest 에서 CustomOAuth2UserService 를 찾을 수 없음

``No qualifying bean of type 'org.example.jodu_01_Starter.config.auth.CustomOAuth2UserService' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}``

> @WebMvcTest 는 WebSecurityConfigurerAdapter, WebMvcConfigurer, @ControllerAdvice, @Controller 를 읽음
> @Repository, @Service, @Component 는 스캔 대상이 아님

```java
import org.example.jodu_01_Starter.config.auth.SecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HelloController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @WithMockUser(roles="USER")
    @Test
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }

    @WithMockUser(roles="USER")
    @Test
    public void helloDto가_리턴된다() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(
                        get("/hello/dto")
                                .param("name", name)
                                .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.amount", is(amount)));
    }
}
```

---

<br/>

### 06 AWS 서버 환경을 만들어보자 - AWS EC2


- 24 시간 작동하는 서버에는
  - 집에서 PC 를 24 시간 구동
  - 호스팅 서비스 (Cafe 24, 코리아호스팅 등) 을 이용
  - 클라우드 서비스 (AWS, AZURE, GCP 등) 을 이용

일반적으로 비용은 호스팅 서비스나 집 PC 를 이용하는 것이 저렴하나 트래픽이 몰린다면 유동적으로 사양을 늘릴 수 있는 클라우드가 유리

<br/>

#### 클라우드의 형태

- Infrastructure as a Service (IaaS)
  - 기존 물리 장비를 미들웨어와 함께 묶어둔 추상화 서비스이다
  - 가상머신, 스토리지, 네트워크, 운영체제 등의 IT 인프라를 대여해 주는 서비스라고 보면 됨
  - AWS 의 EC2, S3 등
- Platfrom as a Service (PaaS)
  - 앞에서 언급한 IaaS 에서 한 번 더 추상화한 서비스이다
  - 한 번 더 추상화했기 때문에 많은 기능이 자동화되어 있다
  - AWS 의 Beanstalk, Heroku 등
- Software as a Service (SaaS)
  - 소프트웨어 서비스를 말한다
  - 구글 드라이브, 드랍박스, 외탭 등

#### AWS 의 장점 
- 첫 가입 시 1년간 대부분 서비스가 무료이다. (단, 서비스마다 제한은 있음)
- 클라우드에선 기본적으로 지원하는 기능 (모니터링, 로그관리, 백업, 복구, 클러스터링 등등)이 많아 개인이나 소규모일 때 개발에 좀 더 집중할 수 있다
- 많은 기업이 AWS 로 이전 중이다. 쿠팡, 우아한형제들, 리멤버 등 클라우드를 사용할 수 있는 회사에선 대부분 AWS 를 사용한다
- 사용자가 많다


---

<br/>

#### 6.4 AWS 회원 가입


- AWS 프리티어 계정 생성

``https://aws.amazon.com/ko/`` 로 이동한 뒤 무료 계정 만들기 선택

![](readmeImage/img_46.png)

![](readmeImage/img_45.png)


![](readmeImage/img_47.png)

> 구글 검색창에 '영어주소'

![](readmeImage/img_48.png)

> 카드 정보 등록

![](readmeImage/img_49.png)

> 플랜 선택

![](readmeImage/img_50.png)

> 콘솔 로그인 버튼 클릭


---

<br/>

#### 6.2 인스턴스 생성하기

EC2 (Elastic Compute Cloud) 는 AWS 에서 제공하는 성능, 용량 등을 유동적으로 사용할 수 있는 서비이며 보통 "AWS 에서 리눅스 서버 혹은 윈도우 서버를 사용합니다" 라고 하면 EC2 를 말하는 것이다.

> EC2 의 이름은 Elastic Compute Cloud 에서 C 가 2개가 있어 붙여진 이름임. 비슷한 예로 AWS 의 S3 는 Simple Storage Service 를 말함

#### EC2 에는 다음과 같은 제한 사한이 있음

- 사양이 t2.micro 만 가능
  - vCPU (가상 CPU) 1 Core, 메모리 1GB 사양임
  - 보통 vCPU 는 물리 CPU 사양의 절반 정도의 성능을 가짐
- 월 750시간의 제한이 있음. 이를 초과하면 비용 부과
  - 24시간 * 31일 = 744 시간임
  - 즉, 1대의 t2.micro 만 사용한다면 24시간 사용 가능


<br/>

---

#### 리전 확인

> 리전이란 AWS 의 서비스가 구동될 지역을 이야기하며, AWS 는 도시별로 클라우드 센터를 지어 해당 센터에서 구축된 가상머신들을 사용할 수 있는데 이를 리전이라고 한다.

- 리전 서울로 변경

![](readmeImage/img_51.png)

---

#### 화면 중앙의 검색창에 EC2 검색 후 클릭

![](readmeImage/img_52.png)

- 그러면 EC2 대시보드 짠 하고 나옴

![](readmeImage/img_53.png)

> 인스턴스란 EC2 서비스에 생성된 가상머신을 말한다.

---

#### AMI (Amazon Machine Image) 선택

AMI: EC2 인스턴스를 시작하는 데 필요한 정보를 이미지로 만들어 둔 것
> 인스턴스라는 가상머신에 운영체제 등을 설치할 수 있게 구워 넣은 이미지임, 예를 들어 아마존 리눅스 1 AMI 를 사용한다면 Amazon Linux 1 OS 가 인스턴스에 설치되어 개발자가 사용할 수 있음을 의미함

![](readmeImage/img_54.png)

![](readmeImage/img_55.png)

![](readmeImage/img_56.png)


> ![](readmeImage/img_57.png)
> 
> Amazon Linux 1 은 지원이 종료됨

---

#### 인스턴스 유형 선택

- free tier 에서 사용 가능한 t2.micro 선택

![](readmeImage/img_58.png)

t2 는 요금 타입을 뜻하며 micro 는 사양을 뜻한다. t2 외에도 t3 가 있으며 보통 이들을 T 시리즈라고 한다.
이들은 다른 서비스와 달리 크레딧이란 일종의 CPU 를 사용할 수 있는 포인트 개념이 있으며 인스턴스 크기에 따라 정해진 비율로 CPU 크레딧을
계속 받게 되며, 사용하지 않을 때는 크레딧을 축적하고, 사용할 때는 이 크레딧을 사용한다. 정해진 사양보다 더 높은 트래픽이 오면 크레딧을 좀 더
적극적으로 사용하며 트래픽을 처리하지만, 크레딧이 모두 사용되면 EC2 를 사용할 수 없다. 따라서 트래픽이 높은 서비스들은 T 시리즈를 사용하지 않고
다른 시리즈를 사용하기도 한다.

---

#### 스토리지 구성

서버의 용량은 얼마로 할지 선택, 설정의 기본값은 8GB 이며, 30GB 까지 프리티어로 가능함.

![](readmeImage/img_59.png)

---

#### 보안그룹 설정

보안 그룹은 방화벽을 뜻하며, '서버로 80포트 외에는 허용하지 않는다'는 역할을 하는 방화벽이
AWS 에서는 보안그룹으로 사용됨

![](readmeImage/img_61.png)

> ssh 설정을 전체 오픈으로 해두면 실수로 pem 키가 노출되는 순간 서버에서 가상화페가 채굴되는 것을 볼 수 있음

- 인스턴스 시작

![](readmeImage/img_60.png)

- 키페어가 없으면 생성

![](readmeImage/img_62.png)

> 인스턴스로 접근하기 위해선 pem 키 (비밀키)가 필요하다. 인스턴스는 지정된 pem 키 (비밀키) 와 매칭되는 공개키를 가지고 있어 해당 pem 키 외에는 접근을 허용하지 않는다.
> 일종의 마스터키이기 때문에 EC2 서버로 접속할 때 필수 파일이니 잘 관리할 수 있는 디렉토리로 저장해야 한다.

![](readmeImage/img_63.png)

![](readmeImage/img_64.png)

![](readmeImage/img_65.png)

- IP 와 도메인이 활당된 것을 확인할 수 있다

![](readmeImage/img_66.png)


---

인스턴스도 결국 하나의 서버이기 때문에 IP 가 존재한다.
또한 인스턴스 생성 시에 항상 새 IP 를 할당하며, 주의할 점이 인스턴스를 중지하고 다시 시작할 때도 새 IP 가 할당된다.
따라서 매번 접속해야 하는 IP 가 달라지기 때문에 고정 IP 할당을 해보자

#### EIP 할당

![](readmeImage/img_67.png)

![](readmeImage/img_68.png)

![](readmeImage/img_69.png)

![](readmeImage/img_70.png)

- 방금 생성한 탄력적 IP 를 인스턴스와 연결

![](readmeImage/img_71.png)

![](readmeImage/img_72.png)

![](readmeImage/img_73.png)

![](readmeImage/img_74.png)

> 탄력적 UP 는 생성하고 EC2 서버에 연결하지 않으면 비용이 발생한다. (탄력적 IP 생성하면 EC2에 바로 연결할 것) <br/>
> 또한 더는 사용할 인스턴스가 없을 때도 탄력적 IP를 삭제하지 않으면 비용 청구된다.
> 

// TODO - p 23

#### 6.3 EC2 서버에 접속하기

#### 윈도우 환경에서

- 실행 파일 다운로드

``https://www.putty.org/`` 에 접속하여 실행 파일 다운로드

- puttygen.exe 파일 실행

> putty 는 pem 키로 사용이 안 되며 pem 키를 ppk 파일로 변환 해야함.
> puttygen 은 이 과정을 진행해 주는 클라이언트이다.

- pem을 ppk 파일로 변환

Conversions -> import key -> pem 파일 선택 -> Save private key -> yes -> ppk 파일을 저장할 폴더 선택 후 ok

- putty.exe 실행

![](readmeImage/img_75.png)

> - HostName: username@puvlic_Ip 를 등록한다. 우리가 생성한 Amazon Linux 는 ec2-user 가 username 이라서 ec2-user@탄력적 IP 주소를 등록하면 됨
> - Port: ssh 접속 포트인 22를 등록한다
> - Connection type: SSH 를 선택한다

사이드바의 Connection -> SSH -> Auth 를 차례로 클랙한 뒤 ppk browse

![](readmeImage/img_76.png)

다시 Session 탭으로 이동하여 Saved Session 에 현재 설정들을 저장할 이름을 등록하고 save

![](readmeImage/img_77.png)


open 을 누른 뒤 ssh 접속 Alert 가 나타나면 accent

![](readmeImage/img_78.png)

SSH 접속 성공!

---

<br/>

#### 6.4 아마존 리눅스 서버 생성 시 곡 해야할 설정들

> - Java 8 설치: 현재 이 프로젝트의 버전은 Java 8 dla
> - 타임존 변경: 기본 서버의 시간은 미국 시간대임. 한국 시간대가 되어야만 우리가 사용하는 시간이 모두 한국 시간으로 등록되고 사용됨
> - 호스트네임 변경: 현재 접속한 서버의 별명을 등로함. 실무에서는 한 대의 서버가 아닌 수십 대의 서버가 작동되는 데, IP 만으로 어떤 서버가 어떤 역할을 하는지 알 수 없음. 이를 구분하기 위해 보통 호스트 네임을 필수로 등록함

<br/>

- Java8 설치

앞의 과정과 동일하게 EC2 서버에 접속한 뒤 다음 과정을 진행할 것!

``sudo yum install -y java-1.8.0-openjdk-devel.x86_64``

![](readmeImage/img_79.png)

Java 버전을 8로 변경

``sudo /usr/sbin/alternatives --config java``

![](readmeImage/img_80.png)

이미 설정되어 있네..

Java 버전 확인 

``java -version``

![](readmeImage/img_81.png)

<br/>

- 타임존 변경

EC2 서버의 기본 타임존은 UTC 임. (세계 표준 시간) 즉, 한국의 시간과는 9시간 차이가 발생하며
서버에서 수행되는 Java 애플리케이션에서 생성되는 시간도 9시간씩 차이가 나기 때문에 KST (한국 시간) 으로 변경한다

<br/>


다음을 차례로 임력


``sudo rm /etc/localtime``

``sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime``

![](readmeImage/img_85.png)

<br/>

- Hostname 변경 

여러 서버를 관리 중일 경우 IP 만으로 어떤 서비스의 서버인지 확인하기 어려움

1. hostnamectl 명령으로 호스트 이름을 설정하여 정규화된 도메인 이름을 반영

``sudo hostnamectl set-hostname freelec-springboot-service``

2. /etc/sysconfig/network 구성 파일을 열고 HOSTNAME 항목을 변경

``sudo vim /etc/sysconfig/network``

![](readmeImage/img_82.png)

I 눌러서 Insert 모드로 전환 뒤, 호스트 네임 입력

![](readmeImage/img_83.png)


ESC 버튼,  :x 입력, Enter 버튼


3. 인스턴스를 재부팅하여 새 호스트 이름 적용

``sudo reboot``

4. 인스턴스에 로그인하고 호스트 이름이 업데이트 되었는지 확인. hostname 명령어 사용

![](readmeImage/img_86.png)

5. 호스트 주소를 찾을 때 가장 먼저 검색해 보는 /etc/hosts 에 변경된 hostname 을 등록

   - /etc/host 파일 오픈
``sudo vim /etc/hosts``
   - HOSTNAME 등록
``127.0.0.1 freelec-springboot-webservice``

![](readmeImage/img_87.png)

6. 호스트 이름이 정상적으로 등록되었는 지 확인 

``curl 등록한 호스트 이름``

- 등록에 실패한 경우

![](readmeImage/img_88.png)

- 등록에 성공한 경우

![](readmeImage/img_89.png)

> 아직 80 포트로 실행된 서비스가 없기 때문에 해당 에러가 발생함

---

<br/>

### 07 AWS 에 데이터베이스 환경을 만들어보자 - AWS RDS

- RDS (Relational Database Service): AWS 에서 지원하는 클라우드 기반 관계형 데이터베이스로 하드웨어 프로비저닝, 데이터베이스 설정, 패치 및 백업과 같이 잦은 운영 작업을 자동화하여 개발자가 개발에 집중할 수 있게 지원하는 서비스 이다.

--- 

<br/>

#### 7.1 RDS 인스턴스 생성하기

![](readmeImage/img_90.png)

![](readmeImage/img_91.png)


> - 특별한 이유가 없다면 MySQL, MariaDB, PostgreSQL 을 사용하는 이유 <br/>
> 1. 상용 데이터베이스인 오라클, MSSQL 이 오픈소스인 MySQL, MariaDB, PostgreSQL 보다 동일한 사양 대비 가격이 더 높다.
> 2. Amazon Aurora 교체 용이성. Amazon Aurora 는 AWS 에서 MySQL 과 PostgreSQL 을 클라우드 기반에 맞게 재구성한 데이터베이스로 공식 자료에 의하면 RDS MySQL 대비 5배, RDS PostgreSQL 보다 3배의 성능을 제공함. 또한 AWS 에서 직접 엔지니어링하고 있기 때문에 계혹해서 발전하고 있다.
> 3. 그러나 Amazon Aurora 는 프리티어 대상이 아니며, 최저 비용이 월 10만원 이상임

- MariaDB의 특징
> 오픈소스로 발전하던 MySQL이 2010년에 썬마이크로시스템즈와 오라클이 합병되면서 많은 MySQL 개발자들이 썬마이크로시스템즈를
> 떠나며 본인만의 프로젝트를 진행하게 됨. 이 중 MySQL 의 창시자인 몬티 와이드니어가 만든 프로젝트가 MariaDB 임
> MySQL을 기반으로 만들어졌기 때문에 쿼리를 비롯한 전반적인 방법은 MySQL 과 유사함 

- MySQL 대비 MariaDB의 장점
> - 동일 하드웨어 사양으로 MySQL 보다 향상된 성능
> - 좀 더 활성화된 커뮤니티
> - 다양한 긴으
> - 다양한 스토리지 엔진

![](readmeImage/img_92.png)

![](readmeImage/img_93.png)

![](readmeImage/img_94.png)

![](readmeImage/img_95.png)

![](readmeImage/img_96.png)

---

<br/>

#### RDS 운영환경에 맞는 파라미터 설정하기

- RDS 설정하기
  - 타임존
  - Character Set
  - Max Connection

<br/>

- 타임존

![](readmeImage/img_97.png)

![](readmeImage/img_98.png)

![](readmeImage/img_99.png)

![](readmeImage/img_115.png)

![](readmeImage/img_100.png)

![](readmeImage/img_101.png)

- Character Set

char 검색해서 나오는 거 전부 utf8mb4 로 변경 (이모지 저장 가능), Collation 항목은 전부 utf8mb4_general_ci 로 변경


- Max Connection

RDS의 Max Connection 은 인스턴스 사양에 따라 자동으로 정해짐.
프리티어 사양으로는 약 60개의 커넥션만 가능해서 좀 더 넉넉한 값으로 지정

![](readmeImage/img_102.png)


#### 생성된 파라미터 그룹을 데이터베이스에 연결

![](readmeImage/img_103.png)

![](readmeImage/img_104.png)

![](readmeImage/img_105.png)

> 수정사항이 반영되는 동안 DB가 작동하지 않을 수 있으므로 예약 시간을 적용하면 주로 새벽시간대에 반영이 됨

---

#### 7.3 내 PC 에서 RDS 에 접속하기

- RDS 의 보안 그룹에 본인 PC의 IP 추가

![](readmeImage/img_106.png)

![](readmeImage/img_107.png)

![](readmeImage/img_109.png)

> - 보안 그룹 첫 번째 줄: 현재 내 PC의 IP를 등록한다
> - 보안 그룹 두 번째 줄: EC2의 보안 그룹을 추가한다
>   - 이렇게 하면 EC2와 RDS 간에 접근이 가능하다
>   - EC2의 경우 이후에 2대 3대가 될 수도 있는데, 매번 IP를 등록할 수는 없으니 보편적으로 이렇게 보안 그룹 간에 연동을 진행함

- Database 플러그인 설치 (로컬에서 테스트)

![](readmeImage/img_110.png)

![](readmeImage/img_111.png)

![](readmeImage/img_112.png)

![](readmeImage/img_113.png)

![](readmeImage/img_114.png)

![](readmeImage/img_116.png)

![](readmeImage/img_117.png)

> 데이터베이스를 선택한다

![](readmeImage/img_118.png)

> 현재의 character_set, collation 설정을 확인한다.

![](readmeImage/img_120.png)

---

- 타임존 확인

```sql
select @@time_zone, now();
```

![](readmeImage/img_119.png)

- 한글명이 잘 들어가는지 간단한 테이블 생성과 insert 쿼리 실행

```sql
CREATE TABLE test (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    content varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

insert into test(content) values ('테스트');

select * from test;
```

![](readmeImage/img_121.png)


---

<br/>

#### 7.4 EC2 에서 RDS 접근 확인

- EC2에 SSH 접속 진행
- 접속 후 MySQL 접근 테스트를 위한 MySQL 설치 ``sudo yum install mysql``
- RDS 접속 ``mysql -u 계정 -p 비밀번호 -h HOST 주소``
- 비밀번호 입력하고 Enter
  - 접속이 성공했거나, 실패 시 실패메시지가 바로 나온다면 RDS 보안그룹이 성공적으로 변경된 것
  - 실패 메세지가 바로 안 나오고 time out 될 때까지 대기한다면 보안그룹 변경이 실패한 것

![](readmeImage/img_122.png)

![](readmeImage/img_123.png)

![](readmeImage/img_124.png)


---

<br/>

### 08 EC2 서버에 프로젝트를 배포해보자

---

#### 8.1 EC2에 프로젝트 Clone 받기

- 깃허브에서 코드를 받아올 수 있게 EC2에 깃 설치

``sudo yum install git``

- 설치가 완료되면 설치 상태 확인

``git --version``

- git clone 으로 프로젝트를 저장할 디렉토리 생성

``mkdir ~/app && mkdir ~/app/step1``

- 생성된 디렉토리로 이동

``cd ~/app/step1``

- 코드가 저장된 깃허브 리포지터리에서 https 주소 복사

![](readmeImage/img_125.png)

- 복사한 https 주소를 통해 git clone 진행

``git clone https://github.com/ImKunYoung/jodu_01_starter.git``

![](readmeImage/img_126.png)

- git clone 이 끝나면 클론된 프로젝트로 이동해서 파일들이 잘 복사되었는지 확인

``cd 프로젝트명``
``ll``

![](readmeImage/img_127.png)

- 코드들이 잘 수행되는지 테스트

``./gradlew test``

- 만일 권한 없다고 나오면

``chmod +x gradlew``

- 테스트가 실패해서 수정하고 깃허브에 푸시를 했다면

``git pull``

![](readmeImage/img_129.png)

> 현재 EC2엔 그레이들을 설치하지 않았으나 Gradle Task (ex: test) 를 수행할 수 있다. 이는 프로젝트 내부에 포함된 gradlew 파일 때문이고,
> 그레이들이 설치되지 않은 환경 혹은 버전이 다른 상황에서도 해당 프로젝트에 한해서 그레이들을 쓸 수 있도록 지원하는 Wrapper 파일 때문임. 해당 파일을
> 직접 이용하기 때문에 별도로 설치할 필요가 없다.

---

### 8.2 배포 스크립트 만들기

배포라 함은 다음의 과정을 모두 포괄하는 의미이다.

1. [x] git clone 혹은 git pull 을 통해 새 버전의 프로젝트 받음
2. [x] Gradle 이나 Maven 을 통해 프로젝트 테스트와 빌드
3. [x] EC2 서버에서 해당 프로젝트 실행 및 재실행

앞선 과정을 배포할 때마다 개발자가 하나하나 명령어를 실행하는 것은 불편함이 많음. 그래서 이를 쉘 스크립트로 작성해 스크립트만 실행하면 
앞의 과정이 차례로 진행되도록 할 것임. 노드 JS 가 .js 라는 파일을 통해 서버에서 작동하는 것처럼 쉘 스크립트 역시 리눅스에서 기본적으로
사용할 수 있는 스크립트 파일의 한 종류임.

<br/>


* ~/app/step1/에 deploy.sh 파일 생성

``vim ~/app/step1/deploy.sh``


```
#!/bin/bash

REPOSITORY=/home/ec2-user/app/step1
PROJECT_NAME=jodu_01_starter

cd $REPOSITORY/$PROJECT_NAME/

echo "> Git Pull"

git pull

echo "> 프로젝트 Build 시작"

./gradlew build

echo "> step1 디렉토리로 이동"

cd $REPOSITORY

echo "> Build 파일 복사"

cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "> 현재 구동중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
 echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
 echo "> kill -15 $CURRENT_PID"
 kill -15 $CURRENT_PID
 sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/ | grep jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

nohup java -jar $REPOSITORY/$JAR_NAME 2>&1 &
```

| 키워드                                                        | 내용                                                                                                                                                                                                                                                                                                  |
|:-----------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| REPOSITORY=/home/ec2-user/app/step1                        | - 프로젝트 디렉토리 주소는 스크립트 내에서 자주 사용하는 값이기 때문에 이를 변수로 저장한다 <br/> - 마찬가지로 PROJECT_NAME=jodu_01_starter도 동일하게 변수로 저장한다 <br/> - 쉘에서는 타입 없이 선언하여 저장한다 <br/> - 쉘에서는 $ 변수명으로 변수를 사용할 수 있다                                                                                                                       |
| cd $REPOSITORY/$PROJECT_NAME/                              | - 제일 처음 git clone 받았던 디렉토리로 이동한다 <br/> - 바로 위의 쉘 변수 설명을 따라 /home/ec2-user/app/step1/freelec-springboot2-webservice 주소로 이동한다                                                                                                                                                                         |
| git pull                                                   | - 디렉토리 이동 후, master 브렌치의 최신 내용을 받는다                                                                                                                                                                                                                                                                 |
| ./gradlew build                                            | - 프로젝트 내부의 gradlew 로 build 를 수행한다                                                                                                                                                                                                                                                                   |
| cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/ | - build의 결과물인 jar 파일을 복사해 jar 파일을 모아둔 위치로 복사한다                                                                                                                                                                                                                                                      |
| CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)              | - 기존에 수행 중이던 스프링 부트 애플리케이션을 종료한다 <br/> - pgrep 은 process id 만 추출하는 명령어이다 <br/> - -f 옵션은 프로세스 이름으로 찾는다                                                                                                                                                                                               |
| if ~ else ~ fi                                             | - 현재 구동 중인 프로세스가 있는지 없는지를 판단해서 기능을 수행한다 <br/> - precess id 값을 보고 프로세스가 있으면 해당 프로세스를 종료한다                                                                                                                                                                                                            |
| JAR_NAME=$(ls -tr $REPOSITORY/ \ grep jar \ tail -n 1)     | - 새로 실행할 jar 파일명을 찾는다 <br/> - 여러 jar 파일이 생기기 때문에 tail -n 로 가장 나중의 jar 파일 (최신 파일)을 변수에 저장한다                                                                                                                                                                                                          |
|nohup java -jar $REPOSITORY/$JAR_NAME 2>&1 &| - 찾은 jar 파일명으로 해당 jar 파일을 nohup 으로 실행한다 <br/> - 스프링 부트의 장점으로 특별히 외장 톰캣을 설치할 필요가 없다 <br/> - 내장 톰캣을 사용해서 jar 파일만 있으면 바로 웹 애플리케이션 서버를 실행할 수 있다 <br/> - 일반적으로 자바를 실행할 때는 java -jar 라는 명령어를 사용하지만, 이렇게 하면 사용자가 터미널 접속을 끊을 때 애플리케이션도 같이 종료된다 - 애플리케이션 실행자가 터미널을 종료해도 애플리케이션은 계속 구동될 수 있도록 nohup 명령어를 사용한다 |

![](readmeImage/img_130.png)

<br/>

* 생성한 스크립트에 실행 권한 추가

```chmod +x ./deploy.sh```

* 권한 추가었는 지 확인

```
ll
```

![](readmeImage/img_131.png)

* 스크립트 실행 

```
./deploy.sh
```

![](readmeImage/img_132.png)


* 잘 실행되었는지 nohup.out 파일을 통해 로그 확인

> nohup.out 은 실행되는 애플리케이션에서 출력되는 모든 내용을 갖고 있음

```
vim nohup.out
``` 

![](readmeImage/img_133.png)

* ERROR

```
Method springSecurityFilterChain in org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration required a bean of type 'org.springframework.security.oauth2.client.registration.ClientRegistrationRepository' that could not be found.
```

---

<br/>

### 8.3 외부 Security 파일 등록하기

* ERROR

```
Method springSecurityFilterChain in org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration required a bean of type 'org.springframework.security.oauth2.client.registration.ClientRegistrationRepository' that could not be found.
```

> ClientRegistrationRepository 를 생성하려면 clientId 와 clientSecret 이 필수임. 로컬 PC 에서 실행할 때는 application-oauth.properties 가 
> 있어 문제가 없었으나 이 파일은 .gitignore 로 git 에서 제외 대상이라 깃허브에 올라가 있지 않음.
> 따라서, 애플리케이션을 실행하기 위해 공개된 저장소에 ClientId 와 ClientSecret 을 올릴 수는 없으니 서버에서 직접 이 설정을 가지고 있게 할 것임.


* step1 이 아닌 app 디렉토리에 properties 파일을 생성.

```
vim /home/ec2-user/app/application-oauth.properties
```

* 생성된 application-oauth.properties 에 로컬 환경에 있는 application-oauth.properties 파일의 내용을 복사 붙여넣기

* 방금 생성한 application-oauth.properties 를 쓰도록 deploy.sh 파일을 수정

```
nohup java -jar \ -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-oauth.properties \ $REPOSITORY/$JAR_NAME 2>&1 &
```

| 키워드                      | 내용                                                                                                                                                                                                                                                          |
|:-------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| -Dspring.config.location | - 스프링 설정 파일 위치를 지정한다 <br/> - 기본 옵션들을 담고 있는 application.properties 과 OAuth 설정들을 담고 있는 application-oauth.properties의 위치를 지정한다 <br/> - classpath 가 붙으면 jar 안에 있는 resouces 디렉토리를 기준으로 경로가 생성된다 <br/> - application-oauth.properties 는 외부에 파일이 있기 때문에 절대경로를 사용한다 |








































|키워드|내용|
|:---|:---|
|||












