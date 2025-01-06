package com.ispan.hestia.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "[user]")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@JsonIgnore
	@Column(name = "email")
	private String email;

	@JsonIgnore
	@Column(name = "password")
	private String password;

	@Column(name = "name")
	private String name;

	@JsonIgnore
	@Column(name = "birthdate")
	private Date birthdate;

	@Column(name = "photo")
	private byte[] photo;

	@JsonIgnore
	@Column(name = "is_provider")
	private boolean isProvider;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "state_id", referencedColumnName = "state_id")
	private State state;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "previous_state_id", referencedColumnName = "state_id")
	private State previousState;

	@JsonIgnore
	@Column(name = "login_attempts")
	private Integer loginAttempts = 0;

	@JsonIgnore
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "login_attempts_last")
	private Date loginAttemptsLast;

	@JsonIgnore
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;

	@JsonIgnore
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_edit_time")
	private Date lastEditTime;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
	@JsonManagedReference
	private Provider provider;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "user")
	private Set<RefundRequest> refundRequest = new HashSet<>();

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<CartRoom> cartRoom = new HashSet<>();

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "user")
	private Set<Order> order = new HashSet<>();

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "user")
	private Set<Favorite> favorite = new HashSet<>();

	public boolean getIsProvider() {
		return isProvider;
	}

	public void setIsProvider(boolean isProvider) {
		this.isProvider = isProvider;
	}
}