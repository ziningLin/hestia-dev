package com.ispan.hestia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.ProviderInfoDTO;
import com.ispan.hestia.service.ProviderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 房東接口
 */
@RestController
@RequestMapping("/provider")
@CrossOrigin
public class ProviderController {
	
	@Autowired
	private ProviderService providerService;
	
	/**
	 * 註冊房東資格接口
	 * @param httpsession
	 * @return
	 */
	@PostMapping("/")
	public ResponseEntity<ProviderInfoDTO> register(@Valid @RequestBody ProviderInfoDTO dto, HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		return ResponseEntity.ok(providerService.register(userId,dto));
	}
	
	/**
	 * 房東資料接口
	 * @param httpsession
	 * @return
	 */
	@GetMapping("/")
	public ResponseEntity<ProviderInfoDTO> getProviderInfo(HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		return ResponseEntity.ok(providerService.getProviderInfo(userId));
	}
	
	/**
	 * 修改房東資料接口
	 * @param httpsession
	 * @return
	 */
	@PutMapping("/")
	public ResponseEntity<ProviderInfoDTO> updateProviderInfo(@Valid @RequestBody ProviderInfoDTO dto,
			HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		return ResponseEntity.ok(providerService.updateProviderInfo(userId,dto));
	}
}
