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
	
	Path(Graph graph){
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
	
	public Path crossover(Path other) {
		if(graph_ != other.graph_){
			return null;
		}
		Path newPath = new Path(graph_);
		newPath.addNodeUnvisited(0);
		
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
	
	public void mutate(){
		int first = rand_.nextInt(getSize());
		int second = rand_.nextInt(getSize());
		
		swap(first, second);
	}

	public void addNode(Integer node){
		if(!hasNode(node)){
			addNodeUnvisited(unvisitedIndexes_.get(node));
		}
	}
	
	public void addNodeUnvisited(int index){
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
		addNodeUnvisited(index);
	}
	
	private void swap(int firstNode, int secondNode){
		int firstIndex = edges_.indexOf(firstNode);
		int secondIndex = edges_.indexOf(secondNode);
		
		edges_.set(firstIndex, secondNode);
		edges_.set(secondIndex, firstNode);
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
	
	public static void main(String[] args) {
		Graph g = new Graph(4);
		g.randomize();

		Path p = new Path(g);
		Path p2 = new Path(g);
		Path p3 = new Path(g);
		
		p.randomize();
		p2.randomize();
		p3.randomize();
		
		Path c1 = p.crossover(p2);
		Path c2 = p.crossover(p3);
		Path c3 = p2.crossover(p3);
		
		Path d1 = c1.crossover(c2);
		Path d2 = c1.crossover(c3);
		Path d3 = c2.crossover(c3);
		
		System.out.println(d1.toString());
		System.out.println(d2.toString());
		System.out.println(d3.toString());
	}
}