package com.example.lab9;

import com.example.lab9.model.Account;
import com.example.lab9.repository.AccountRepository;
import com.example.lab9.repository.DepositRepository;
import com.example.lab9.service.DepositService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DepositTransactionIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private DepositService depositService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        depositRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void rollsBackBalanceWhenSavingDepositHistoryFails() {
        Account account = accountRepository.save(new Account("1111222233", "Rollback Test", 500.0));

        jdbcTemplate.execute("""
                alter table deposit_transaction
                add constraint chk_test_rejected_amount check (amount <> 250)
                """);

        try {
            assertThatThrownBy(() -> depositService.deposit(account.getId(), 250.0))
                    .isInstanceOf(DataIntegrityViolationException.class);
        } finally {
            jdbcTemplate.execute("alter table deposit_transaction drop constraint chk_test_rejected_amount");
        }

        Account unchanged = accountRepository.findById(account.getId()).orElseThrow();
        assertThat(unchanged.getBalance()).isEqualTo(500.0);
        assertThat(depositRepository.count()).isZero();
    }
}
