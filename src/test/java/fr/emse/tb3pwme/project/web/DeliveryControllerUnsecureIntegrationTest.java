package fr.emse.tb3pwme.project.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.emse.tb3pwme.project.domain.Status;
import fr.emse.tb3pwme.project.persistence.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(DeliveryControllerUnsecureIntegrationTest.NoSecurityConfig.class)
@ActiveProfiles("dev")
class DeliveryControllerUnsecureIntegrationTest {

    @TestConfiguration
    static class NoSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(c -> c.requestMatchers("/api/**").permitAll());
            return http.build();
        }

    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryRepository repository;

    @BeforeEach
    void emptyDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldGetDeliveries() throws Exception {
        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void shouldGetDelivery() throws Exception {
        String location = createDelivery();
        String deliveryJson = mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DeliveryRepresentation delivery = objectMapper.readValue(deliveryJson, DeliveryRepresentation.class);
        assertThat(delivery.id()).isNotNull();
    }

    @Test
    void shouldRescheduleDelivery() throws Exception {
        String location = createDelivery();
        LocalDate tomorrow = LocalDate.now().plusDays(1L);
        RescheduleRequestRepresentation rescheduleRequest = new RescheduleRequestRepresentation(tomorrow);
        String rescheduleRequestJson = objectMapper.writeValueAsString(rescheduleRequest);
        String deliveryJson = mockMvc.perform(post(location + "/reschedule")
                        .content(rescheduleRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DeliveryRepresentation delivery = objectMapper.readValue(deliveryJson, DeliveryRepresentation.class);
        assertThat(delivery.rescheduleAllowed()).isFalse();
        mockMvc.perform(post(location + "/reschedule")
                .content(rescheduleRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldCompleteDelivery() throws Exception {
        String location = createDelivery();
        String deliveryJson = mockMvc.perform(post(location + "/complete"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DeliveryRepresentation delivery = objectMapper.readValue(deliveryJson, DeliveryRepresentation.class);
        assertThat(delivery.status()).isEqualTo(Status.DELIVERED);
    }

    private String createDelivery() throws Exception {
        DeliveryToCreateRepresentation deliveryToCreate = deliveryToCreate();
        String deliveryToCreateJson = objectMapper.writeValueAsString(deliveryToCreate);
        return mockMvc.perform(post("/api/deliveries")
                        .content(deliveryToCreateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, notNullValue()))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.LOCATION);
    }

    private DeliveryToCreateRepresentation deliveryToCreate() {
        return new DeliveryToCreateRepresentation(LocalDate.now(),
                "John Doe", "14465 Mulholland Drive", "Los Angeles", "CA 90077", "United States");
    }

}
