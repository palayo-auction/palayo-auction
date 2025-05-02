package com.example.palayo;

import com.example.palayo.common.util.S3Uploader;
import com.example.palayo.domain.elasticsearch.repository.ItemElasticSearchRepository;
import com.example.palayo.domain.notification.scheduler.RedisNotificationScheduler;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PalayoApplicationTests {
   @MockitoBean
   private ItemElasticSearchRepository itemElasticSearchRepository;

   @MockitoBean
   private S3Uploader s3Uploader;

   @MockitoBean
   private RedissonClient redissonClient;

   @MockitoBean
   private RedisNotificationScheduler redisNotificationScheduler;

   @BeforeAll
   static void loadEnv() {
      Dotenv dotenv = Dotenv.configure()
              .ignoreIfMissing()
              .load();  // .env 파일 로드
      dotenv.entries().forEach(entry ->
              System.setProperty(entry.getKey(), entry.getValue())
      );
   }

   @Test
   void contextLoads() {
   }
}
