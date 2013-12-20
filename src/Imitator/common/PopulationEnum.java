package Imitator.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PopulationEnum {

	String _population;
	private static List<PopulationEnum> allValues = new ArrayList<PopulationEnum>();
	
	private PopulationEnum(String pop){
		_population = pop;
		allValues.add(this);
	}
	
	public static PopulationEnum MALE_10s = new PopulationEnum("male-10s"); 
	public static PopulationEnum MALE_20s = new PopulationEnum("male-20s"); 
	public static PopulationEnum MALE_30s = new PopulationEnum("male-30s"); 
	public static PopulationEnum FEMALE_10s = new PopulationEnum("female-10s"); 
	public static PopulationEnum FEMALE_20s = new PopulationEnum("female-20s"); 
	public static PopulationEnum FEMALE_30s = new PopulationEnum("female-30s"); 

	public String getValue(){
		return this._population;
	}
	
	public static Collection<PopulationEnum> getAllValues(){
		return allValues;
	}
	
	public static PopulationEnum getRandomPopulation(){
		return allValues.get((int) (Math.random() * allValues.size()));
	}
}
