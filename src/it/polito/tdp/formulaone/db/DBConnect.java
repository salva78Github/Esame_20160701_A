package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

import it.polito.tdp.formulaone.exception.Formula1Exception;

public class DBConnect {
	
	static private final String jdbcUrl = "jdbc:mysql://localhost/formula1?user=root&password=salva_root";
	private static DataSource ds;

	
	public static Connection getConnection() throws Formula1Exception {

		if (ds == null) {
			// crea il DataSource
			try {
				ds = DataSources.pooledDataSource(DataSources.unpooledDataSource(jdbcUrl));
			} catch (SQLException sqle) {
				sqle.printStackTrace();
				throw new Formula1Exception("Errore nella creazione del datasource", sqle);
			}
		}

		try {
			Connection c = ds.getConnection();
			return c;
		} catch (SQLException sqle) {
			// TODO Auto-generated catch block
			sqle.printStackTrace();
			throw new Formula1Exception("Errore nel recupero della connessione. ", sqle);
		}

	}

	public static void closeResources(Connection c, Statement s, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (s != null) {
				s.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (c != null) {
				c.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
