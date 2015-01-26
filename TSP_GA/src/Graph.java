import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;

public class Graph {
	private int[][] costs_;
	private int size_;
	
	public Graph(int size){
		costs_ = new int[size][size];
		size_ = size;
		zeroValues();
	}
	
	private void zeroValues(){
		for(int i = 0; i<size_; ++i)
		{
			for(int j = 0; j<size_; ++j)
			{
				costs_[i][j] = 0;
			}
		}
	}
	
	public void randomize(){
		final int MAX = 200;
		final int MIN = 1;
		final int BOUND = MAX - MIN + 1;
		
		Random rand = new Random();
		for(int i = 0; i<size_; ++i)
		{
			for(int j = i + 1; j<size_; ++j)
			{
				costs_[i][j] = costs_[j][i] = rand.nextInt(BOUND) + MIN; 
			}
		}
	}
	/**
	 * Branch and bound class which find the small cost of possibly path
	 *
	 */
	public class BranchAndBound
	{
		private Graph graph_;
		private int max_;
		private ArrayList<ArrayList<Integer>> possibleNodes_;
		private Integer FIRST_EDGE = 0;
		
		BranchAndBound(Graph graph){
			graph_ = graph;
			possibleNodes_ = new ArrayList<ArrayList<Integer>>(graph_.getSize());
			
			for(int i = 0; i <graph_.getSize(); ++i){
				possibleNodes_.add(new ArrayList<Integer>(graph_.getSize()));
				for(int j = 0; j < graph_.getSize();++j){
					if(i != j){
						possibleNodes_.get(i).add(j);
					}
				}
			}
			
			for(int i = 0; i < possibleNodes_.size(); ++i){
				Collections.sort(possibleNodes_.get(i), new NodeCostComparator(i, graph_));
			}
			
			max_ = countMax();
		}
		
		
		public int findBestCost() {
			ArrayList<Integer> path = new ArrayList<Integer>(graph_.getSize());
			addNode(path, 0, possibleNodes_, 0, null,null);
			return max_;
		}
		
		private void addNode(ArrayList<Integer> path, Integer chosenNode, ArrayList<ArrayList<Integer>> possibleNodes, int cost, Integer firstNode, ArrayList<Integer> nodesToFirstEdge){
			if(path.isEmpty()){
				firstNode = chosenNode;
				nodesToFirstEdge = initNodesToFirstEdge(firstNode);
			}
			else{
				cost += graph_.getCost(path.get(path.size() - 1), chosenNode);
				removeNodeFrom(possibleNodes, path.get(path.size() - 1), nodesToFirstEdge);
				if(nodesToFirstEdge.isEmpty())
				{
					return;
				}
				removeNodeTo(possibleNodes, chosenNode);
				if(minIsHigherThanMax(cost, possibleNodes)) return;
				updateMax(cost, possibleNodes);
			}
			
			path.add(chosenNode);
			if(path.size() >= graph_.getSize()){
				return;
			}

			
			while(!possibleNodes.get(chosenNode).isEmpty()){
				ArrayList<ArrayList<Integer>> newPossibleNodes = copyPossibleNodes(possibleNodes);
				ArrayList<Integer> newPath = new ArrayList<Integer>(path);
				ArrayList<Integer> newNodesToFirstEdge = new ArrayList<Integer>(nodesToFirstEdge);
				Integer newChosenNode = possibleNodes.get(chosenNode).get(0);
				if(newChosenNode.intValue() != firstNode.intValue()){
					addNode(newPath, newChosenNode, newPossibleNodes, cost, firstNode, newNodesToFirstEdge);
				}
				if(chosenNode == firstNode.intValue()){
					nodesToFirstEdge.remove(newChosenNode);
				}
				else if(newChosenNode == firstNode.intValue()){
					nodesToFirstEdge.remove(chosenNode);
				}
				if(nodesToFirstEdge.isEmpty())
				{
					return;
				}
				possibleNodes.get(newChosenNode).remove(chosenNode);
				possibleNodes.get(chosenNode).remove(newChosenNode);
			}
		}
		
		private ArrayList<Integer> initNodesToFirstEdge(Integer firstNode){
			ArrayList<Integer> nodesToFirstEdge = new ArrayList<Integer>(graph_.getSize());
			for(int i = 0; i<graph_.getSize(); ++i){
				if(i != firstNode.intValue()){
					nodesToFirstEdge.add(i);
				}
			}
			return nodesToFirstEdge;
		}
		
		private void removeNodeFrom(ArrayList<ArrayList<Integer>> possibleNodes, Integer from, ArrayList<Integer> nodesToFirstEdge){
			possibleNodes.get(from).clear();
			nodesToFirstEdge.remove(from);
		}

		private void removeNodeTo(ArrayList<ArrayList<Integer>> possibleNodes, Integer to){
			for(ArrayList<Integer> a: possibleNodes){
				a.remove(to);
			}
		}
		
		private ArrayList<ArrayList<Integer>> copyPossibleNodes(ArrayList<ArrayList<Integer>> possibleNodes){
			ArrayList<ArrayList<Integer>> newPossibleNodes = new ArrayList<ArrayList<Integer>>(possibleNodes.size());
			for(ArrayList<Integer> a: possibleNodes){
				newPossibleNodes.add(new ArrayList<Integer>(a));
			}
			return newPossibleNodes;
		}
		
		private boolean minIsHigherThanMax(int cost, ArrayList<ArrayList<Integer>> possibleNodes){
			int min = cost;
			for(int i = 0; i < possibleNodes.size(); ++i){
				ArrayList<Integer> a = possibleNodes.get(i);
				if(a.isEmpty()){
					continue;
				}
				min += graph_.getCost(i, a.get(0));
			}
			return min > max_;
		}
		
		private int countMax() {
			int max = 0;
			for(int i = 0; i < possibleNodes_.size(); ++i){
				ArrayList<Integer> a = possibleNodes_.get(i);
				if(a.isEmpty()){
					continue;
				}
				max += graph_.getCost(i, a.get(a.size() - 1));
			}
			return max;
		}
		
		private void updateMax(int cost, ArrayList<ArrayList<Integer>> possibleNodes) {
			int max = cost;
			for(int i = 0; i < possibleNodes.size(); ++i){
				ArrayList<Integer> a = possibleNodes.get(i);
				if(a.isEmpty()){
					continue;
				}
				max += graph_.getCost(i, a.get(a.size() - 1));
			}
			max_ = Math.min(max, max_);
		}
		
		private class NodeCostComparator implements Comparator<Integer> {
		    private Graph graph_;
		    private Integer firstEdge_;
		    
		    public NodeCostComparator(Integer edge, Graph graph){
		    	graph_ = graph;
		    	firstEdge_ = edge;
		    }
		    
			@Override
		    public int compare(Integer i1, Integer i2) {
				if (i1 == i2){
					return 0;
				}
				return graph_.getCost(firstEdge_, i1) < graph_.getCost(firstEdge_, i2) ? -1 : 1;
		    }
		}
	}
	/**
	 * Brutal force algorithm which finds the best path cost
	 * @return
	 */
	public int findBestPathCost(){
		ArrayList<Integer> path = new ArrayList<Integer>();
		ArrayList<Integer> unvisited = new ArrayList<Integer>();
		for(int i = 1; i < getSize(); ++i){
			unvisited.add(i);
		}
		path.add(0);
		return countBest(path, unvisited);
	}
	
	/**
	 * This one using brand and bound algorithm
	 * @return
	 */
	public int findBestPathCost2(){
		BranchAndBound b = new BranchAndBound(this);
		return b.findBestCost();
	}
	
	private int countBest(ArrayList<Integer> path, ArrayList<Integer> unvisited){
		int cost = -1;
		for(int i = 0; i < unvisited.size(); ++i){
			ArrayList<Integer> newPath = new ArrayList<Integer>(path);
			newPath.add(unvisited.get(i));
			ArrayList<Integer> newUnvisited = new ArrayList<Integer>(unvisited);
			newUnvisited.set(i, newUnvisited.get(newUnvisited.size() - 1));
			newUnvisited.remove(newUnvisited.size() - 1);
			int countedCost = countBest(newPath, newUnvisited);
			if(cost == -1 || countedCost < cost){
				cost = countedCost;
			}
		}
		if(unvisited.size() == 0){
			cost = countCost(path);
		}
		return cost;
	}
	
	/**
	 * Count cost of given path by array of integers
	 * @param path
	 * @return
	 */
	public int countCost(ArrayList<Integer> path){
		int cost = getCost(path.get(path.size() - 1), path.get(0));
		for(int i =0; i < path.size() - 1; ++i){
			cost += getCost(path.get(i), path.get(i+1));
		}
		return cost;
	}
	
	/**
	 * Get node's cost of edges i and i.
	 * @param i
	 * @param j
	 * @return
	 */
	public int getCost(int i, int j){
		return costs_[i][j];
	}
	
	/**
	 * return size of graph
	 * @return
	 */
	public int getSize(){
		return size_;
	}
}
