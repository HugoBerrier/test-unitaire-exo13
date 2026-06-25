package com.example.bank.bdd;

import com.example.bank.model.Account;
import com.example.bank.repository.InMemoryAccountRepository;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryAccountRepository accountRepository;

    private MvcResult lastResult;

    @Before
    public void reset() {
        accountRepository.clear();
        lastResult = null;
    }

    @Given("no accounts exist")
    public void noAccountsExist() {
        accountRepository.clear();
    }

    @Given("an account exists with number {string} and holder {string} and balance {double}")
    public void anAccountExists(String number, String holder, double balance) {
        Account account = new Account(number, holder, balance);
        accountRepository.save(account);
    }

    @When("I create an account with number {string} and holder {string}")
    public void iCreateAnAccount(String number, String holder) throws Exception {
        lastResult = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"number":"%s","holder":"%s"}
                                """.formatted(number, holder)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @When("I deposit {double} on account {string}")
    public void iDepositOnAccount(double amount, String number) throws Exception {
        lastResult = mockMvc.perform(post("/accounts/" + number + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":%s}
                                """.formatted(amount)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @When("I withdraw {double} from account {string}")
    public void iWithdrawFromAccount(double amount, String number) throws Exception {
        lastResult = mockMvc.perform(post("/accounts/" + number + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":%s}
                                """.formatted(amount)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @When("I try to withdraw {double} from account {string}")
    public void iTryToWithdrawFromAccount(double amount, String number) throws Exception {
        lastResult = mockMvc.perform(post("/accounts/" + number + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":%s}
                                """.formatted(amount)))
                .andReturn();
    }

    @When("I transfer {double} from account {string} to account {string}")
    public void iTransferBetweenAccounts(double amount, String fromNumber, String toNumber) throws Exception {
        lastResult = mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromNumber":"%s","toNumber":"%s","amount":%s}
                                """.formatted(fromNumber, toNumber, amount)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @When("I try to transfer {double} from account {string} to account {string}")
    public void iTryToTransferBetweenAccounts(double amount, String fromNumber, String toNumber) throws Exception {
        lastResult = mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromNumber":"%s","toNumber":"%s","amount":%s}
                                """.formatted(fromNumber, toNumber, amount)))
                .andReturn();
    }

    @Then("the account is created with balance {double}")
    public void theAccountIsCreatedWithBalance(double balance) throws Exception {
        assertEquals(201, lastResult.getResponse().getStatus());
        double actualBalance = JsonPath.read(lastResult.getResponse().getContentAsString(), "$.balance");
        assertEquals(balance, actualBalance);
    }

    @Then("the account {string} has balance {double}")
    public void theAccountHasBalance(String number, double balance) throws Exception {
        MvcResult result = mockMvc.perform(get("/accounts/" + number))
                .andExpect(status().isOk())
                .andReturn();
        double actualBalance = JsonPath.read(result.getResponse().getContentAsString(), "$.balance");
        assertEquals(balance, actualBalance);
    }

    @Then("the withdrawal is rejected")
    public void theWithdrawalIsRejected() {
        assertEquals(400, lastResult.getResponse().getStatus());
    }

    @Then("the transfer is rejected")
    public void theTransferIsRejected() {
        assertEquals(400, lastResult.getResponse().getStatus());
    }
}
