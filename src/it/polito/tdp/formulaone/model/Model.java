package it.polito.tdp.formulaone.model;

import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private List<Season> seasons ;
	
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph ;
	
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

}
