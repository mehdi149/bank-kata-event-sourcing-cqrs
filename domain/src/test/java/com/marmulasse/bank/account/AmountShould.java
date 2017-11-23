package com.marmulasse.bank.account;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AmountShould {

    @Test
    public void add_two_amount() throws Exception {
        Amount amount1 = Amount.of(0.0);
        Amount amount2 = Amount.of(1.0);
        assertThat(amount1.add(amount2)).isEqualTo(Amount.of(1.0));
    }
}