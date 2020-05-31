package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//per estrarre tutti i possibili vertici del grafo se ci mettiamo nel caso di voler anche 
	//inserire in questo eventuali nodi che sono isolati
	public List<Integer> getArtisti(String ruolo) {
		String sql = "select a.artist_id from artists a, authorship au " +
					"where a.artist_id = au.artist_id and au.role = ?";
		List<Integer> artisti = new ArrayList<Integer>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, ruolo);
			ResultSet res = st.executeQuery();
			while(res.next()) {
				artisti.add(res.getInt("artist_id"));
			}
			conn.close();
			return artisti;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 	Mi faccio dare direttamente tutti i collegamenti.
	 	
	 	Vogliamo estrarre coppie di artisti con un conteggio delle volte in cui hanno esposto insieme delle
	 	opere.
	 	Dobbiamo fare un join della tabella con se stessa per poter estrarre la coppia di artisti e unire 
	 	anche altre tabelle per poter recuperare tutte le informazioni che ci servono sugli artisti che 
	 	andiamo a verificare.
	 	Autorship serve per recuperare il ruolo e ce ne servono 2 di tabelle perche' dobbiamo settare il 
	 	ruolo per entrambi gli artisti. Stesso discorso vale per evere due tabelle che riguardano la 
	 	tabella base di exhibition_oblects in quanto dobbiamoa legate un autore alle sue esibizioni.
	 	Non ci serve la tabella objects perche' ci bastano le info che troviamo anche nelle altre.
	 	
	 	La parte della query qui sotto,
	 	a1.artist_id > a2.artist_id
	 	serve per evitare di avere lo stesso artista con se stesso e anche di avere le coppie invertite che
	 	non servono.
	 	
	 	La group by serve per avere il conteggio relativo alla coppia di artisti che e' cio' che ci serve.
	 */
	public List<Adiacenza> getAdiacenze(String ruolo){
		String sql = "select a1.artist_id AS a1, a2.artist_id AS a2, COUNT(DISTINCT eo1.exhibition_id) AS peso " + 
				"from artists a1, artists a2, authorship au1, authorship au2, exhibition_objects eo1, exhibition_objects eo2 " + 
				"where au1.role = ? and au2.role = ? " + 
				"and au1.artist_id = a1.artist_id and au2.artist_id = a2.artist_id " + 
				"and au1.object_id = eo1.object_id and au2.object_id = eo2.object_id " + 
				"and eo1.exhibition_id = eo2.exhibition_id " + 
				"and a1.artist_id > a2.artist_id " + 
				"GROUP BY a1.artist_id, a2.artist_id";
		List<Adiacenza> adiacenze = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, ruolo);
			st.setString(2, ruolo);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				adiacenze.add(new Adiacenza(res.getInt("a1"), res.getInt("a2"), res.getInt("peso")));
			}
			conn.close();
			return adiacenze;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	//serve per popolare il menu a tendina da cui scegliere il ruolo nel punto 1
	public List<String> getRuoli() {
		String sql = "SELECT DISTINCT role FROM authorship ORDER BY role ASC";
		List<String> ruoli = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				ruoli.add(res.getString("role"));
			}
			conn.close();
			return ruoli;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
