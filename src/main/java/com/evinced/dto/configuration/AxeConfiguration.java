package com.evinced.dto.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Map;

/**
 * Axe configuration as taken from the official documentation:
 * https://github.com/dequelabs/axe-core/blob/develop/doc/API.md#api-name-axeconfigure
 */
@JsonInclude(Include.NON_NULL)
public class AxeConfiguration {
	private AxeBranding branding;
	private Object reporter;
	private Map<String, Object> rules;
	private Object locale;
	private Boolean disableOtherRules;
	private Boolean noHtml;

	public AxeBranding getBranding() {
		return branding;
	}

	public void setBranding(AxeBranding branding) {
		this.branding = branding;
	}

	public Object getReporter() {
		return reporter;
	}

	public void setReporter(Object reporter) {
		this.reporter = reporter;
	}

	public Map<String, Object> getRules() {
		return rules;
	}

	public void setRules(Map<String, Object> rules) {
		this.rules = rules;
	}

	public Object getLocale() {
		return locale;
	}

	public void setLocale(Object locale) {
		this.locale = locale;
	}

	public Boolean getDisableOtherRules() {
		return disableOtherRules;
	}

	public void setDisableOtherRules(Boolean disableOtherRules) {
		this.disableOtherRules = disableOtherRules;
	}

	public Boolean getNoHtml() {
		return noHtml;
	}

	public void setNoHtml(Boolean noHtml) {
		this.noHtml = noHtml;
	}

	@Override
	public String toString() {
		return "{" +
				"branding=" + branding +
				", reporter=" + reporter +
				", rules=" + rules +
				", locale=" + locale +
				", disableOtherRules=" + disableOtherRules +
				", noHtml=" + noHtml +
				'}';
	}

	public static class AxeBranding {
		private String brand;
		private String application;

		public String getBrand() {
			return brand;
		}

		public void setBrand(String brand) {
			this.brand = brand;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		@Override
		public String toString() {
			return "{" +
					"brand='" + brand + '\'' +
					", application='" + application + '\'' +
					'}';
		}
	}
}
