package it.polito.tdp.artsmia.model;

//classe relativa alle coppie di artisti che devono essere collegate
public class Adiacenza implements Comparable<Adiacenza> {
	private Integer a1;
	private Integer a2;
	private Integer peso;
	
	public Adiacenza(Integer a1, Integer a2, Integer peso) {
		super();
		this.a1 = a1;
		this.a2 = a2;
		this.peso = peso;
	}
	
	public Integer getA1() {
		return a1;
	}
	public void setA1(Integer a1) {
		this.a1 = a1;
	}
	public Integer getA2() {
		return a2;
	}
	public void setA2(Integer a2) {
		this.a2 = a2;
	}
	public Integer getPeso() {
		return peso;
	}
	public void setPeso(Integer peso) {
		this.peso = peso;
	}

	//per poter ordinare le adiacenze in maniera decrescente per la stampa alla fine del punto 1
	@Override
	public int compareTo(Adiacenza o) {
		return -this.peso.compareTo(o.getPeso());
	}
	
	
	
}
