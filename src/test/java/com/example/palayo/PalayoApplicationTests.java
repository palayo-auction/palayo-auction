package com.example.palayo;

import com.example.palayo.domain.elasticsearch.repository.ItemElasticSearchRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PalayoApplicationTests {

    @MockitoBean
    private ItemElasticSearchRepository itemElasticSearchRepository;

    @BeforeAll
    static void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            System.err.println("Warning: .env file not found. Skipping...");
        }
    }

    @Test
    void contextLoads() {
    }

}
