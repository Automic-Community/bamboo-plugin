package com.automic.cda.modal;

public class Artifact {

	private String name;
	private String source;
	private String customField;

	public Artifact() {

	}

	public Artifact(String name, String source, String customField) {

		this.name = name;
		this.source = source;
		this.customField = customField;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}

	@Override
	public String toString() {
		return "Artifact [name=" + name + ", source=" + source + ", customField=" + customField + "]";
	}

}
