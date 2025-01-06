package com.ispan.hestia.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_details_refund_record")
public class OrderDetailsRefundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_details_refund_record_id")
    private Integer orderDetailsRefundRecordId;

    @ManyToOne
    @JoinColumn(name = "order_room_id", referencedColumnName = "order_room_id")
    private OrderDetails orderDetails;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 加入時間部分
    @Temporal(TemporalType.TIMESTAMP) // 設定為時間戳
    @Column(name = "[date]")
    private Date date;

}
