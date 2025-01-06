package com.ispan.hestia.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "report")
public class Report implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Integer reportId;

	@Column(name = "reporting_user_id")
	private Integer reportingUserId;

	@Column(name = "reported_user_id")
	private Integer reportedUserId;

	@Column(name = "reported_provider_id")
	private Integer reportedProviderId;

	@Column(name = "reported_room_id")
	private Integer reportedRoomId;

	@Column(name = "reporting_content")
	private String reportingContent;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reporting_time")
	private Date reportingTime;

}
