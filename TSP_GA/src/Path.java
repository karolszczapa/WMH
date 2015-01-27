import java.util.ArrayList;
import java.util.Random;

public class Path {
	private ArrayList<Integer> edges_;
	private ArrayList<Integer> unvisited_;
	private ArrayList<Integer> unvisitedIndexes_;
	private Integer lastNode_;
	private Random rand_; 
	private Graph graph_;
	private int cost_;
	
	private final int NOT_CONNECTED = -1;
	private final int NOT_COUNTED = -1;
	private final int VISITED = -1;
	
	/**
	 * Constructor which initializes every data
	 * @param graph Graph in which path is localized
	 */
	public Path(Graph graph){
		edges_ = new ArrayList<Integer>(graph.getSize());
		unvisited_ = new ArrayList<Integer>(graph.getSize());
		unvisitedIndexes_ = new ArrayList<Integer>(graph.getSize());
		rand_ = new Random();
		graph_ = graph;
		cost_ = NOT_COUNTED;
		
		for(int i = 0; i < graph.getSize(); i++){
			edges_.add(NOT_CONNECTED);
			unvisited_.add(i);
			unvisitedIndexes_.add(i);
		}
	}
	/**
	 * crossover two paths to create new one
	 * On begin, there is chosen one edge randomly. 
	 * Than we take edges, connected by nodes to last added edge, from both parent paths.
	 * We compare their cost and choose one which smaller one. 
	 * If one of chosen edge is already in child path, we chose second one.
	 * If both of them is in child path, we add random, still non-added one.
	 * @param other second path
	 * @return
	 */
	public Path crossover(Path other) {
		if(graph_ != other.graph_){
			return null;
		}
		Path newPath = new Path(graph_);
		newPath.addUnvisitedNode(rand_.nextInt(graph_.getSize()));
		
		for(int i = 0; i<getSize() - 1; ++i)
		{
			int lastNode = newPath.lastNode_;
			int first = getEdgeNode(lastNode);
			int second = other.getEdgeNode(lastNode);

			if(newPath.hasNode(first)){
				if(newPath.hasNode(second)){
					newPath.addRandomUnvisitedNode();
				}
				else{
					newPath.addNode(second);
				}
			}
			else if(newPath.hasNode(second)){
				newPath.addNode(first);
			}
			else{
				int firstCost = graph_.getCost(lastNode, first);
				int secondCost = graph_.getCost(lastNode, second);
				int chosen = firstCost < secondCost ? first : second;
				newPath.addNode(chosen);
			}
		}
		return newPath;
	}
	/**
	 * mutation - chooses random two cities and swaps them
	 */
	public void mutate(){
		int first = rand_.nextInt(getSize());
		int second = rand_.nextInt(getSize());
		
		swap(first, second);
	}

	/**
	 * Add node to path
	 * @param node
	 */
	public void addNode(Integer node){
		if(!hasNode(node)){
			addUnvisitedNode(unvisitedIndexes_.get(node));
		}
	}
	/**
	 * Add unvisited node to path by index in unvisited table
	 * @param index
	 */
	public void addUnvisitedNode(int index){
		if(unvisited_.size() == 0){
			return;
		}
		Integer node = unvisited_.get(index);
		if(unvisited_.size() == getSize()){
			edges_.set(node, node);
			lastNode_ = node;
		}
		else{
			edges_.set(node, edges_.get(lastNode_));
			edges_.set(lastNode_, node);
		}
	    visit(index);
	}
	
	/**
	 * Create random path
	 */
	public void randomize(){
		int[] values = new int[getSize()];
		for(int i =0; i < getSize(); ++i)
		{
			values[i] = i;
		}
		for(int i = getSize() - 1; i>=0; --i)
		{
			addRandomUnvisitedNode();
		}
	}
	
	/**
	 * Counts cost of path
	 * @return
	 */
	public int countCost(){
		if(unvisited_.size() != 0){
			return NOT_COUNTED;
		}
		if(cost_ == NOT_COUNTED){
			cost_ = 0;
			for (int i = 0;i < edges_.size(); ++i){
				cost_ += graph_.getCost(i, edges_.get(i));
			}
		}
		return cost_;
	}
	
	/**
	 * Visit edge, what means, removes from unvisited and unvisitedIdexes arrays. 
	 * There is used fast remove which last element assign to removing one and remove last one to void costly array copying.
	 * @param index
	 */
	private void visit(int index){
		int lastElement = unvisited_.get(unvisited_.size() - 1);
		unvisitedIndexes_.set(unvisited_.get(index), VISITED);
		unvisitedIndexes_.set(lastElement, index);
		unvisited_.set(index, lastElement);
		unvisited_.remove(unvisited_.size() - 1);
	}
	
	private int getEdgeNode(int i){
		return edges_.get(i);
	}
	
	private boolean hasNode(int i){
		return edges_.get(i) != NOT_CONNECTED;
	}
	
	private void addRandomUnvisitedNode(){
		int index = rand_.nextInt(unvisited_.size());
		addUnvisitedNode(index);
	}
	
	private void swap(int firstNode, int secondNode){
		int firstIndex = edges_.indexOf(firstNode);
		int secondIndex = edges_.indexOf(secondNode);
		
		edges_.set(firstIndex, secondNode);
		edges_.set(secondIndex, firstNode);
		
		int swapValue = edges_.get(firstNode);
		edges_.set(firstNode, edges_.get(secondNode));
		edges_.set(secondNode, swapValue);
	}
	
	public int getSize(){
		return graph_.getSize();
	}
	
	private ArrayList<Integer> getNodes(){
		ArrayList<Integer> nodes = new ArrayList<Integer>(getSize());
		Integer el = 0;
		do{
			nodes.add(el);
			el = edges_.get(el);
		}while(el != 0);
		return nodes;
	}
	
	public String toString(){
		ArrayList<Integer> nodes = getNodes();
		return nodes.toString();
	}
}
