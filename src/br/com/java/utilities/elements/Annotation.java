package br.com.java.utilities.elements;

import java.util.ArrayList;
import java.util.List;

public class Annotation {
	private String name;
	private List<Configuration> configurations;

	public Annotation() {
		this.configurations = new ArrayList<Configuration>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}
}