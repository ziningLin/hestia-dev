package com.ispan.hestia.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
@Entity
@Table(name = "order_details")
public class OrderDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_room_id")
	private Integer orderRoomId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne
	@JoinColumn(name = "room_available_date_id", referencedColumnName = "room_available_date_id")
	private RoomAvailableDate roomAvailableDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "check_in_date")
	private Date checkInDate;

	@Column(name = "purchased_price")
	private Integer purchasedPrice;

	@Column(name = "active_refund_request")
	private Integer activeRefundRequest;

	@ManyToOne
	@JoinColumn(name = "state_id", referencedColumnName = "state_id")
	private State state;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "orderDetails", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<OrderDetailsRefundRecord> orderDetailsRefundRecord = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "orderDetails")
	private Set<RefundRequest> refundRequest = new HashSet<>();

}
