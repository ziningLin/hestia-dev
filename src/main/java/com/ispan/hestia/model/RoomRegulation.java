package com.ispan.hestia.model;

import java.io.Serializable;

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
@Entity
@Table(name = "room_regulation")
public class RoomRegulation implements Serializable { // 中介表

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
	private Room room;

	@ManyToOne
	@JoinColumn(name = "room_regulation_id", referencedColumnName = "room_regulation_id", nullable = false)
	private Regulation regulation;
}
