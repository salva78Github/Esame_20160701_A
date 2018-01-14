package it.polito.tdp.formulaone.model;

import java.util.List;

import it.polito.tdp.formulaone.exception.Formula1Exception;

public class TestModel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		Model model = new Model();
		try {
			Driver d = model.retrieveBestDriver(1997);
			System.out.println(d);
			
			List<Driver> dreamTeam = model.retrieveDreamTeam(2);
			
		} catch (Formula1Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
