package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class DemoApplication {

  @Value("${NAME:World}")
  String name;

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }
}
