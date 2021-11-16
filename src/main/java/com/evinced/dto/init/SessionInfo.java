package com.evinced.dto.init;

import java.util.Map;

/**
 * Session Information part in init-configuration
 */
public class SessionInfo {
	private String userId;
	private Map<String, Object> serverConfig;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, Object> getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(Map<String, Object> serverConfig) {
		this.serverConfig = serverConfig;
	}
}
