package rlmf.java.budgetcontrol.adapter.in.web;

import org.junit.jupiter.api.Nested;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class MonthlyOrCustomRangeReport {

        @Test
        void theOneWhere_userRequestsAugustReport_seesCategoryTotalsAndOverallBalance() throws Exception {
            createOperation("PAYMENT", "300", "groceries", "Food", "05/08/2024");
            createOperation("PAYMENT", "800", "rent", "Rent", "01/08/2024");
            createOperation("INCOME", "2500", "salary", "Salary", "01/08/2024");

            mockMvc.perform(get("/api/reports/monthly").param("year", "2024").param("month", "8"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.startDate").value("01/08/2024"))
                    .andExpect(jsonPath("$.endDate").value("31/08/2024"))
                    .andExpect(jsonPath("$.totalIncome").value(2500))
                    .andExpect(jsonPath("$.totalPayments").value(1100))
                    .andExpect(jsonPath("$.balance").value(1400))
                    .andExpect(jsonPath("$.categories[?(@.categoryName == 'Food')].totalPayments").value(300))
                    .andExpect(jsonPath("$.categories[?(@.categoryName == 'Rent')].totalPayments").value(800))
                    .andExpect(jsonPath("$.categories[?(@.categoryName == 'Salary')].totalIncome").value(2500));
        }

        @Test
        void theOneWhere_userRequestsCustomRange_receivesTotalsForJustThatWindow() throws Exception {
            createOperation("PAYMENT", "50", "inside range", "Food", "12/08/2024");
            createOperation("PAYMENT", "999", "outside range", "Food", "25/08/2024");

            mockMvc.perform(get("/api/reports/custom")
                            .param("startDate", "10/08/2024")
                            .param("endDate", "20/08/2024"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.startDate").value("10/08/2024"))
                    .andExpect(jsonPath("$.endDate").value("20/08/2024"))
                    .andExpect(jsonPath("$.totalPayments").value(50));
        }

        @Test
        void theOneWhere_categoryWithNoOperationsInPeriod_isOmittedNotAnError() throws Exception {
            createOperation("PAYMENT", "300", "groceries", "Food", "05/08/2024");

            mockMvc.perform(get("/api/reports/monthly").param("year", "2024").param("month", "8"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.categories.length()").value(1))
                    .andExpect(jsonPath("$.categories[0].categoryName").value("Food"));
        }

        @Test
        void theOneWhere_customRangeCrossesYearBoundary_isRejected() throws Exception {
            mockMvc.perform(get("/api/reports/custom")
                            .param("startDate", "15/12/2024")
                            .param("endDate", "15/01/2025"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class YearlyReport {

        @Test
        void theOneWhere_userRequestsPastYear_seesAllTwelveMonthsWithAnnualTotals() throws Exception {
            createOperation("INCOME", "1000", "salary", "Salary", "15/01/2023");
            createOperation("PAYMENT", "200", "groceries", "Food", "15/06/2023");

            mockMvc.perform(get("/api/reports/yearly").param("year", "2023"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.year").value(2023))
                    .andExpect(jsonPath("$.months.length()").value(12))
                    .andExpect(jsonPath("$.totalIncome").value(1000))
                    .andExpect(jsonPath("$.totalPayments").value(200))
                    .andExpect(jsonPath("$.balance").value(800));
        }

        @Test
        void theOneWhere_userRequestsCurrentYear_seesOnlyMonthsElapsedSoFar() throws Exception {
            var today = LocalDate.now(OperationService.PORTUGAL_ZONE);
            createOperation("INCOME", "10", "misc", "Salary", "01/01/" + today.getYear());

            mockMvc.perform(get("/api/reports/yearly").param("year", String.valueOf(today.getYear())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.months.length()").value(today.getMonthValue()));
        }

        @Test
        void theOneWhere_yearWithNoRecordedOperations_showsAllZerosInsteadOfError() throws Exception {
            mockMvc.perform(get("/api/reports/yearly").param("year", "1999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.months.length()").value(12))
                    .andExpect(jsonPath("$.totalIncome").value(0))
                    .andExpect(jsonPath("$.totalPayments").value(0))
                    .andExpect(jsonPath("$.balance").value(0));
        }
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

    private void createOperation(String type, String amount, String description, String categoryName, String date) throws Exception {
        Long categoryId = findCategoryId(categoryName);
        var node = objectMapper.createObjectNode();
        node.put("type", type);
        node.put("amount", new BigDecimal(amount));
        node.put("description", description);
        node.put("categoryId", categoryId);
        node.put("date", date);

        mockMvc.perform(post("/api/operations")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isCreated());
    }
}
