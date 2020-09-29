package com.automic.cda.modal;

public class PackageProperty extends PackageDynamicProperty {

	private String type;

	public PackageProperty() {

		super();
	}

	public PackageProperty( String name, String value, String type) {

		this.type = type;
		this.name = name;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Property [type=" + type + ", name=" + name + ", value=" + value + "]";
	}

}
