package br.com.java.utilities.elements;

import java.util.ArrayList;
import java.util.List;

public class Attribute {
	private String name;
	private String type;
	private List<String> modifiers;
	private List<Annotation> annotations;
	private String initialValue;
	private Boolean generateGetter;
	private Boolean generateSetter;

	public Attribute() {
		this.modifiers = new ArrayList<String>();
		this.annotations = new ArrayList<Annotation>();
		this.generateGetter = true;
		this.generateSetter = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getModifiers() {
		return modifiers;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	public Boolean getGenerateGetter() {
		return generateGetter;
	}

	public void setGenerateGetter(Boolean generateGetter) {
		this.generateGetter = generateGetter;
	}

	public Boolean getGenerateSetter() {
		return generateSetter;
	}

	public void setGenerateSetter(Boolean generateSetter) {
		this.generateSetter = generateSetter;
	}
}