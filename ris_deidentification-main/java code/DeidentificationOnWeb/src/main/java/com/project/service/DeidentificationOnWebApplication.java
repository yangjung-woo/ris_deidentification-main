package com.project.service;

import com.opencsv.exceptions.CsvException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"com.project.controller"})
public class DeidentificationOnWebApplication {

	public static void main(String[] args) throws IOException, CsvException {

		SpringApplication.run(DeidentificationOnWebApplication.class, args);
	}

}
