package com.evinced.dto.init;

import com.evinced.dto.results.AnalysisResult;
import com.evinced.dto.results.Component;

import java.util.List;

/**
 * PersistedData part in init-configuration
 */
public class AnalysisPersistedData {
	List<Component> components;
	List<Object> validations;

	public AnalysisPersistedData(AnalysisResult previousUrlStateState) {
		this.components = previousUrlStateState.getComponents();
		this.validations = previousUrlStateState.getValidations();
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public List<Object> getValidations() {
		return validations;
	}

	public void setValidations(List<Object> validations) {
		this.validations = validations;
	}
}