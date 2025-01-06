package com.ispan.hestia.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.RegulationDTO;
import com.ispan.hestia.service.RegulationService;

@RestController
@RequestMapping("/regulation")
@CrossOrigin
public class RegulationController {
    @Autowired
    private RegulationService regulationService;

    /* 列出所有規定：含 regulation id */
    @GetMapping("/all")
    public List<RegulationDTO> getAllRegulation() {
        List<RegulationDTO> regulations = regulationService.findAllRegulation().stream()
                .map(regulation -> new RegulationDTO(regulation.getRoomRegulationId(),
                        regulation.getRoomRegulationName()))
                .collect(Collectors.toList());
        return regulations;
    }

}
