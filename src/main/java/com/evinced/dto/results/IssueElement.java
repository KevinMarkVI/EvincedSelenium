package com.evinced.dto.results;

public class IssueElement {
	private String componentId;
	private String domSnippet;
	private String id;
	private String index;
	private String pageUrl;
	private String selector;

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getDomSnippet() {
		return domSnippet;
	}

	public void setDomSnippet(String domSnippet) {
		this.domSnippet = domSnippet;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}
}
