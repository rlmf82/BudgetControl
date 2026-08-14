package rlmf.java.budgetcontrol.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void get_withExistingId_returns200WithCategory() throws Exception {
        Long foodCategoryId = findCategoryId("Food");

        mockMvc.perform(get("/api/categories/{id}", foodCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(foodCategoryId))
                .andExpect(jsonPath("$.name").value("Food"));
    }

    @Test
    void get_withUnknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", 999999L))
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
}
