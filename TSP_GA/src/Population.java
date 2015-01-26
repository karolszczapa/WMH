import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population extends ArrayList<Path> {
	private int populationSize_;
	private Random random_;
	private Graph graph_;
	
	/**
	 * create population.
	 * @param populationSize
	 * @param graph
	 */
	public Population(int populationSize, Graph graph){
		super(populationSize);
		populationSize_ = populationSize;
		graph_ = graph;
		random_ = new Random();
	}
	
	/**
	 * returns population size
	 * @return
	 */
	public int getPopulationSize(){
		return populationSize_;
	}
	
	/**
	 * returns space in population
	 * @return
	 */
	public int getRemainSize(){
		return populationSize_ - size();
	}
	
	/**
	 * creates population randomly
	 */
	public void randomize(){
		for (int i =0; i < populationSize_; ++i){
			Path p = new Path(graph_);
			p.randomize();
			add(p);
		}
	}
	
	/**
	 * Chooses path randomly.
	 * @return
	 */
	public Path randomSelect(){
		int index = random_.nextInt(populationSize_);
		return get(index);
	}
	
	/**
	 * Chooses path by tournament selection.
	 * @return
	 */
	public Path tournamentSelect(){
		Path p1 = randomSelect();
		Path p2 = randomSelect();
		
		int cost1 = p1.countCost();
		int cost2 = p2.countCost();
		
		return cost1 < cost2 ? p1 : p2;
	}
	
	/**
	 * Sort of population by there cost
	 */
	public void sort(){
		if(size() == 0){
			return;
		}
		Collections.sort(this, new PathCostComparator());
	}
	
	/**
	 * count average cost of population.
	 * @return
	 */
	public double averageCost(){
		double cost = 0;
		for(int i = 0; i < size(); ++i){
			cost += get(i).countCost();
		}
		return cost/size(); 
	}
	
	private class PathCostComparator implements Comparator<Path> {
	    @Override
	    public int compare(Path p1, Path p2) {
	    	int cost1 = p1.countCost();
	    	int cost2 = p2.countCost();
	    	if(cost1 == cost2){
	    		return 0;
	    	}
	    	else{
	    		return cost1 < cost2 ? -1 : 1;
	    	}
	    }
	}
}
