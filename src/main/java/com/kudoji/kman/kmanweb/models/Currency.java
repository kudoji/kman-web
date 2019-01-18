/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@Entity
@Table(name = "currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "Name is invalid")
    @Size(min = 5, max = 35, message = "Name must be from 5 to 35 characters long")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Currency code is not set")
    @Size(min = 2, max = 5, message = "Code must be from 2 to 5 characters long")
    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "starts_with_code")
    private boolean startsWithCode;

    @Min(value = 0, message = "Rate must be more than 0.0")
    private float rate;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.PERSIST)
    @Setter(AccessLevel.NONE)
    private Set<Account> accounts = new HashSet<>();

    public Currency(String name){
        this();

        this.name = name;
    }

    @Override
    public String toString(){
        return "" + this.name + " (" + this.code + ") #" + this.id;
    }

    @Override
    public int hashCode(){
        return id + 7 * code.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Currency)) return false;

        Currency currency = (Currency)obj;

        return ((this.id == currency.id) & (this.code == currency.code));
    }
}
