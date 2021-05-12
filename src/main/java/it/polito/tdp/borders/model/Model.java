package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	private BordersDAO dao;
	private Map<Integer, Country> idMap;
	private Graph<Country, DefaultEdge> grafo;
	private Map<Country, Country> predecessore;
	
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
		System.out.println(ci.connectedSetOf(c).size());
		return ci.connectedSetOf(c);
	}
	
	public List<Country> getRaggiungibili(Country c) {
		BreadthFirstIterator<Country, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, c) ;
		
		this.predecessore = new HashMap<>() ;
		this.predecessore.put(c, null) ;
		
		bfv.addTraversalListener(new TraversalListener<Country, DefaultEdge>() {
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {}
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {}
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge arco = e.getEdge() ;
				Country a = grafo.getEdgeSource(arco);
				Country b = grafo.getEdgeTarget(arco);
				
				if(predecessore.containsKey(b) && !predecessore.containsKey(a))
					predecessore.put(a, b) ;
					
				else if(predecessore.containsKey(a) && !predecessore.containsKey(b))
					predecessore.put(b, a) ;
			}
			@Override
			public void vertexTraversed(VertexTraversalEvent<Country> e) {}
			@Override
			public void vertexFinished(VertexTraversalEvent<Country> e) {}
		});
	
		List<Country> result = new ArrayList<>() ;
		
		while(bfv.hasNext()) {
			Country f = bfv.next() ;
			result.add(f) ;
		}
		System.out.println(result.size());
		return result ;
	}
	public List<Country> getRaggiungibiliIT(Country c) {
		List<Country> daVisitare = new ArrayList<>();
		List<Country> visitati = new ArrayList<>();
		Set<DefaultEdge> archi;
		int i = 0;
		
		daVisitare.add(c);
		while(i < daVisitare.size()) {
			Country d = daVisitare.get(i);
			
			//aggiungo tutti i confinanti di d a daVisitare se non li ho già trattati
			archi = this.grafo.edgesOf(d);
			for(DefaultEdge e : archi) {
				if(!visitati.contains(this.grafo.getEdgeTarget(e)) && !daVisitare.contains(this.grafo.getEdgeTarget(e))
						&& !d.equals(this.grafo.getEdgeTarget(e))) {
					daVisitare.add(this.grafo.getEdgeTarget(e));
				}
				if(!visitati.contains(this.grafo.getEdgeSource(e)) && !daVisitare.contains(this.grafo.getEdgeSource(e))
						&& !d.equals(this.grafo.getEdgeSource(e))) {
					daVisitare.add(this.grafo.getEdgeSource(e));
				}
			}
			
			//aggiungo d a visitati se non è già presente
			if(!visitati.contains(d))
				visitati.add(d);
			
			//incremento il contatore per scorrere la lista e tenere traccia di quanti stati ho analizzato
			i++;
		}
		System.out.println(visitati.size());
		return visitati;
	}

	public List<Country> getRaggiungibiliRIC(Country c) {
		List<Country> daVisitare = new ArrayList<>();
		List<Country> visitati = new ArrayList<>();
		
		daVisitare.add(c);
		
		this.cerca(daVisitare, visitati, 0);
		
		System.out.println(visitati.size());
		return visitati;
	}

	private void cerca(List<Country> daVisitare, List<Country> visitati, int L) {
		if(L == daVisitare.size()) {
			return;
		}
		
		Country d = daVisitare.get(L);
		Set<Country> vicini;
		//aggiungo tutti i confinanti di d a daVisitare se non li ho già trattati
		vicini = Graphs.neighborSetOf(grafo, d); 
		for(Country v : vicini) {
			if(!visitati.contains(v) && !daVisitare.contains(v) && !d.equals(v)) {
				daVisitare.add(v);
			}
			if(!visitati.contains(v) && !daVisitare.contains(v) && !d.equals(v)) {
				daVisitare.add(v);
			}
		}
		
		//aggiungo d a visitati se non è già presente
		if(!visitati.contains(d)) {
			visitati.add(d);
			cerca(daVisitare, visitati, L+1);
		}
		
	}

}
