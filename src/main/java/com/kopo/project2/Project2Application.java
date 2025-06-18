// 패키지 선언: 이 파일이 com.kopo.project2라는 패키지(폴더 구조)에 속함을 의미
package com.kopo.project2;

// Spring Boot 애플리케이션 구동을 위한 클래스 및 애노테이션들 import
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// REST API를 만들기 위한 컨트롤러 관련 애노테이션 import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Spring Boot 애플리케이션의 진입점임을 나타냄
@SpringBootApplication

// 해당 클래스가 REST API 요청을 처리하는 컨트롤러 역할도 한다는 표시
@RestController
public class Project2Application {

	// 애플리케이션 시작 지점: 자바에서 실행되는 main 메서드
	public static void main(String[] args) {
		// Spring Boot 내장 서버(Tomcat 등)를 구동시키고 이 클래스를 기반으로 프로젝트 시작
		SpringApplication.run(Project2Application.class, args);
	}

	// GET 방식으로 "/hello" 요청이 들어오면 이 메서드가 실행됨
	@GetMapping("/hello")
	public String hello(
			// URL에 ?name=값 을 전달받을 때 해당 값을 name 변수에 매핑함 (기본값: World)
			@RequestParam(value = "name", defaultValue = "World") String name) {
		// name 값으로 메시지 반환 (예: /hello?name=T → Hello? T!)
		return String.format("Hello? %s!", name);
	}
}
