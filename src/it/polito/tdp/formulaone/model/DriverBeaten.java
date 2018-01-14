package it.polito.tdp.formulaone.model;

public class DriverBeaten {

	private final Driver driver;
	private final int beatsNumber;
	/**
	 * @param driver
	 * @param beatsNumber
	 */
	public DriverBeaten(Driver driver, int beatsNumber) {
		super();
		this.driver = driver;
		this.beatsNumber = beatsNumber;
	}
	/**
	 * @return the driver
	 */
	public Driver getDriver() {
		return driver;
	}
	/**
	 * @return the beatsNumber
	 */
	public int getBeatsNumber() {
		return beatsNumber;
	}
	
	
	
	
}
