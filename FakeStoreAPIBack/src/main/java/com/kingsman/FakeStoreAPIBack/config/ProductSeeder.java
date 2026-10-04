package com.kingsman.FakeStoreAPIBack.config;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import com.kingsman.FakeStoreAPIBack.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Carga el catálogo inicial (copia de fakestoreapi.com) al arrancar la aplicación.
// Corre después de que Hibernate creó las tablas, y solo si products está vacía,
// así no se duplican los datos en cada reinicio.
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSeeder implements CommandLineRunner {

    private static final String SEED_SCRIPT = "db/seed/fakestore_products.sql";

    private final ProductRepository productRepository;
    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource(SEED_SCRIPT));
        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(dataSource);

        log.info("Catálogo inicial cargado desde {}", SEED_SCRIPT);
    }
}
