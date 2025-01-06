package com.ispan.hestia.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.ispan.hestia.model.RoomImages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Integer roomId;
    private String roomName;
    private String roomAddr;
    private Integer roomSize;
    private String roomContent;
    private String roomNotice;
    private Integer doubleBed;
    private Integer singleBed;
    private Integer bedroomCount;
    private Integer bathroom;
    private Double checkinTime;
    private Double checkoutTime;

    private Integer providerId;
    private Integer roomTypeId;
    private Integer cityId;
    private Integer stateId;
    private Integer refundPolicyId;
    private byte[] mainImage;
    private Map<Integer, byte[]> roomImages;

    private Set<Integer> facilityId = new HashSet<>();
    private Set<Integer> regulationId = new HashSet<>();

}
