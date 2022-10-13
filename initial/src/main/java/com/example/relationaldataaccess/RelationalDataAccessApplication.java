package com.example.relationaldataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class RelationalDataAccessApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(RelationalDataAccessApplication.class);

	public static void main(String args[]) {
		SpringApplication.run(RelationalDataAccessApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... strings) throws Exception {

		log.info("Creation des tables");

		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(" +
				"id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		// on divise le tableau des noms entiers en un tableau de noms et prénoms.
		List<Object[]> splitUpNames = Arrays.asList("Abdou Ndiaye", "Samba Diop", "Abdou Diallo ", "Awa Sy").stream()
				.map(name -> name.split(" "))
				.collect(Collectors.toList());

		// on utilise un flux Java 8 pour obtenir chaque tuple de la liste.
		splitUpNames.forEach(name -> log.info(String.format("Ajout des informations du client: %s %s", name[0], name[1])));

		// on utilise la methode batchUpdate de JdbcTemplate pour inserer des données en masse.
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		log.info("Information de clients tel que first_name = 'Abdou':");
		jdbcTemplate.query(
				"SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
				new Object[] { "Abdou" },
				(rs, rowNum) -> new Customer(rs.getLong("id"),
											rs.getString("first_name"),
											rs.getString("last_name"))
		).forEach(customer -> log.info(customer.toString()));
	}
}