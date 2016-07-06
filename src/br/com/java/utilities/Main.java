package br.com.java.utilities;

import java.util.List;

import br.com.java.utilities.elements.NewClass;

public class Main {
	public static void main(String[] args) {
		try {
			List<NewClass> newClasses = EntityExtractor.extractEntities(Constants.PATH_TO_SQL, Constants.ENTITIES_PACKAGE_NAME);
			EntityExtractor.writeEntities(newClasses, Constants.PATH_TO_CLASSES + "/entity/");

			List<NewClass> newDTOs = DTOExtractor.extractDTOs(Constants.PATH_TO_SQL, Constants.DTOS_PACKAGE_NAME);
			DTOExtractor.writeDTOs(newDTOs, Constants.PATH_TO_CLASSES + "/dto/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}