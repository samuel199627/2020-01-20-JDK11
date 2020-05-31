package it.polito.tdp.artsmia;

import java.net.URL;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

//importato
/*
	Abbiamo artisti con relative opere d'arte in apposite tabelle in relazioni molte e molte.
	
	Ho un arco tra due artisti quando hanno delle opere esposte nelle stesse mostre e il peso e'
	relativo al numero di mostre in cui hanno esposto insieme.
	
	Nel grafo e' andato ad inserire direttamente gli id degli artisti e non l'oggetto artista che
	sarebbe stato piu' elegante sicuramente. Non si e' quindi neanche creato delle identity Map.
	
	Grafo semplice, pesato, non orientato.
	
	C'e' alla fine del punto 1 la stampa delle adiacenze che ci siamo creati in pratica.
	
	Dopo aver creato il grafo, nel punto 2 abbiamo una richiesta ricorsiva. Inserendo un artista
	dobbiamo trovare il cammino piu' lungo con sempre gli stessi pesi sugli archi man mano che ci
	muoviamo dalla partenza verso i primi vicini.
 */
public class ArtsmiaController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnArtistiConnessi;

    @FXML
    private Button btnCalcolaPercorso;

    @FXML
    private ComboBox<String> boxRuolo;

    @FXML
    private TextField txtArtista;

    @FXML
    private TextArea txtResult;

    @FXML
    void doArtistiConnessi(ActionEvent event) {
    	//stampa tutte le adiacenze, cioe' tutti gli archi del grafo che ci siamo creati in precedenza
    	txtResult.clear();
    	List<Adiacenza> adiacenze = this.model.getAdiacenze();
    	if(adiacenze == null) {
    		txtResult.appendText("DEVI CREARE PRIMA IL GRAFO");
    		return ;
    	}
    	//ordina secondo l'ordinamento naturale che ci siamo andati a definire in Adiacenza che e'
    	//l'ordinamento decrescente sui numeri
    	Collections.sort(adiacenze);
    	
    	for(Adiacenza a : adiacenze) {
    		txtResult.appendText(String.format("(%d,%d) = %d\n", a.getA1(), a.getA2(), a.getPeso()));
    	}
    	
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	Integer id;
    	//controlliamo inizialmente il codice dell'artista inserito nella casella di testo
    	try {
    		id = Integer.parseInt(txtArtista.getText());
    	} catch(NumberFormatException e) {
    		txtResult.appendText("INSERIRE UN ID NEL FORMATO CORRETTO\n");
    		return ;
    	}
    	
    	//controlliamo se l'id e' presente
    	if(!this.model.grafoContiene(id)) {
    		txtResult.appendText("L'ARTISTA NON E' NEL GRAFO!\n");
    		return ;
    	}
    	
    	//acquisisco il percorso
    	List<Integer> percorso = this.model.trovaPercorso(id);
    	txtResult.appendText("PERCORSO PIU' LUNGO: " + percorso.size() + " \n");
    	//stampo il percorso, cioe' stampo gli id degli artisti che ho percorso man mano
    	for(Integer v : percorso) {
    		txtResult.appendText(v + " ");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	
    	String ruolo = this.boxRuolo.getValue();
    	if(ruolo == null) {
    		txtResult.appendText("SELEZIONA UN RUOLO!");
    		return ;
    	}
    	
    	this.model.creaGrafo(ruolo);
    	txtResult.appendText(String.format("Grafo creato con %d vertici e %d archi", 
    			this.model.vertici(), this.model.archi()));
    	
    	//quando ho il grafo creato do la possibilita' di calcolare il percorso
    	btnCalcolaPercorso.setDisable(false);
    	
    }
    
    @FXML
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnArtistiConnessi != null : "fx:id=\"btnArtistiConnessi\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnCalcolaPercorso != null : "fx:id=\"btnCalcolaPercorso\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert boxRuolo != null : "fx:id=\"boxRuolo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtArtista != null : "fx:id=\"txtArtista\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Artsmia.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	//prima devo crearmi un grafo rispetto a calcolare il percorso
    	btnCalcolaPercorso.setDisable(true);
    	//popolo la tendina da cui scegliere il ruolo per il punto 1
    	boxRuolo.getItems().addAll(this.model.getRuoli());
    }
}
