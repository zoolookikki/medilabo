package com.medilabo.note_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class NoteServiceApplication  implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;
    
	public static void main(String[] args) {
		SpringApplication.run(NoteServiceApplication.class, args);
	}
	
    @Override
    public void run(String... args) throws Exception {
        log.info("DB name: {}", mongoTemplate.getDb().getName());
        log.info("Collections: {}", mongoTemplate.getCollectionNames());
    }

}
