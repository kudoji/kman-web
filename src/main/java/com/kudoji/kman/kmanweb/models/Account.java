/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
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
    @Setter(AccessLevel.NONE)
    private float balanceInitial;

    @Min(value = 0, message = "Current balance cannot be less than zero")
    @Column(name = "balance_current")
    private float balanceCurrent;

    @NotNull(message = "Currency is invalid")
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Currency currency;

    public Account(Currency currency, float balanceInitial){
        this();

        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        if (balanceInitial < 0f) throw new IllegalArgumentException("Initial balance cannot be less than zero");

        this.currency = currency;
        this.currency.getAccounts().add(this);

        this.balanceInitial = balanceInitial;
        this.balanceCurrent = balanceInitial;
    }

    @Override
    public String toString(){
        return this.name;
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
