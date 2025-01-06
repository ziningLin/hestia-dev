package com.ispan.hestia.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@Entity
@Table(name = "refund_request")
public class RefundRequest {
    @Id
    @Column(name = "refund_request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer refundRequestId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "order_room_id", referencedColumnName = "order_room_id")
    private OrderDetails orderDetails;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "total_price_refund")
    private Integer totalPriceRefund;

    @ManyToOne
    @JoinColumn(name = "state_id", referencedColumnName = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "refundRequest", fetch = FetchType.LAZY)
    private Set<RefundRequestProvider> refundRequestProvider = new HashSet<>();

}
