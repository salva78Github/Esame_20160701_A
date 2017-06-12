package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private List<Season> seasons ;
	
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph ;
	
	
	// variabili di stato della ricorsione
	int tassoMin ;
	List<Driver> teamMin ;
	
	public List<Season> getSeasons() {
		if(this.seasons == null) {
			FormulaOneDAO dao = new FormulaOneDAO() ;
			this.seasons = dao.getAllSeasons() ;
		}
		return this.seasons ;
	}
	
	public void creaGrafo(Season s) {
		
		// create graph
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class) ;
		
		FormulaOneDAO dao = new FormulaOneDAO() ;

		// add vertices

		List<Driver> drivers = dao.getDriversForSeason(s) ;		
		Graphs.addAllVertices(this.graph, drivers) ;
		// System.out.println(this.graph);
		
		// add edges
		for(Driver d1: this.graph.vertexSet()) {
			for(Driver d2: this.graph.vertexSet()) {
				if(!d1.equals(d2)) {
					Integer vittorie = dao.contaVittorie(d1, d2, s) ;
					if(vittorie>0) {
						Graphs.addEdgeWithVertices(this.graph, d1, d2, vittorie) ;
					}
				}
			}
		}
		
		// System.out.println(this.graph);
	}
	
	public void creaGrafo2(Season s) {
		/**
		 * Versione alternativa
		 */
		String sql = "SELECT count(races.raceId), r1.driverId as d1, r2.driverId as d2\r\n" + 
				"FROM results r1, results r2, races\r\n" + 
				"WHERE r1.raceId=r2.raceId\r\n" + 
				"AND races.raceId=r1.raceId\r\n" + 
				"AND races.year=2000\r\n" + 
				"AND r1.position<r2.position\r\n" + 
				"GROUP BY d1, d2" ;
	}
	
	public Driver getBestDriver() {
		Driver best=null ;
		int max = Integer.MIN_VALUE ;
		
		for(Driver d: this.graph.vertexSet()) {
			int peso = 0 ;
			
			for(DefaultWeightedEdge e: graph.outgoingEdgesOf(d)) {
				peso += graph.getEdgeWeight(e) ;
			}
			
			for(DefaultWeightedEdge e: graph.incomingEdgesOf(d)) {
				peso -= graph.getEdgeWeight(e) ;
			}
			
			if(peso>max) {
				max = peso ;
				best = d ;
			}
		}
		
		return best ;
	}
	
	public List<Driver> getDreamTeam(int K) {
		
		Set<Driver> team = new HashSet<>() ;
		this.tassoMin = Integer.MAX_VALUE ;
		this.teamMin = null ;
		
		ricorsiva(0, team, K);
		
		return this.teamMin ;
		
	}
	
	/**
	 * In ingresso ricevo il {@code team} parziale composto da {@code passo} elementi.
	 * La variabile {@code passo} parte da 0.
	 * Il caso terminale è quanto {@code passo==K}, ed in quel caso va calcolato il 
	 * tasso di sconfitta.
	 * Altrimenti, si procede ricorsivamente ad aggiungere 
	 * un nuovo vertice (il passo+1-esimo), scegliendolo tra i vertici
	 * non ancora presenti nel {@code team}.
	 * 
	 * @param passo
	 * @param team
	 * @param K
	 */
	private void ricorsiva(int passo, Set<Driver> team, int K) {
		
		// caso terminale?
		if(passo==K) {
			
			// calcolare tasso di sconfitta del team
			int tasso = this.tassoSconfitta(team) ;
			
			// eventuamlente aggiornare il minimo
			if(tasso < tassoMin) {
				tassoMin = tasso ;
				teamMin = new ArrayList<>(team) ;
				
				//System.out.println(tassoMin + " "+team.toString()) ;
			}
		} else {
			
			// caso normale
			Set<Driver> candidati = new HashSet<>(graph.vertexSet()) ;
			candidati.removeAll(team) ;
			
			for(Driver d: candidati) {
				team.add(d) ;
				ricorsiva(passo+1, team, K) ;
				team.remove(d) ;
			}
			
		}	
	}

	private int tassoSconfitta(Set<Driver> team) {
		int tasso = 0 ;
		
		for(DefaultWeightedEdge e : this.graph.edgeSet()) {
			if( !team.contains(graph.getEdgeSource(e)) &&
					team.contains(graph.getEdgeTarget(e)) ) {
				tasso += graph.getEdgeWeight(e) ;
			}
		}
		
		return tasso ;
		
	}

}
