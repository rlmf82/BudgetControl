package rlmf.java.budgetcontrol.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import rlmf.java.budgetcontrol.application.OperationService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OperationControllerTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_withValidPayload_returns201WithSavedOperation() throws Exception {
        Long foodCategoryId = findCategoryId("Food");

        mockMvc.perform(post("/api/operations")
                        .contentType("application/json")
                        .content(operationJson("PAYMENT", "45.90", "groceries", foodCategoryId, null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PAYMENT"))
                .andExpect(jsonPath("$.amount").value(45.90))
                .andExpect(jsonPath("$.description").value("groceries"))
                .andExpect(jsonPath("$.categoryName").value("Food"))
                .andExpect(jsonPath("$.date").value(LocalDate.now(OperationService.PORTUGAL_ZONE).format(DATE_FORMAT)));
    }

    @Test
    void create_withZeroAmount_returns400() throws Exception {
        Long categoryId = findCategoryId("Food");

        mockMvc.perform(post("/api/operations")
                        .contentType("application/json")
                        .content(operationJson("PAYMENT", "0", "groceries", categoryId, null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withFutureDate_returns400() throws Exception {
        Long categoryId = findCategoryId("Salary");
        String tomorrow = LocalDate.now(OperationService.PORTUGAL_ZONE).plusDays(1).format(DATE_FORMAT);

        mockMvc.perform(post("/api/operations")
                        .contentType("application/json")
                        .content(operationJson("INCOME", "100", "advance", categoryId, tomorrow)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withUnknownCategory_returns404() throws Exception {
        mockMvc.perform(post("/api/operations")
                        .contentType("application/json")
                        .content(operationJson("PAYMENT", "10", "misc", 999999L, null)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateThenDelete_roundTrip() throws Exception {
        Long categoryId = findCategoryId("Food");

        String createResponse = mockMvc.perform(post("/api/operations")
                        .contentType("application/json")
                        .content(operationJson("PAYMENT", "100", "initial", categoryId, null)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(put("/api/operations/{id}", id)
                        .contentType("application/json")
                        .content(operationJson("PAYMENT", "80", "updated", categoryId, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(80));

        mockMvc.perform(delete("/api/operations/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/operations/{id}", id))
                .andExpect(status().isNotFound());
    }

    private Long findCategoryId(String name) throws Exception {
        String response = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        for (JsonNode node : objectMapper.readTree(response)) {
            if (node.get("name").asText().equals(name)) {
                return node.get("id").asLong();
            }
        }
        throw new IllegalStateException("Category not found: " + name);
    }

    private String operationJson(String type, String amount, String description, Long categoryId, String date) throws Exception {
        var node = objectMapper.createObjectNode();
        node.put("type", type);
        node.put("amount", new BigDecimal(amount));
        node.put("description", description);
        node.put("categoryId", categoryId);
        if (date != null) {
            node.put("date", date);
        }
        return objectMapper.writeValueAsString(node);
    }
}
