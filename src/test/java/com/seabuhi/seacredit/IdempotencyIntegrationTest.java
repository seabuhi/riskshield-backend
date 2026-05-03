package com.seabuhi.seacredit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class IdempotencyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIdempotency() throws Exception {
        String idempotencyKey = UUID.randomUUID().toString();
        
        String signupJson = "{\"username\":\"idemp_user\",\"email\":\"idemp@test.com\",\"password\":\"Pass123!\",\"fullName\":\"Idemp User\"}";

        // First request - MockRedis returns true for setIfAbsent
        mockMvc.perform(post("/auth/signup")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson));

        // Second request - MockRedis returns false for setIfAbsent
        mockMvc.perform(post("/auth/signup")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(status().isConflict());
    }
}
