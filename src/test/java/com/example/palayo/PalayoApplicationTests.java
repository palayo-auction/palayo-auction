package com.example.palayo;

import com.example.palayo.domain.elasticsearch.repository.ItemElasticSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PalayoApplicationTests {

    @MockitoBean
    ItemElasticSearchRepository itemElasticSearchRepository;

    @Test
    void contextLoads() {
    }

}
