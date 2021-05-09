package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	private BordersDAO dao;
	private Map<Integer, Country> idMap;
	private Graph<Country, DefaultEdge> grafo;
	private Map<Country, Country> visita;
	private List<Country> ultimo;
	
	public Model() {
		this.dao = new BordersDAO();
		this.idMap = new HashMap<>();
		dao.loadAllCountries(idMap);
	}

	public void createGraph(int anno) {
		this.grafo = new SimpleGraph<Country, DefaultEdge>(DefaultEdge.class);
		
		//aggiungo i vertici filtrati
		Graphs.addAllVertices(grafo, dao.getVertici(idMap, anno));
		
		//aggiungo gli archi
		for(Border b : dao.getCountryPairs(idMap, anno)) {
			if(this.grafo.containsVertex(b.getC1()) && this.grafo.containsVertex(b.getC2())) {
				DefaultEdge e = this.grafo.getEdge(b.getC1(), b.getC2());
				if(e == null) {
					Graphs.addEdgeWithVertices(grafo, b.getC1(), b.getC2());
				}
			}
		}
		System.out.println("Grafo creato");
		//System.out.println("#Vertici: " + grafo.vertexSet().size());
		//System.out.println("#Archi: " + grafo.edgeSet().size());
	}

	public String getGradoVertici() {
		String s = "";
		for(Country c : this.grafo.vertexSet()) {
			s += c.getStateNme() + ": " + this.grafo.degreeOf(c) + " stato/i confinante/i\n";
		}
		return s;
	}
	
	public Set<Country> getCountries() {
		return grafo.vertexSet();
	}

	public int getNumberOfConnectedComponents() {
		ConnectivityInspector<Country, DefaultEdge> ci = new ConnectivityInspector<>(grafo);
		return ci.connectedSets().size();
	}
	
	public Set<Country> getRaggiungibiliCI(Country c){
		ConnectivityInspector<Country, DefaultEdge> ci = new ConnectivityInspector<>(grafo);
		//System.out.println(""+ci.connectedSetOf(c).size());
		return ci.connectedSetOf(c);
	}
	
	public List<Country> getRaggiungibili(Country c) {
		List<Country> percorso = new LinkedList<>();
		
		DepthFirstIterator<Country, DefaultEdge> it = new DepthFirstIterator<>(grafo, c);
		
		ultimo = new ArrayList<>();
		
		visita = new HashMap<>();
		visita.put(c, null);
		
		it.addTraversalListener(new TraversalListener<Country, DefaultEdge>(){
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {}
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {}
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				Country c1 = grafo.getEdgeSource(e.getEdge());
				Country c2 = grafo.getEdgeTarget(e.getEdge());
				
				if(visita.containsKey(c1) && !visita.containsKey(c2)) {
					visita.put(c2, c1);
				} else if (visita.containsKey(c2) && !visita.containsKey(c1)){
					visita.put(c1, c2);
				}
			}
			@Override
			public void vertexTraversed(VertexTraversalEvent<Country> e) {}
			@Override
			public void vertexFinished(VertexTraversalEvent<Country> e) {
				ultimo.add(e.getVertex());
			}				
		});
		
		while(it.hasNext()) {
			it.next();
		}
		
		if(!visita.containsKey(c)) {
			return null;
		}
		
		for(Country u : ultimo) {
			if(!percorso.contains(u))
				percorso.add(u);
			
			Country step = u;
			
			while (visita.get(step) != null) {
				step = visita.get(step);
				if(!percorso.contains(step))
					percorso.add(0, step);
			}
		}
		//System.out.println(""+percorso.size());
		return percorso;
	}

}
