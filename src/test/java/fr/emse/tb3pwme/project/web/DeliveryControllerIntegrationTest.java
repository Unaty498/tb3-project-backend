package fr.emse.tb3pwme.project.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.emse.tb3pwme.project.domain.Status;
import fr.emse.tb3pwme.project.persistence.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerIntegrationTest {

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
    void shouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/deliveries")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/deliveries/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/deliveries/" + UUID.randomUUID() + "/reschedule")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/deliveries/" + UUID.randomUUID() + "/complete")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetDeliveries() throws Exception {
        mockMvc.perform(get("/api/deliveries")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void shouldGetDelivery() throws Exception {
        String location = createDelivery();
        String deliveryJson = mockMvc.perform(get(location)
                        .with(jwt()))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DeliveryRepresentation delivery = objectMapper.readValue(deliveryJson, DeliveryRepresentation.class);
        assertThat(delivery.rescheduleAllowed()).isFalse();
        mockMvc.perform(post(location + "/reschedule")
                        .content(rescheduleRequestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt()))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldBeForbidden() throws Exception {
        String location = createDelivery();
        mockMvc.perform(post(location + "/complete")
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCompleteDelivery() throws Exception {
        String location = createDelivery();
        String deliveryJson = mockMvc.perform(post(location + "/complete")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_POSTMAN"))))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt()))
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
