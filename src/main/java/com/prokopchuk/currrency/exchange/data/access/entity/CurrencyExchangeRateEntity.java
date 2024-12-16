package com.prokopchuk.currrency.exchange.data.access.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "currency_exchange_rate")
@NoArgsConstructor
@Getter
@Setter
public class CurrencyExchangeRateEntity {

    @EmbeddedId
    private CurrencyExchangeId id = new CurrencyExchangeId();

    @ManyToOne(optional = false)
    @MapsId("baseId")
    private CurrencyEntity base;

    @ManyToOne(optional = false)
    @MapsId("targetId")
    private CurrencyEntity target;

    @Column(nullable = false)
    private BigDecimal rate;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public CurrencyExchangeRateEntity(CurrencyEntity base, CurrencyEntity target, BigDecimal rate) {
        this.base = base;
        this.target = target;
        this.rate = rate;
    }

//    @PrePersist
//    void initIdentifier() {
//        if (id == null || id.getBaseId() == null || id.getTargetId() == null) {
//            this.id = new CurrencyExchangeId(base.getId(), target.getId());
//        }
//    }

//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null) {
//            return false;
//        }
//        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
//          ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
//        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
//          ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
//        if (thisEffectiveClass != oEffectiveClass) {
//            return false;
//        }
//        CurrencyExchangeRateEntity that = (CurrencyExchangeRateEntity) o;
//        return getId() != null && Objects.equals(getId(), that.getId());
//    }
//
//    @Override
//    public final int hashCode() {
//        return Objects.hash(id);
//    }

}
