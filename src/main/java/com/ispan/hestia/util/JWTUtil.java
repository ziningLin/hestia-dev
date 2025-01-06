package com.ispan.hestia.util;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ispan.hestia.exception.JWTException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.PostConstruct;

@Component
public class JWTUtil {

	/**
	 * 密鑰
	 */
	@Value("${jwt.secret}")
	private String secret;

	/**
	 * 一般登入的 token 過期時間 => 60分鐘
	 */
	private long DEFAULT_EXPIRATION_TIME = 1000 * 60 * 60;

	/**
	 * 勾選「記住我」的 token 過期時間 => 30天
	 */
	private long REMEMBER_ME_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30;

	@PostConstruct
	public void init() {
		if (secret == null || secret.isEmpty()) {
			throw new JWTException("JWT secret key is not configured");
		}
	}

	/**
	 * 生成 JWT Token
	 *
	 * @param userId
	 * @param isProvider       是否為房東
	 * @param additionalClaims 擴展屬性
	 * @return JWT Token
	 */
	public String createToken(String data, boolean rememberMe) {
		// 決定過期時間
		long expirationTime = rememberMe ? REMEMBER_ME_EXPIRATION_TIME : DEFAULT_EXPIRATION_TIME;

		try {
			// 生成 JWT 主體
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().issuer("hestia").issueTime(new Date())
					.expirationTime(new Date(System.currentTimeMillis() + expirationTime)).subject(data).build();

			JWSSigner signer = new MACSigner(secret);
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
			signedJWT.sign(signer);

			return signedJWT.serialize();
		} catch (JOSEException e) {
			throw new JWTException("Error generating JWT token");
		}
	}

	/**
	 * 驗證 JWT Token
	 *
	 * @param token JWT Token
	 * @return 提取的存入的 data
	 * @throws JOSEException
	 * @throws ParseException
	 */
	public JWTClaimsSet validateAndParseToken(String token) {
		try {
			// 解析 Token
			SignedJWT signedJWT = SignedJWT.parse(token);

			// 驗證簽名
			JWSVerifier verifier = new MACVerifier(secret);
			if (!signedJWT.verify(verifier)) {
				throw new JWTException("Invalid JWT signature");
			}

			// 驗證是否過期
			Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
			if (expirationTime == null || expirationTime.before(new Date())) {
				throw new JWTException("JWT token has expired");
			}

			// 驗證成功，返回存入的 data
			JWTClaimsSet data = signedJWT.getJWTClaimsSet();
			if (data == null) {
				throw new JWTException("No data found in JWT token");
			}
			return data;

		} catch (ParseException e) {
			throw new JWTException("Error parsing JWT token");
		} catch (JOSEException e) {
			throw new JWTException("Error verifying JWT signature");
		}

	}

	/**
	 * 從 Token 中驗證並提取 userId
	 *
	 * @param token JWT Token
	 * @return userId (Integer)
	 */
	public Integer getUserIdFromToken(JWTClaimsSet data) {
		String dataStr = data.getSubject();

		if (dataStr == null || dataStr.isEmpty()) {
			throw new JWTException("Unable to parse or invalid JWT token.");
		}
		// 解析為 JSONObject
		JSONObject dataJson;
		try {
			dataJson = new JSONObject(dataStr);
		} catch (Exception e) {
			throw new JWTException("Data in the token is not a valid JSON format.");
		}
		// 確認是否包含 userId
		if (!dataJson.has("userId")) {
			throw new JWTException("Token does not contain userId.");
		}
		// 獲取 userId
		Integer userId;
		try {
			userId = dataJson.getInt("userId");
		} catch (Exception e) {
			throw new JWTException("userId in the token is not a valid numeric format.");
		}
		return userId;
	}

	/**
	 * 從 Token 中提取 isProvider
	 *
	 * @param token JWT Token
	 * @return isProvider (Boolean)
	 */
	public Boolean getIsProviderFromToken(JWTClaimsSet data) {
		String dataStr = data.getSubject();

		if (dataStr == null || dataStr.isEmpty()) {
			throw new JWTException("Unable to parse or invalid JWT token.");
		}
		// 解析為 JSONObject
		JSONObject dataJson;
		try {
			dataJson = new JSONObject(dataStr);
		} catch (Exception e) {
			throw new JWTException("Data in the token is not a valid JSON format.");
		}
		// 確認是否包含 isProvider
		if (!dataJson.has("isProvider")) {
			throw new JWTException("Token does not contain isProvider.");
		}
		// 獲取 isProvider
		Boolean isProvider;
		try {
			isProvider = dataJson.getBoolean("isProvider");
		} catch (Exception e) {
			throw new JWTException("isProvider in the token is not a valid boolean format.");
		}
		return isProvider;
	}

}
