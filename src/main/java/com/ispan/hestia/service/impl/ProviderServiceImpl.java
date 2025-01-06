package com.ispan.hestia.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.dto.ProviderInfoDTO;
import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.exception.EntityNotFoundException;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.ProviderRepository;
import com.ispan.hestia.repository.UserRepository;
import com.ispan.hestia.service.ProviderService;



@Service
public class ProviderServiceImpl implements ProviderService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ProviderRepository providerRepo;

	/**
	 * 註冊房東資格
	 *@param
	 *@return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProviderInfoDTO register(Integer userId,ProviderInfoDTO dto) {
		User dbUser = isProvider(userId, false);
		dbUser.setIsProvider(true);
		userRepo.save(dbUser);
		
		Provider provider = ProviderInfoDTO.fromProviderInfoDTO(dto);
		provider.setUser(dbUser);
		
		Provider save = providerRepo.save(provider);
		return ProviderInfoDTO.fromProvider(save);
	}

	/**
	 * 獲得房東資料
	 *@param
	 *@return
	 */
	@Override
	public ProviderInfoDTO getProviderInfo(Integer userId) {
		User user = isProvider(userId, true);
		Provider provider = user.getProvider();
		if(provider == null) {
			throw new EntityNotFoundException(Provider.class,"userId",userId.toString());
		}
		ProviderInfoDTO dto = ProviderInfoDTO.fromProvider(provider);
		return dto;
	}

	/**
	 * 修改房東資料
	 *@param
	 *@return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProviderInfoDTO updateProviderInfo(Integer userId, ProviderInfoDTO dto) {
		User user = isProvider(userId, true);
		Provider dbProvider = user.getProvider();
		if(dbProvider == null) {
			throw new EntityNotFoundException(Provider.class,"userId",userId.toString());
		}
		if (dto.getBankAccount() != null) {
	        dbProvider.setBankAccount(dto.getBankAccount());
	    }
	    if (dto.getAddress() != null) {
	        dbProvider.setAddress(dto.getAddress());
	    }
		Provider save = providerRepo.save(dbProvider);
		return ProviderInfoDTO.fromProvider(save);
	}	

	/**
	 * 判斷是否為房東
	 * 
	 * @param userId
	 * @param shouldBeProvider true=需要是房東才繼續，false=需要不是房東才繼續
	 * @return
	 */
	public User isProvider(Integer userId, boolean shouldBeProvider) {
		Optional<User> op = userRepo.findById(userId);
		if(op.isEmpty()) {
			throw new EntityNotFoundException(User.class,"userId",userId.toString());
		}
		User user = op.get();
		// 需要是房東但用戶不是房東
		if (shouldBeProvider && !user.getIsProvider()) {
			throw new BadRequestException("該使用者不是房東");
			// 不應該是房東但用戶卻是房東
		} else if (!shouldBeProvider && user.getIsProvider()) {
			throw new BadRequestException("該使用者已經是房東");
		}
		return user;
	}

	/* 檢查 provider 是否存在 */
	public boolean exists(Integer id) {
		if (id != null) {
			System.out.println(providerRepo.existsById(id));
			return providerRepo.existsById(id);
		}
		return false;
	}
}
