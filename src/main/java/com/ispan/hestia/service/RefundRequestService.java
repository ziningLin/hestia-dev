package com.ispan.hestia.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.dto.OrderProviderRefundRequestDTO;
import com.ispan.hestia.dto.OrderUserRefundRequestDTO;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.ProviderRepository;
import com.ispan.hestia.repository.RefundRequestRepository;
import com.ispan.hestia.repository.UserRepository;

@Service
public class RefundRequestService {

    @Autowired
    private RefundRequestRepository refReqRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProviderRepository providerRepo;

    public Integer getOrderIdFromRefReqId(Integer refundReqId) {
        return refReqRepo.findById(refundReqId).get().getOrder().getOrderId();
    }

    @Transactional
    public Page<OrderUserRefundRequestDTO> findRefundRequestUser(Integer userId, Integer currentPage,
            Integer pageSize, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Optional<User> userOp = userRepo.findById(userId);
        if (userOp.isEmpty()) {
            return null;
        }

        return refReqRepo.showUserOrderRefundRequest(userOp.get(), pageable, startDate, endDate);
    }

    @Transactional
    public Page<OrderProviderRefundRequestDTO> activeRefundRequestProvider(Integer providerId, Integer currentPage,
            Integer pageSize, Date startSearchDate, Date endSearchDate) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Optional<Provider> providerOp = providerRepo.findById(providerId);
        if (providerOp.isEmpty()) {
            return null;
        }
        System.out.println("id:" + providerOp.get().getProviderId());
        return refReqRepo.showProviderActiveRefundRequest(providerOp.get(), pageable, startSearchDate, endSearchDate);
    }

    @Transactional
    public Page<OrderProviderRefundRequestDTO> pastRefundRequestProvider(Integer providerId, Integer currentPage,
            Integer pageSize,
            Date startSearchDate, Date endSearchDate) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Optional<Provider> providerOp = providerRepo.findById(providerId);
        if (providerOp.isEmpty()) {
            return null;
        }
        return refReqRepo.showProviderPastRefundRequest(providerOp.get(), pageable, startSearchDate, endSearchDate);
    }

}
