package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mchange.v1.db.sql.DriverManagerDataSource;

import it.polito.tdp.formulaone.exception.Formula1Exception;
import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.DriverBeaten;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() throws Formula1Exception {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = DBConnect.getConnection() ;
			ps = c.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Formula1Exception("SQL Query Error", e);
		} finally {
			DBConnect.closeResources(c, ps, rs);
		}
	}
	
	public List<Season> getAllSeasons() throws Formula1Exception {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = DBConnect.getConnection() ;
			ps = c.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Formula1Exception("SQL Query Error", e);
		} finally {
			DBConnect.closeResources(c, ps, rs);
		}
	}
	
	public List<Circuit> getAllCircuits() throws Formula1Exception {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			c = DBConnect.getConnection() ;
			ps = c.prepareStatement(sql) ;
			rs = ps.executeQuery() ;

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Formula1Exception("SQL Query Error", e);
		} finally {
			DBConnect.closeResources(c, ps, rs);
		}
	}
	
	public List<Constructor> getAllConstructors() throws Formula1Exception {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			c = DBConnect.getConnection() ;
			ps = c.prepareStatement(sql) ;
			rs = ps.executeQuery() ;

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Formula1Exception("SQL Query Error", e);
		} finally {
			DBConnect.closeResources(c, ps, rs);
		}
	}

	public List<Driver> getAllDriversByYear(int year, Map<Integer, Driver> driversIdMap) throws Formula1Exception {

		String sql = "select distinct s.driverId, s.driverRef, s.number, s.code, s.forename, s.surname, s.dob, s.nationality, s.url from races r, drivers s, results ds where s.driverId = ds.driverId and r.raceId = ds.raceId and r.year = ?";
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			c = DBConnect.getConnection() ;
			ps = c.prepareStatement(sql) ;
			ps.setInt(1, year);
			rs = ps.executeQuery() ;

			List<Driver> drivers = new ArrayList<>();
			while (rs.next()) {
				//s.driverId, s.driverRef, s.number, s.code, s.forename, s.surname, s.dob, s.nationality, s.url
				
				Driver d = new Driver(rs.getInt("s.driverId"),rs.getString("s.driverRef"),rs.getInt("s.number"),rs.getString("s.code"),rs.getString("s.forename"),rs.getString("s.surname"),
						rs.getDate("s.dob").toLocalDate(), rs.getString("s.nationality"), rs.getString("s.url"));				
				drivers.add(d);
				driversIdMap.put(d.getDriverId(), d);
			
			}

			return drivers;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Formula1Exception("SQL Query Error", e);
		} finally {
			DBConnect.closeResources(c, ps, rs);
		}
	}

	public List<DriverBeaten> getDriversBeatenByYear(int year, Driver driver, Map<Integer, Driver> driversIdMap) throws Formula1Exception {

		String sql = "select ds2.driverId, count(*) as beatenDrivers from races r, results ds, results ds2 where r.raceId = ds.raceId and ds.raceId = ds2.raceId and ds.driverId =? and ds.position = 1 and ds2.position > 1 and r.year = ? group by ds2.driverId";
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			c = DBConnect.getConnection() ;
			ps = c.prepareStatement(sql) ;
			ps.setInt(2, year);
			ps.setInt(1, driver.getDriverId());
			rs = ps.executeQuery() ;

			List<DriverBeaten> drivers = new ArrayList<>();
			while (rs.next()) {
				//s.driverId, s.driverRef, s.number, s.code, s.forename, s.surname, s.dob, s.nationality, s.url
				DriverBeaten db = new DriverBeaten(driversIdMap.get(rs.getInt("ds2.driverId")), rs.getInt("beatenDrivers"));
				drivers.add(db);
				
			}

			return drivers;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Formula1Exception("SQL Query Error", e);
		} finally {
			DBConnect.closeResources(c, ps, rs);
		}
	}
	
}
