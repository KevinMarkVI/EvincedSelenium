package com.evinced.helpers;

import com.evinced.EvincedRunner;
import com.evinced.dto.configuration.AxeConfiguration;
import com.evinced.dto.configuration.EvincedConfiguration;
import com.evinced.dto.init.AnalysisConfig;
import com.evinced.dto.init.AnalysisPersistedData;
import com.evinced.dto.init.SessionInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EvincedScriptsHelper {
	private static final String PROJECT_VERSION = PropertiesHelper.getVersion();
	private static final URL analysisWebScriptUrl = EvincedRunner.class.getResource("/analysisClientCore.bundle.js");
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String SELENIUM_ASYNC_EXECUTION_CODE = "var callback = arguments[arguments.length - 1];" +
			"var result = { error: '', results: null };" +
			"%s." +
			"	then((res) => {" +
			"		result.results = res;" +
			"  		callback(result);" +
			"	}).catch((err) => {" +
			"    	result.error = err.message;" +
			"  		callback(result);" +
			"	});";

	private static final Logger logger = LogManager.getLogger(EvincedScriptsHelper.class);

	public static String getBundleToInject() {
		String evScript = FileReaderHelper.readFileWithLineBreaksFromUrl(analysisWebScriptUrl);
		evScript += "window.Evinced = Evinced;";
		return evScript;
	}

	private static SessionInfo getSessionInfo(AxeConfiguration axeConfig, Boolean shouldIncludeIframes) {
		SessionInfo sessionInfo = new SessionInfo();
		sessionInfo.setUserId("SELENIUM-SDK-POC");
		Map<String, Object> serverConfig = new HashMap<>();
		serverConfig.put("RECORDING_SERVICE", Collections.singletonMap("ENABLE_DEBOUNCE_NEW_SELECTORS", false));
		Map toggles = new HashMap<String, Boolean>();
		toggles.put("ENABLE_SENDING_ENGINE_MATRICES", true);
		if (shouldIncludeIframes) {
			logger.error("Iframes are not supported yet!");
//			toggles.put("RUN_VALIDATION_ON_IFRAMES", true);
		}
		serverConfig.put("TOGGLES", toggles);
		if (axeConfig != null) {
			serverConfig.put("AXE_CONFIG", axeConfig);
		}
		sessionInfo.setServerConfig(serverConfig);
		return sessionInfo;
	}

	private static AnalysisConfig getAnalysisConfig(String rootSelector) {
		AnalysisConfig analysisConfig = new AnalysisConfig();
		String analysisId = UUID.randomUUID().toString();
		analysisConfig.setAnalysisId(analysisId);
		analysisConfig.setRootSelector(rootSelector);
		return analysisConfig;
	}

	private static Map<String, String> getRunningContext() {
		Map<String, String> runningContext = new HashMap<>();
		runningContext.put("hostingProduct", "SELENIUM-SDK");
		runningContext.put("hostingProductVersion", PROJECT_VERSION);
		return runningContext;
	}

	public static String getEvincedInitScript(EvincedConfiguration configuration, AnalysisPersistedData persistedData) {
		SessionInfo sessionInfo = getSessionInfo(configuration.getAxeConfig(), configuration.getIncludeIframes());
		AnalysisConfig analysisConfig = getAnalysisConfig(configuration.getRootSelector());
		Map<String, String> runningContext = getRunningContext();
		String analysisPersistedDataStr = null;
		String sessionInfoStr = null;
		String analysisConfigStr = null;
		String runningContextStr = null;

		try {
			if (persistedData != null) {
				analysisPersistedDataStr = objectMapper.writeValueAsString(persistedData);
			}
			sessionInfoStr = objectMapper.writeValueAsString(sessionInfo);
			analysisConfigStr = objectMapper.writeValueAsString(analysisConfig);
			runningContextStr = objectMapper.writeValueAsString(runningContext);
		} catch (JsonProcessingException e) {
			logger.error("Error processing Evinced initialization JSON", e);
		}

		return String.format("window.Evinced.analysisClientCore.init(" +
				"%s," + // session info
				"%s," + // analysis config
				"%s," + // persisted data
				"null," +
				"%s" +
				");", sessionInfoStr, analysisConfigStr, analysisPersistedDataStr, runningContextStr);
	}


	public static String getEvincedStopAnalysisSnippet() {
		String stopCommand = "window.Evinced.analysisClientCore.stopAnalysis()";
		return String.format(SELENIUM_ASYNC_EXECUTION_CODE, stopCommand);
	}

	public static String getEvincedRunAnalysisOnceSnippet() {
		String runOnceCommand = "window.Evinced.analysisClientCore.runAnalysisOnce()";
		return String.format(SELENIUM_ASYNC_EXECUTION_CODE, runOnceCommand);
	}


	public static String getEvincedStartAnalysisSnippet() {
		String startCommand = "window.Evinced.analysisClientCore.startAnalysis()";
		return String.format(SELENIUM_ASYNC_EXECUTION_CODE, startCommand);
	}

	public static String getIsEvincedInjectedScript() {
		return "return typeof window.Evinced === 'object' && typeof window.Evinced.analysisClientCore === 'object'";
	}
}
