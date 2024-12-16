package com.prokopchuk.currrency.exchange.data.access.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyExchangeId implements Serializable {

    @Column(name = "base_id", updatable = false, nullable = false)
    private Integer baseId;

    @Column(name = "target_id", updatable = false, nullable = false)
    private Integer targetId;
}
