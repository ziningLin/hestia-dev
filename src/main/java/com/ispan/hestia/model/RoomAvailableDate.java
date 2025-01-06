package com.ispan.hestia.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "room_available_date")
public class RoomAvailableDate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_available_date_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "room_id", referencedColumnName = "room_id")
	private Room room;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "available_dates")
	private Date availableDates;

	@Column(name = "price")
	private Integer price;

	@Column(name = "room_sum")
	private Integer roomSum;

	@ManyToOne
	@JoinColumn(name = "state_id", referencedColumnName = "state_id")
	private State state;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "latest_booking_date")
	private Date latestBookingDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "release_date")
	private Date releaseDate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "roomAvailableDate", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private Set<CartRoom> cartRoom = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "roomAvailableDate", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private Set<OrderDetails> orderDetails;
}
