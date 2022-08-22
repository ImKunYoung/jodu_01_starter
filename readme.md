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

#### 2.3 롬복 소개

> 롬복이란? 개발 시 자주 사용하는 Getter, Setter, 기본생성자, toString 등을 어노테이션으로 자동 생성해줌

     implementation 'org.projectlombok:lombok:'

프로젝트마다 build.gradle 라이브러리 추가, Enable annotation processing 체크하기

<br/>


























