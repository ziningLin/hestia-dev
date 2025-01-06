package com.ispan.hestia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.Regulation;
import com.ispan.hestia.repository.RegulationRepository;

@Service
public class RegulationService {
    @Autowired
    private RegulationRepository regulationRepo;

    /* 查詢全部規定 */
    public List<Regulation> findAllRegulation() {
        return regulationRepo.findAll();
    }

}
