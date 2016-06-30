package br.com.java.utilities.elements;

import java.util.ArrayList;
import java.util.List;

public class NewClass {
	private String packageFullPath;
	private List<Attribute> attributes;
	private Boolean open;
	private String name;
	private List<Annotation> annotations;
	private List<String> imports;
	private List<String> implementations;

	public NewClass() {
		this.open = true;
		this.attributes = new ArrayList<Attribute>();
		this.annotations = new ArrayList<Annotation>();
		this.imports = new ArrayList<String>();
		this.implementations = new ArrayList<String>();
	}

	public String getPackageFullPath() {
		return packageFullPath;
	}

	public void setPackageFullPath(String packageFullPath) {
		this.packageFullPath = packageFullPath;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	public void addAnnotation(Annotation annotation) {
		this.annotations.add(annotation);
	}

	public void addImport(String importEntry) {
		this.imports.add(importEntry);
	}

	public void addImplementation(String implementation) {
		this.implementations.add(implementation);
	}

	public boolean isOpen() {
		return this.open;
	}

	public void close() {
		this.open = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public List<String> getImports() {
		return imports;
	}

	public List<String> getImplementations() {
		return implementations;
	}
}