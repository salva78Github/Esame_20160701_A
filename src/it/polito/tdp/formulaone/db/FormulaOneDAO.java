package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Driver> getDriversForSeason(Season s) {
		String sql = "SELECT DISTINCT drivers.* " + 
				"FROM races, results, drivers " + 
				"WHERE races.year=? " + 
				"AND results.raceId=races.raceId " + 
				"AND results.position is not null " + 
				"AND results.driverId=drivers.driverId" ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, s.getYear().getValue());
			
			ResultSet res = st.executeQuery() ;
			
			List<Driver> result = new ArrayList<>() ;
			
			while(res.next()) {
				Driver d = new Driver(
						res.getInt("driverid"),
						res.getString("driverref"),
						res.getInt("number"),
						res.getString("code"),
						res.getString("forename"),
						res.getString("surname"),
						res.getDate("dob").toLocalDate(),
						res.getString("nationality"),
						res.getString("url") ) ;
				result.add(d) ;
			}
			
			conn.close();
			return result ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}
	
	/**
	 * Conta il numero di vittorie di {@code d1} su {@code d2} nella
	 * stagione {@code s}
	 * @param d1
	 * @param d2
	 * @param s
	 * @return
	 */
	public Integer contaVittorie(Driver d1, Driver d2, Season s) {
		
		String sql = "SELECT count(races.raceId) as cnt " + 
				"FROM results r1, results r2, races " + 
				"WHERE r1.raceId=r2.raceId " + 
				"AND races.raceId=r1.raceId " + 
				"AND races.year=? " + 
				"AND r1.position<r2.position " + 
				"AND r1.driverId=? " + 
				"AND r2.driverId=?" ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, s.getYear().getValue());
			st.setInt(2, d1.getDriverId());
			st.setInt(3, d2.getDriverId());
			
			ResultSet res = st.executeQuery() ;
			
			res.next() ;
			
			Integer result = res.getInt("cnt") ;
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}


	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

		List<Constructor> constructors = dao.getAllConstructors();
		System.out.println(constructors);
		
	}
	
}
