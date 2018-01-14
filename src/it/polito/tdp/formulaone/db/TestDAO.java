package it.polito.tdp.formulaone.db;

import java.util.List;

import it.polito.tdp.formulaone.exception.Formula1Exception;
import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Season;

public class TestDAO {

	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		 
		List<Integer> years;
		try {
			years = dao.getAllYearsOfRace();
			System.out.println(years);
			
			List<Season> seasons = dao.getAllSeasons() ;
			System.out.println(seasons);
			
			
			List<Circuit> circuits = dao.getAllCircuits();
			System.out.println(circuits);
			
			List<Constructor> constructors = dao.getAllConstructors();
			System.out.println(constructors);
		} catch (Formula1Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
