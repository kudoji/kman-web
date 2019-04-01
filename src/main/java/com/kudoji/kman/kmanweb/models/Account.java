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
    private int id;

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

    /**
     *
     * @param currency if null removes prev link
     */
    public void setCurrency(Currency currency){
        if (currency == null){
            if (this.currency != null){
                //  remove prev link
                log.info("remove account '{}' from the currency '{}'", this, this.currency);
                log.info("before {}", this.currency.getAccounts().size());
                this.currency.getAccounts().remove(this);
                log.info("after {}", this.currency.getAccounts().size());
            }

            this.currency = null;
        }else{
            //  in this case nothing to do
            if (this.currency == currency) return;

            if (this.currency != null){
                //  remove link to this account from previous currency object
                this.currency.getAccounts().remove(this);
            }

            this.currency = currency;
            this.currency.getAccounts().add(this);
        }
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

        return (this.id > 0) & (this.id == account.id);
    }

    @PreRemove
    private void preDelete(){
        //  destroy bi-directional link to the currency
        log.info("preDelete() is called for account '{}'", this);

        setCurrency(null);
    }
}
