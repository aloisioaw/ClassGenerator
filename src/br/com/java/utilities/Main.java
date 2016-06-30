package br.com.java.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import br.com.java.utilities.elements.Annotation;
import br.com.java.utilities.elements.Attribute;
import br.com.java.utilities.elements.Configuration;
import br.com.java.utilities.elements.NewClass;

public class Main {
	private static final String[] DATATYPES = {"NUMBER", "DATE", "VARCHAR2", "CHAR"};
	private static final String PACKAGE_NAME = "br.com.sicredi.ucm.entity";
	private static final String PACKAGE_DECLARATION = "package %s;\n\n";
	private static final String PATH_TO_SQL = "/home/aloisio/Dev/Workbench/Projetos/Java - Tools/GeradorClasseDDL/Estrutura 86-808.sql";
	private static final String PATH_TO_CLASSES = "/home/aloisio/Dev/Workbench/Projetos/Java - Tools/GeradorClasseDDL/GeneratedClasses/";

	public static void main(String[] args) {
		try {
			List<NewClass> newClasses = extractClasses(PATH_TO_SQL, PACKAGE_DECLARATION);
			writeClasses(newClasses, PATH_TO_CLASSES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeClasses(List<NewClass> newClasses, String pathToClasses) throws Exception {
		StringBuffer sb = null;
		for (NewClass newClass : newClasses) {
			sb = new StringBuffer();
			//Package
			sb.append(String.format(PACKAGE_DECLARATION, PACKAGE_NAME));

			writeImports(sb, newClass);

			writeAnnotations(sb, newClass.getAnnotations(), 0);

			sb.append("public class " + newClass.getName());

			writeImplementations(sb, newClass);
			//Open class
			sb.append(" {\n");

			writeAttributes(sb, newClass);

			writeGettersSetters(sb, newClass);

			//Close class
			sb.append( "}" );

		    writeFile(newClass.getName(), sb.toString(), pathToClasses);
		}
	}

	private static void writeGettersSetters(StringBuffer sb, NewClass newClass) {
		//Getters and Setters
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

	private static void writeAttributes(StringBuffer sb, NewClass newClass) {
		//Attributes
		for(Attribute attribute : newClass.getAttributes()) {
			writeAnnotations(sb, attribute.getAnnotations(), 1);

			sb.append("\tprivate ");

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

	private static void writeImplementations(StringBuffer sb, NewClass newClass) {
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

	private static void writeAnnotations(StringBuffer sb, List<Annotation> annotations, Integer level) {
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
					sb.append(configuration.getName()).append(" = ");
					if (configuration.getType().equals("String")) {
						sb.append("\"").append(configuration.getValue()).append("\"");
					} else {
						sb.append(configuration.getValue());
					}
				}
				sb.append(")\n");
			} else {
				sb.append("\n");
			}
		}
	}

	private static void writeImports(StringBuffer sb, NewClass newClass) {
		if (!newClass.getImports().isEmpty()) {
			for(String importEntry : newClass.getImports()) {
				sb.append("import ").append(importEntry).append(";\n");
			}

			sb.append("\n");
		}
	}

	private static void writeFile(String name, String content, String path) throws Exception {
		BufferedWriter out = null;
		out = new BufferedWriter(new FileWriter(path + name + ".java"));
		out.write(content);
		out.close();
	}

	private static List<NewClass> extractClasses(String path, String packageFullPath) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(path));
		List<NewClass> classes = new ArrayList<NewClass>();
		String str;


		NewClass newClass = null;
		while ((str = bf.readLine()) != null) {
			str = str.trim();
			System.out.println(str);

			if (str.isEmpty() || str.startsWith("--")) {
				continue;
			} else if (str.contains("CREATE TABLE")) {
				newClass = new NewClass();
				newClass.setName(prepareClassName(str));
				newClass.setPackageFullPath(packageFullPath);

				newClass.addImport("java.io.Serializable");
				newClass.addImport("javax.persistence.Column");
				newClass.addImport("javax.persistence.Entity");
				newClass.addImport("javax.persistence.SequenceGenerator");
				newClass.addImport("javax.persistence.Id");
				newClass.addImport("javax.persistence.GeneratedValue");
				newClass.addImport("javax.persistence.GenerationType");
				newClass.addImport("javax.persistence.Table");

				Annotation entity = new Annotation();
				entity.setName("Entity");
				newClass.addAnnotation(entity);

				Annotation table = new Annotation();
				table.setName("Table");
				String tableName = extractTableName(str);
				table.getConfigurations().add(new Configuration("name", "String", tableName));
				newClass.addAnnotation(table);

				newClass.addImplementation("Serializable");

				Attribute serialVersion = new Attribute();
				serialVersion.setName("serialVersionUID");
				serialVersion.getModifiers().add("static");
				serialVersion.getModifiers().add("final");
				serialVersion.setType("long");
				serialVersion.setInitialValue("1L");
				serialVersion.setGenerateGetter(false);
				serialVersion.setGenerateSetter(false);

				newClass.addAttribute(serialVersion);
			} else if (isCreateTableTermination(str) && newClass != null && newClass.isOpen()) {
				newClass.close();
				classes.add(newClass);
				newClass = null;
			} else if (verifyStringsContainValidDataType(str) && newClass != null && newClass.isOpen()) {
				newClass.addAttribute(createAttribute(str));
			}
		}
		bf.close();

		return classes;
	}

	private static String extractTableName(String str) {
		return str.replace("CREATE TABLE", "").replace(" ", "").replace("(", "").trim().split("\\.")[1];
	}

	private static boolean verifyStringsContainValidDataType(String str) {
		for (String dataType : DATATYPES) {
			if (str.contains(dataType)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isCreateTableTermination(String str) {
		str = str.trim();

		return str.equals(")") || str.equals(");");
	}

	private static String prepareClassName(String str) {
		if (str.contains("."))
			str = str.trim().split("\\.")[1];

		return camelCase(str);
	}

	private static String camelCase(String str) {
		str = str.replace("CREATE TABLE", "").replace(" ", "").replace("(", "").replace("_", " ").trim();
		return WordUtils.capitalizeFully(str).replace(" ", "");
	}

	private static Attribute createAttribute(String str) {
		String[] aux = prepareLine(str);

		if (aux != null && aux.length > 0) {

			Attribute attribute = new Attribute();
	        attribute.setName(uncapitalize(camelCase(aux[0])));

	        if (aux[1].contains("NUMBER")){
	        	attribute.setType("Integer");

	        	if (aux[0].toUpperCase().startsWith("OID_")) {
	        		attribute.setGenerateSetter(false);

	        		Annotation id = new Annotation();
	        		id.setName("Id");
	        		attribute.getAnnotations().add(id);

	        		Annotation sequenceGenerator = new Annotation();
	        		sequenceGenerator.setName("SequenceGenerator");
	        		sequenceGenerator.getConfigurations().add(new Configuration("name", "String", null));
	        		sequenceGenerator.getConfigurations().add(new Configuration("sequenceName", "String", null));
	        		sequenceGenerator.getConfigurations().add(new Configuration("allocationSize", "Integer", "1"));
	        		attribute.getAnnotations().add(sequenceGenerator);

	        		Annotation generatedValue = new Annotation();
	        		generatedValue.setName("GeneratedValue");
	        		generatedValue.getConfigurations().add(new Configuration("strategy", "Object", "GenerationType.SEQUENCE"));
	        		generatedValue.getConfigurations().add(new Configuration("generator", "String", null));
	        		attribute.getAnnotations().add(generatedValue);
	        	}
	        } else if(aux[1].contains("VARCHAR")) {
	        	attribute.setType("String");
	        } else if(aux[1].contains("DATE")) {
	        	attribute.setType("Date");
	        } else if(aux[1].contains("CHAR")) {
	        	attribute.setType("Char");
	        }

	        Annotation column = new Annotation();
    		column.setName("Column");
    		column.getConfigurations().add(new Configuration("name", "String", aux[0]));
    		attribute.getAnnotations().add(column);

			return attribute;
		} else
			return null;
	}

	private static String[] prepareLine(String str) {
		return str.replace(",", "").trim().replaceAll("\\s+", " ").split(" ");
	}

	public static String uncapitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuffer(strLen)
			.append(Character.toLowerCase(str.charAt(0))).append(str.substring(1))
			.toString();
	}
}