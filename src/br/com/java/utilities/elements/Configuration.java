package br.com.java.utilities.elements;

public class Configuration {
	private String name;
	private ConfigurationValueTypes type;
	private String value;

	public Configuration(String name, ConfigurationValueTypes type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ConfigurationValueTypes getType() {
		return type;
	}

	public void setType(ConfigurationValueTypes type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}