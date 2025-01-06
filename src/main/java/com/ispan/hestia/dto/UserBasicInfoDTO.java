package com.ispan.hestia.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ispan.hestia.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 *  用於傳輸用戶基本資料
 */
@Getter
@Setter
@NoArgsConstructor
public class UserBasicInfoDTO {

	private Integer userId;
	
	private String email;
	
	private String name;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
	private Date birth;
	
	private byte[] photo;
	
	private boolean isProvider;
	
	private Integer stateId;
	
	private Integer providerId;
	
	public boolean getIsProvider() {
        return isProvider;
	}

    public void setIsProvider(boolean provider) {
        this.isProvider = provider;
    } 

    /**
     * 將 User 實體轉換為 UserBasicInfoDTO
     * @param user User 實體
     * @return UserBasicInfoDTO
     */
    public static UserBasicInfoDTO fromUser(User user) {
        UserBasicInfoDTO dto = new UserBasicInfoDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setBirth(user.getBirthdate());
        dto.setPhoto(user.getPhoto());
        dto.setIsProvider(user.getIsProvider());
        dto.setStateId(user.getState() != null ? user.getState().getStateId() : null);
        return dto;
    }
    
}
