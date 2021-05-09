package it.polito.tdp.borders.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.borders.model.Border;
import it.polito.tdp.borders.model.Country;

public class BordersDAO {

	public void loadAllCountries(Map<Integer, Country> map) {

		String sql = "SELECT ccode, StateAbb, StateNme FROM country ORDER BY StateAbb";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!map.containsKey(rs.getInt("ccode"))) {
					Country c = new Country(rs.getInt("ccode"), rs.getString("StateAbb"), rs.getString("StateNme")); 
					map.put(c.getCcode(), c);
				}
				
			}
			
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Country> getVertici(Map<Integer, Country> map, int anno){
		String sql = "SELECT c.CCode AS id "
				+ "FROM country c, contiguity co "
				+ "WHERE (c.CCode = co.state1no OR c.CCode = co.state2no) "
				+ "AND co.year <= ? AND co.conttype = 1 "
				+ "GROUP BY c.CCode";
		List<Country> result = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(map.get(rs.getInt("id")));				
			}	
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Border> getCountryPairs(Map<Integer, Country> map, int anno) {
		String sql = "SELECT state1no AS c1, state2no AS c2 "
				+ "FROM contiguity "
				+ "WHERE year <= ? AND conttype = 1 "
				+ "GROUP BY state1no, state2no";
		List<Border> result = new ArrayList<Border>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Country c1 = map.get(rs.getInt("c1"));		
				Country c2 = map.get(rs.getInt("c2"));		
				
				if(c1 != null && c2 != null) {
					Border b = new Border(c1, c2);
					result.add(b);
				}
				else {
					System.out.println("Errore getRotte");
				}
			}	
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}
