package com.marmulasse.bank.account.aggregate;

import com.marmulasse.bank.account.aggregate.events.AccountEvent;
import com.marmulasse.bank.account.aggregate.events.NewAccountCreated;
import com.marmulasse.bank.account.aggregate.events.NewDepositMade;
import com.marmulasse.bank.account.aggregate.events.NewWithdrawMade;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class AccountShould {

    @Test
    public void have_a_zero_balance_when_account_created_as_empty() throws Exception {
        Account account = Account.empty();
        assertThat(account.getBalance()).isEqualTo(Balance.ZERO);
    }

    @Test
    public void have_the_balance_specified_as_input_of_account_creation() throws Exception {
        Account account = Account.with(Balance.of(1.0));
        assertThat(account.getBalance()).isEqualTo(Balance.of(1.0));
    }


    @Test
    public void add_creation_event_when_creating_account() throws Exception {
        Account account = Account.empty();
        assertThat(account.getUncommittedChanges()).containsOnly(new NewAccountCreated(account.getAccountId(), Balance.ZERO));
    }

    @Test
    public void add_amount_to_empty_account_when_deposit_is_made() throws Exception {
        Account account = Account.empty();

        account.deposit(Amount.of(10.0));

        assertThat(account.getBalance()).isEqualTo(Balance.of(10.0));
    }

    @Test
    public void add_amount_to_non_zero_balance_when_deposit_is_made() throws Exception {
        Account account = Account.with(Balance.of(10.0));

        account.deposit(Amount.of(5.0));

        assertThat(account.getBalance()).isEqualTo(Balance.of(15.0));
    }

    @Test
    public void fail_when_deposit_a_negative_amount() throws Exception {
        Account account = Account.empty();

        assertThatThrownBy(() -> account.deposit(Amount.of(-1.0)))
                .hasMessage("A deposit must be positive");
    }

    @Test
    public void fail_when_deposit_a_zero_amount() throws Exception {
        Account account = Account.empty();

        assertThatThrownBy(() -> account.deposit(Amount.of(0.0)))
                .hasMessage("A deposit must be positive");
    }


    @Test
    public void add_deposit_event_to_uncommitted_change_when_deposit_made() throws Exception {
        Account account = Account.empty();

        account.deposit(Amount.of(10.0));

        assertThat(account.getUncommittedChanges()).contains(new NewDepositMade(account.getAccountId(), Amount.of(10.0)));
    }

    @Test
    public void be_initialized_with_past_events() throws Exception {
        AccountId accountId = AccountId.create();
        List<AccountEvent> events = Arrays.asList(
                new NewAccountCreated(accountId, Balance.of(10.0)),
                new NewDepositMade(accountId, Amount.of(10.0))
        );

        Account account = Account.rebuild(events);

        assertThat(account.getBalance()).isEqualTo(Balance.of(20.0));
        assertThat(account.getAccountId()).isEqualTo(accountId);
    }

    @Test
    public void reduce_balance_when_withdraw_amount_from_account() throws Exception {
        Account account = Account.with(Balance.of(15.0));

        account.withdraw(Amount.of(10.0));

        assertThat(account.getBalance()).isEqualTo(Balance.of(5.0));
    }

    @Test
    public void add_withdraw_event_to_uncommitted_change_when_withdraw_made() throws Exception {
        Account account = Account.empty();

        account.withdraw(Amount.of(10.0));

        assertThat(account.getUncommittedChanges()).contains(new NewWithdrawMade(account.getAccountId(), Amount.of(10.0)));
    }

    @Test
    public void fail_when_withdraw_a_negative_amount() throws Exception {
        Account account = Account.empty();

        assertThatThrownBy(() -> account.withdraw(Amount.of(-1.0)))
                .hasMessage("A withdraw must be positive");
    }

    @Test
    public void fail_when_withdraw_a_zero_amount() throws Exception {
        Account account = Account.empty();

        assertThatThrownBy(() -> account.withdraw(Amount.of(0.0)))
                .hasMessage("A withdraw must be positive");
    }
}