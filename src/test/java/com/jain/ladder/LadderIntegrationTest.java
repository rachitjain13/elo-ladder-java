package com.jain.ladder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LadderIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private LadderController controller;

    @Test
    @DisplayName("Health endpoint reports both postgres and redis true")
    void healthChecksLiveServices() {
        var response = controller.health();
        assertEquals(200, response.getStatusCode().value());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("ok", body.get("status"));
        assertEquals(true, body.get("postgres"));
        assertEquals(true, body.get("redis"));
    }

    @Test
    @DisplayName("Stats endpoint returns player and match counts")
    void statsEndpointWorks() {
        var stats = controller.stats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("total_players"));
        assertTrue(stats.containsKey("total_matches"));
    }
}
