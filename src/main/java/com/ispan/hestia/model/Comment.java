package com.ispan.hestia.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "comment")
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Integer commentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", referencedColumnName = "room_id")
	private Room room;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "comment_date")
	private Date commentDate = new Date();

	@Column(name = "cleaness_score", nullable = true)
	private Integer cleanessScore;

	@Column(name = "comfort_score", nullable = true)
	private Integer comfortScore;

	@Column(name = "location_score", nullable = true)
	private Integer locationScore;

	@Column(name = "facility_score", nullable = true)
	private Integer facilityScore;

	@Column(name = "pationess_score", nullable = true)
	private Integer pationessScore;

	@Column(name = "recommendation_score")
	private Integer recommendationScore;

	@Column(name = "comment_content")
	private String commentContent;

	@Column(name = "overall_score")
	private Double overallScore;

	@Column(name = "useful")
	private Integer useful;

}
