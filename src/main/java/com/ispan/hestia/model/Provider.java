package com.ispan.hestia.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "provider")
public class Provider implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "provider_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer providerId;

	@Column(name = "bank_account")
	private String bankAccount;

	@Column(name = "address")
	private String address;

	@ManyToOne
	@JoinColumn(name = "state_id", referencedColumnName = "state_id")
	private State state;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	@JsonBackReference
	private User user;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "provider")
	private Set<Room> room = new HashSet<>();

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "provider")
	private Set<RefundRequestProvider> RefundRequestProvider = new HashSet<>();

}
