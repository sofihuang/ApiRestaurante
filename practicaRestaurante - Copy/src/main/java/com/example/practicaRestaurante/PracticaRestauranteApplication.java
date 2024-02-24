package com.example.practicaRestaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//comprobar vaio y nullo de string
		//en caso de no ha podido crear objeto aparece mensaje
		// revisa Httpsatus si está bien
		// controla si no ha creado mesa pero crea reserva

@SpringBootApplication
public class PracticaRestauranteApplication {

	public static void main(String[] args) {
		SpringApplication.run(PracticaRestauranteApplication.class, args);
	}

}
