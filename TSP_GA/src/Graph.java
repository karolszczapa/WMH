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
	
	public int findBestPathCost(){
		ArrayList<Integer> path = new ArrayList<Integer>();
		ArrayList<Integer> unvisited = new ArrayList<Integer>();
		for(int i = 1; i < getSize(); ++i){
			unvisited.add(i);
		}
		path.add(0);
		return countBest(path, unvisited);
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
	
	public int countCost(ArrayList<Integer> path){
		int cost = getCost(path.get(path.size() - 1), path.get(0));
		for(int i =0; i < path.size() - 1; ++i){
			cost += getCost(path.get(i), path.get(i+1));
		}
		return cost;
	}
	
	public int getCost(int i, int j){
		return costs_[i][j];
	}
	
	public int getSize(){
		return size_;
	}
}
