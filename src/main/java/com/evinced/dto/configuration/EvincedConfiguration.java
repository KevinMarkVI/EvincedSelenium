package com.evinced.dto.configuration;

public class EvincedConfiguration {
	private String rootSelector = null;
	private AxeConfiguration axeConfig = null;
	private Boolean includeIframes = false;

	public EvincedConfiguration(String rootSelector, AxeConfiguration axeConfig, Boolean includeIframes) {
		this.rootSelector = rootSelector;
		this.axeConfig = axeConfig;
		this.includeIframes = includeIframes;
	}

	public EvincedConfiguration() {
	}

	public Boolean shouldIncludeIframes() {
		return this.includeIframes;
	}

	public String getRootSelector() {
		return rootSelector;
	}

	/**
	 * @param rootSelector defaults to null. Choose a single CSS selector to run the analysis,
	 *                     for example - run analysis on the element that hold only the menu bar.
	 *                     When no configuration is passed, it will scan the entire page.
	 * @return an EvincedConfiguration instance
	 */
	public EvincedConfiguration setRootSelector(String rootSelector) {
		this.rootSelector = rootSelector;
		return this;
	}

	public AxeConfiguration getAxeConfig() {
		return axeConfig;
	}

	/**
	 * @param axeConfig Pass configuration to Axe (some of the validations Evinced runs are based on Axe, and uses the same configuration).
	 *                  For Axe config options: https://github.com/dequelabs/axe-core/blob/develop/doc/API.md#api-name-axeconfigure.
	 * @return an EvincedConfiguration instance
	 */
	public EvincedConfiguration setAxeConfig(AxeConfiguration axeConfig) {
		this.axeConfig = axeConfig;
		return this;
	}

	public Boolean getIncludeIframes() {
		return includeIframes;
	}

	/**
	 * @param includeIframes - When set to true, makes the accessibility tests run the analysis on iframes that exist inside the page.
	 *                       Defaults to `false`.
	 * @return an EvincedConfiguration instance
	 */
	public EvincedConfiguration setIncludeIframes(Boolean includeIframes) {
		this.includeIframes = includeIframes;
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				"rootSelector='" + rootSelector + '\'' +
				", axeConfig=" + axeConfig +
				", includeIframes=" + includeIframes +
				'}';
	}
}
