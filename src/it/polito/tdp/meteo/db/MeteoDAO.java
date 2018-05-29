package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {
	
	
	public List <Citta> getAllCitta(){
		
		final String sql = "SELECT distinct Localita from situazione";
		
		List <Citta> cittaInDB = new ArrayList<>();
		
		try {
			
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Citta c = new Citta(rs.getString(1));
				
				cittaInDB.add(c);
			}
			
			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return cittaInDB;
	}

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		return null;
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		final String sql = "SELECT  avg(umidita) FROM SITUAZIONE where  SUBSTR(Data,6,2) = ? and localita= ? ";
		
		double avgUmMese=0;
		
		try {
			
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1,mese);
			st.setString(2, localita);

			ResultSet rs = st.executeQuery();

            while (rs.next()) {
             
				avgUmMese = rs.getDouble(1);
            }
							
				conn.close(); 
				
			} catch (SQLException e) {
				
				 e.printStackTrace();
				throw new RuntimeException("Errore Db");
			}
					
		return avgUmMese;
	}

}
