package it.polito.tdp.borders.model;

import java.util.List;
import java.util.Set;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();

		//System.out.println("TestModel -- TODO");
		
		System.out.println("Creo il grafo relativo al 2000");
		model.createGraph(2000);
		
		Set<Country> countries = model.getCountries();
		System.out.format("Trovate %d nazioni\n", countries.size());
		System.out.format(model.getGradoVertici());

		System.out.format("Numero componenti connesse: %d\n", model.getNumberOfConnectedComponents());
		
//		Map<Country, Integer> stats = model.getCountryCounts();
//		for (Country country : stats.keySet())
//			System.out.format("%s %d\n", country, stats.get(country));		
		
	}

}
