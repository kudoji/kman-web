/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.*;

@Slf4j
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;

    @NotNull(message = "Name is invalid")
    @Size(min = 5, max = 35, message = "Name must be from 5 to 35 characters long")
    @Column(nullable = false)
    private String name;

    @Min(value = 0, message = "Initial balance cannot be less than zero")
    @Column(name = "balance_initial")
    private float balanceInitial;

    @Min(value = 0, message = "Current balance cannot be less than zero")
    @Column(name = "balance_current")
    private float balanceCurrent;

    @NotNull(message = "Currency is invalid")
    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;

    public Account(Currency currency, float balanceInitial){
        this();

        log.info("account constructor is called. currency '{}', balanceInitial: {}", currency, balanceInitial);

        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        if (balanceInitial < 0f) throw new IllegalArgumentException("Initial balance cannot be less than zero");

        this.currency = currency;
        this.currency.getAccounts().add(this);

        this.balanceInitial = balanceInitial;
        this.balanceCurrent = balanceInitial;
    }

    public void setBalanceInitial(float balanceInitial){
        if (this.balanceInitial < 0f) throw new IllegalArgumentException("Initial balance cannot be less than zero");

        if (this.balanceInitial > 0f) throw new IllegalArgumentException("Initial balance is already set");

        this.balanceInitial = balanceInitial;
        this.balanceCurrent = balanceInitial;
    }

    public void setCurrency(Currency currency){
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");

        if (this.currency == currency) return;

        if (this.currency != null){
            //  remove link to this account from previous currency object
            this.currency.getAccounts().remove(this);
        }

        this.currency = currency;
        this.currency.getAccounts().add(this);
    }

    @Override
    public String toString(){
        return "" + this.name + " #" + this.id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Account)) return false;

        Account account = (Account)obj;

        return this.id == account.id;
    }
}
