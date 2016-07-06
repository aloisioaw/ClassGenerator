package br.com.java.utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import br.com.java.utilities.elements.Annotation;
import br.com.java.utilities.elements.Attribute;
import br.com.java.utilities.elements.Configuration;
import br.com.java.utilities.elements.ConfigurationValueTypes;
import br.com.java.utilities.elements.NewClass;

public class Util {
	public static final Attribute serialVersion;

	static {
		serialVersion = new Attribute();
		serialVersion.setName("serialVersionUID");
		serialVersion.getModifiers().add("static");
		serialVersion.getModifiers().add("final");
		serialVersion.setType("long");
		serialVersion.setInitialValue("1L");
		serialVersion.setGenerateGetter(false);
		serialVersion.setGenerateSetter(false);
	}

	public static Attribute getSerialVersionAttribute() {
		return serialVersion;
	}

	public static void writeGettersSetters(StringBuffer sb, NewClass newClass) {
		for(Attribute attribute : newClass.getAttributes()) {
			if (attribute.getGenerateGetter()) {
				sb.append("\tpublic " + attribute.getType()+ " get" + WordUtils.capitalize(attribute.getName()) + "() {\n");
				sb.append("\t\treturn this." + attribute.getName() + ";");
				sb.append("\n\t}\n\n");
			}

			if (attribute.getGenerateSetter()) {
				sb.append( "\tpublic void set" + WordUtils.capitalize(attribute.getName()) + "(" + attribute.getType()+ " " + attribute.getName() + ") {\n" );
				sb.append("\t\tthis." + attribute.getName() + " = " + attribute.getName() + ";");
				sb.append("\n\t}\n\n");
			}
		}
	}

	public static void writeAttributes(StringBuffer sb, NewClass newClass) {
		for(Attribute attribute : newClass.getAttributes()) {
			writeAnnotations(sb, attribute.getAnnotations(), 1);

			sb.append("\tpublic ");

			for (String modifier : attribute.getModifiers()) {
				sb.append(modifier).append(" ");
			}

			sb.append(attribute.getType()).append(" ").append(attribute.getName());
			if (attribute.getInitialValue() != null && !attribute.getInitialValue().trim().isEmpty()) {
				sb.append(" = ").append(attribute.getInitialValue());
			}

			sb.append(";\n\n");
		}
	}

	public static void writeImplementations(StringBuffer sb, NewClass newClass) {
		if (!newClass.getImplementations().isEmpty()) {
			sb.append(" implements ");

			for(String implementation : newClass.getImplementations()) {
				if (newClass.getImplementations().indexOf(implementation) > 0) {
					sb.append(", ");
				}
				sb.append(implementation);
			}
		}
	}

	public static void writeAnnotations(StringBuffer sb, List<Annotation> annotations, Integer level) {
		String indentation = "";
		for(int i = 0; i < level; i++) {
			indentation += "\t";
		}

		for(Annotation annotation : annotations) {
			sb.append(indentation).append("@").append(annotation.getName());

			if (!annotation.getConfigurations().isEmpty()) {
				sb.append("(");
				for (Configuration configuration : annotation.getConfigurations()) {
					if (annotation.getConfigurations().indexOf(configuration) > 0) {
						sb.append(", ");
					}
					if (configuration.getName() != null && !configuration.getName().isEmpty()) {
						sb.append(configuration.getName()).append(" = ");
						if (configuration.getType().equals(ConfigurationValueTypes.STRING)) {
							sb.append("\"").append(configuration.getValue()).append("\"");
						} else {
							sb.append(configuration.getValue());
						}
					} else if (configuration.getValue() != null && !configuration.getValue().isEmpty()) {
						sb.append(configuration.getValue());
					}
				}
				sb.append(")\n");
			} else {
				sb.append("\n");
			}
		}
	}

	public static void writeImports(StringBuffer sb, NewClass newClass) {
		if (!newClass.getImports().isEmpty()) {
			for(String importEntry : newClass.getImports()) {
				sb.append("import ").append(importEntry).append(";\n");
			}

			sb.append("\n");
		}
	}

	public static void writeFile(String name, String content, String path) throws Exception {
		BufferedWriter out = null;
		out = new BufferedWriter(new FileWriter(path + name + ".java"));
		out.write(content);
		out.close();
	}
}
