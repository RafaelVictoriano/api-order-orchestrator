package com.br.apiorderorchestrator;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootApplication
@EnableFeignClients
public class ApiOrderOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiOrderOrchestratorApplication.class, args);

		//start mock server
		WireMockServer wireMockServer = new WireMockServer( 8989);
		wireMockServer.start();
		configureFor("localhost", 8989);
		stubFor(post(urlPathEqualTo("/encumber")).willReturn(aResponse().withBody("Hello, i am mock!")));
	}

}
