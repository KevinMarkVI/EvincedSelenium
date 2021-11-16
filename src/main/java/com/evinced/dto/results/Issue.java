package com.evinced.dto.results;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {
	private String id;
	private String index;
	private String signature;
	private NamedProp type;
	private NamedProp severity;
	private String summary;
	private String description;
	private Object additionalInformation;
	private String duplicates;
	private List<IssueElement> elements;
	private long firstSeenTime;
	private List<IssueTag> tags;
	private String knowledgeBaseLink;

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

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public NamedProp getType() {
		return type;
	}

	public void setType(NamedProp type) {
		this.type = type;
	}

	public NamedProp getSeverity() {
		return severity;
	}

	public void setSeverity(NamedProp severity) {
		this.severity = severity;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(Object additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(String duplicates) {
		this.duplicates = duplicates;
	}

	public List<IssueElement> getElements() {
		return elements;
	}

	public void setElements(List<IssueElement> elements) {
		this.elements = elements;
	}

	public long getFirstSeenTime() {
		return firstSeenTime;
	}

	public void setFirstSeenTime(long firstSeenTime) {
		this.firstSeenTime = firstSeenTime;
	}

	public List<IssueTag> getTags() {
		return tags;
	}

	public void setTags(List<IssueTag> tags) {
		this.tags = tags;
	}

	public String getKnowledgeBaseLink() {
		return knowledgeBaseLink;
	}

	public void setKnowledgeBaseLink(String knowledgeBaseLink) {
		this.knowledgeBaseLink = knowledgeBaseLink;
	}
}
