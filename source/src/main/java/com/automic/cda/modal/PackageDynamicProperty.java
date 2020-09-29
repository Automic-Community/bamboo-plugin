package com.automic.cda.modal;

public class PackageDynamicProperty {

	protected String name;
	protected String value;

	public PackageDynamicProperty() {

	}

	public PackageDynamicProperty(String name, String value) {

		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "DynamicProperty [name=" + name + ", value=" + value + "]";
	}

}
