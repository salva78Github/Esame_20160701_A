package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;
import it.polito.tdp.formulaone.exception.Formula1Exception;

public class Model {

	private static final FormulaOneDAO dao = new FormulaOneDAO();
	private Map<Integer, Driver> driversIdMap;
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph;

	public Model() {
		this.driversIdMap = new HashMap<Integer, Driver>();
	}

	public List<Season> getAllSeasons() throws Formula1Exception {
		// TODO Auto-generated method stub
		return dao.getAllSeasons();
	}

	public List<Driver> getAllDriversByYear(int year) throws Formula1Exception {
		return dao.getAllDriversByYear(year, driversIdMap);
	}

	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> creaGrafo(int year) throws Formula1Exception {

		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

		List<Driver> vertexList = getAllDriversByYear(year);
		System.out.println("<creaGrafo> numero vertici/esibizioni: " + vertexList.size());
		// crea i vertici del grafo
		Graphs.addAllVertices(graph, vertexList);

		// crea gli archi del grafo --versione 3
		// faccio fare tutto il lavoro al dao
		// che mi dà la lista della coppia dei vertici

		for (Driver d : vertexList) {
			System.out.println("<creaGrafo> Pilota: " + d);
			List<DriverBeaten> driversBeaten = dao.getDriversBeatenByYear(year, d, driversIdMap);
			for (DriverBeaten db : driversBeaten) {
				DefaultWeightedEdge dwe = this.graph.addEdge(d, db.getDriver());
				this.graph.setEdgeWeight(dwe, db.getBeatsNumber());
			}
		}

		System.out.println("<creaGrafo> numero archi: " + this.graph.edgeSet().size());

		return this.graph;

	}

	public Driver retrieveBestDriver(int year) throws Formula1Exception {
		creaGrafo(year);
		int bestScore = 0;
		Driver bestDriver = null;
		int score = 0;
		for (Driver d : this.graph.vertexSet()) {
			score = this.graph.outDegreeOf(d) - this.graph.inDegreeOf(d);
			if (score > bestScore) {
				bestDriver = d;
				bestDriver.setScore(score);
				bestScore = score;
			}
		}

		return bestDriver;

	}

	public List<Driver> retrieveDreamTeam(int dimensione) {
		List<Driver> dreamTeam = new ArrayList<Driver>();

		List<Driver> soluzioneParziale = new ArrayList<Driver>();

		recursive(0, dimensione, soluzioneParziale, dreamTeam, 0);

		return dreamTeam;
	}

	private void recursive(int level, int dimensione, List<Driver> soluzioneParziale, List<Driver> soluzioneOttima,
			int tassoMinimo) {

		if (level == dimensione) {
			int tassoSoluzioneParziale = getTassoSconfitta(soluzioneParziale);
			if (soluzioneOttima.size() == 0 || tassoSoluzioneParziale < tassoMinimo) {
				soluzioneOttima.clear();
				soluzioneOttima.addAll(soluzioneParziale);
				tassoMinimo = tassoSoluzioneParziale;
			}
			return;
		}

		for (Driver d : this.graph.vertexSet()) {

			if (!soluzioneParziale.contains(d)) {
				// genera soluzione nuova parziale
				soluzioneParziale.add(d);
				recursive(level + 1, dimensione, soluzioneParziale, soluzioneOttima, tassoMinimo);
				// backtracking
				soluzioneParziale.remove(d);
			}

		}

	}

	private int getTassoSconfitta(List<Driver> team) {
		int tasso = 0;

		// Il tasso di sconfitta di un team è definito come il numero totale di
		// vittorie di un qualsiasi pilota non appartenente al team su un
		// qualsiasi pilota appartenente al team.
		for (Driver d : team) {
			for (DefaultWeightedEdge dwe : this.graph.incomingEdgesOf(d)) {
				tasso += this.graph.getEdgeWeight(dwe);
			}

		}

		return tasso;

	}

}
