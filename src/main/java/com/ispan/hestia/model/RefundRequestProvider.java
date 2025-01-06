package com.ispan.hestia.model;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@Entity
@Table(name = "refund_request_provider")
public class RefundRequestProvider {
    @Id
    @Column(name = "refund_request_provider_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer refundRequestProviderId;

    @ManyToOne
    @JoinColumn(name = "refund_request_id", referencedColumnName = "refund_request_id")
    private RefundRequest refundRequest;

    @ManyToOne
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id")
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "state_id", referencedColumnName = "state_id")
    private State state;

    @Column(name = "total_price_refund")
    private Integer totalPriceRefund;
}
