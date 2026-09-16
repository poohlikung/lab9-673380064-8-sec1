package com.example.lab9;

import com.example.lab9.model.Account;
import com.example.lab9.repository.AccountRepository;
import com.example.lab9.repository.DepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DepositRepository depositRepository;

    @BeforeEach
    void cleanDatabase() {
        depositRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void createsReadsAndDepositsIntoAccount() throws Exception {
        String location = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountNumber": "1234567890",
                                  "ownerName": "John",
                                  "balance": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is("1234567890")))
                .andExpect(jsonPath("$.balance", is(0.0)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(location)
                .get("id")
                .asLong();

        mockMvc.perform(post("/accounts/{id}/deposit", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Deposit successful")));

        mockMvc.perform(get("/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(1000.0)));

        org.assertj.core.api.Assertions.assertThat(depositRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidDepositWithoutChangingData() throws Exception {
        Account account = accountRepository.save(new Account("9876543210", "Jane", 100.0));

        mockMvc.perform(post("/accounts/{id}/deposit", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":0}"))
                .andExpect(status().isBadRequest());

        org.assertj.core.api.Assertions.assertThat(
                accountRepository.findById(account.getId()).orElseThrow().getBalance()
        ).isEqualTo(100.0);
        org.assertj.core.api.Assertions.assertThat(depositRepository.count()).isZero();
    }

    @Test
    void returnsNotFoundForUnknownAccount() throws Exception {
        mockMvc.perform(get("/accounts/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details.message", is("Account with id 999999 was not found")));
    }
}
