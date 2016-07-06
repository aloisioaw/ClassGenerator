package br.com.java.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import br.com.java.utilities.elements.Annotation;
import br.com.java.utilities.elements.Attribute;
import br.com.java.utilities.elements.Configuration;
import br.com.java.utilities.elements.ConfigurationValueTypes;
import br.com.java.utilities.elements.NewClass;

public class EntityExtractor {
	public static void writeEntities(List<NewClass> newClasses, String pathToClasses) throws Exception {
		StringBuffer sb = null;
		for (NewClass newClass : newClasses) {
			sb = new StringBuffer();
			//Package
			sb.append(String.format(Constants.PACKAGE_DECLARATION, Constants.ENTITIES_PACKAGE_NAME));

			Util.writeImports(sb, newClass);

			Util.writeAnnotations(sb, newClass.getAnnotations(), 0);

			sb.append("public class " + newClass.getName());

			Util.writeImplementations(sb, newClass);
			//Open class
			sb.append(" {\n");

			Util.writeAttributes(sb, newClass);

			Util.writeGettersSetters(sb, newClass);

			//Close class
			sb.append( "}" );

			Util.writeFile(newClass.getName(), sb.toString(), pathToClasses);
		}
	}

	public static List<NewClass> extractEntities(String path, String packageFullPath) throws IOException {
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
				table.getConfigurations().add(new Configuration("name", ConfigurationValueTypes.STRING, tableName));
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
		for (String dataType : Constants.DATATYPES) {
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
	        		sequenceGenerator.getConfigurations().add(new Configuration("name", ConfigurationValueTypes.STRING, null));
	        		sequenceGenerator.getConfigurations().add(new Configuration("sequenceName", ConfigurationValueTypes.STRING, null));
	        		sequenceGenerator.getConfigurations().add(new Configuration("allocationSize", ConfigurationValueTypes.INTEGER, "1"));
	        		attribute.getAnnotations().add(sequenceGenerator);

	        		Annotation generatedValue = new Annotation();
	        		generatedValue.setName("GeneratedValue");
	        		generatedValue.getConfigurations().add(new Configuration("strategy", ConfigurationValueTypes.OBJECT, "GenerationType.SEQUENCE"));
	        		generatedValue.getConfigurations().add(new Configuration("generator", ConfigurationValueTypes.STRING, null));
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
    		column.getConfigurations().add(new Configuration("name", ConfigurationValueTypes.STRING, aux[0]));
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
