package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	//Grafo semplice, pesato, non orientato.
	//Come vertici mettiamo direttamente gli 'id' degli artisti al posto di crearci la classe
	//per risparmiare tempo e sarebbe stato sicuramente piu' elegante.
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Adiacenza> adiacenze;
	private ArtsmiaDAO dao;
	//la soluzione al nostro percorso e' una lista di interi che sono la lista di vertici in questo
	//esercizio
	private List<Integer> best;
	
	
	public Model() {
		this.dao = new ArtsmiaDAO();
	}
	
	//per poter popolare il menu a tendina da cui selezionare quello che vogliamo
	public List<String> getRuoli(){
		return this.dao.getRuoli();
	}
	
	//per stampare la dimensione del grafo se volessimo estrarla dal controller
	public int vertici() {
		return this.grafo.vertexSet().size();
	}
	//per stampare la dimensione del grafo se volessimo estrarla dal controller
	public int archi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Adiacenza> getAdiacenze(){
		return this.adiacenze;
	}
	
	public void creaGrafo(String ruolo) {
		this.grafo = new SimpleWeightedGraph<Integer,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//mettiamo tutti gli artisti con un determinato ruolo, anche se possono essere potenzialmente
		//essere dei nodi isolati. Se tipo si parla di componenti connesse o cose di questo genere e'
		//bene aggiungere tutti i possibili vertici. Qui in questo esercizio e' abbastanza inutile 
		//mettere tutti i vertici, pero' lo abbiamo fatto per completezza.
		Graphs.addAllVertices(this.grafo, this.dao.getArtisti(ruolo));
		
		adiacenze = this.dao.getAdiacenze(ruolo);
		
		for(Adiacenza a : adiacenze) {
			/*
			
			Questa qui sotto era l'alternativa di inserire i vertici direttamente estraendoli dalle
			adiacenza
			
			if(!this.grafo.containsVertex(a.getA1()))
				this.grafo.addVertex(a.getA1());
			if(!this.grafo.containsVertex(a.getA2()))
				this.grafo.addVertex(a.getA2());
			*/
			if(this.grafo.getEdge(a.getA1(), a.getA2()) == null) {
				//grafo non c'e' ancora e quindi lo vado ad aggiungere
				Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
		
		System.out.println("Grafo creato!");
		System.out.println("# VERTICI: " + this.grafo.vertexSet().size());
		System.out.println("# ARCHI: " + this.grafo.edgeSet().size());
	}
	
	//serve per il punto 2 per controllare se l'id dell'artista inserito e' contenuto nel grafo
	//oppure no
	public boolean grafoContiene(Integer id) {
		if(this.grafo.containsVertex(id))
			return true;
		return false;
	}
	
	public List<Integer> trovaPercorso(Integer sorgente){
		this.best = new ArrayList<Integer>();
		List<Integer> parziale = new ArrayList<Integer>();
		//in parziale devo mettere il nodo di partenza
		parziale.add(sorgente);
		//lancio la ricorsione
		//iniziamo con il peso -1 per riuscire ad identificare quando siamo all'inizio della
		//ricorsione e quindi ogni strada va bene ed e' li' che il peso si va ad instaurare
		ricorsione(parziale, -1);
		
		return best;
	}
	
	//dobbiamo esplorare le varie strade partendo da un nodo inziale e quello che mi vincola sulle
	//strade da scegliere e' il peso di arco che deve essere lo stesso sull'intero percorso.
	//Non devo poi andare in vertici che sono gia' presenti in parziale, altrimenti creerei dei 
	//cicli perche' ritornerei su nodi gia' percorsi.
	//La ricorsione termina alla fine quando non ci sono piu' vicini da esplorare senza bisogno nessuna
	//return perche' dobbiamo andare finoa alla fine.
	private void ricorsione(List<Integer> parziale, int peso) {
		
		//devo esplorare tutti i vicini dell'ultimo nodo che era in parziale
		Integer ultimo = parziale.get(parziale.size() - 1);
		//ottengo i vicini
		List<Integer> vicini = Graphs.neighborListOf(this.grafo, ultimo);
		for(Integer vicino : vicini) {
			//condizione iniziale in cui devo esplorare l'iniziale e fissare cosi' il peso.
			//sinceramente io avrei solo controllato il peso perche' e' l'inizio e quindi in parziale
			//c'e' solo sorgente, mentre nell'else ha senso controllare che il vicino non fosse gia'
			//stato inserito.
			if(!parziale.contains(vicino) && peso == -1) {
				parziale.add(vicino);
				ricorsione(parziale, (int) this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)));
				//backtracking
				parziale.remove(vicino);
			} else {
				//non siamo piu' nello step iniziale in quanto un peso e' stato fissato
				if(!parziale.contains(vicino) && this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)) == peso) {
					parziale.add(vicino);
					ricorsione(parziale, peso);
					//backtracking
					parziale.remove(vicino);
				}
			}
		}
		
		//indifferente metterla qui o all'inizio
		if(parziale.size() > best.size()) {
			//se abbiamo un cammino piu' lungo di quello che avevamo in precedenza aggiorniamo
			this.best = new ArrayList<>(parziale);
		}
		
	}

}
