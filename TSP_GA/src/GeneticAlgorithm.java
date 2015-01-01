import java.util.Random;


public class GeneticAlgorithm {
	private double mutationPropability_;
	private double elitismPercentage_;
	
	private Graph graph_;
	private Population population_;
	private Random random_;

	public GeneticAlgorithm(int populationSize, int graphSize, double mutationPropability, double elitismPercentage){
		graph_ = new Graph(graphSize);
		population_ = new Population(populationSize, graph_);
		random_ = new Random();
		
		mutationPropability_ = mutationPropability;
		elitismPercentage_ = elitismPercentage;
	}
	
	public Population invoke(int iterationCount){
		randomize();
		long start = System.currentTimeMillis();   
		int bestPathCost = 0;//graph_.findBestPathCost();
		long newStart = System.currentTimeMillis();
		long elapsedTime = newStart - start;
		start = newStart;
		System.out.println("Best path cost: "+ bestPathCost+ " " +" elapsed: " +elapsedTime);
		sort();
		for(int i = 0; i<iterationCount; ++i){
			if(i%1000 == 0){
				newStart = System.currentTimeMillis();
				elapsedTime = newStart - start;
				start = newStart;
				System.out.println("" + i + ". Best: "+population_.get(0).countCost()+" Average: "+population_.averageCost()+" elapsed: "+elapsedTime);
			}
			Population newPop = createNewPopulation();
			int remainSpace = newPop.getRemainSize();
			for(int j = 0; j < remainSpace; ++j){
				Path p1 = select();
				Path p2 = select();
				
				Path child = crossover(p1, p2);
				mutate(child);
				newPop.add(child);
			}
			population_ = newPop;
			sort();
		}
		return population_;
	}
	
	public void randomize(){
		population_.randomize();
		graph_.randomize();
	}
	
	private boolean canMutate(){
		double p = random_.nextDouble();
		return p <= mutationPropability_ ? true : false;
	}
	
	private Path select(){
		return population_.tournamentSelect();
	}
	
	private Path crossover(Path p1, Path p2){
		return p1.crossover(p2);
	}
	
	private void mutate(Path path){
		if(canMutate()){
			path.mutate();
		}
	}
	
	private void sort(){
		population_.sort();
	}
	
	private Population createNewPopulation(){
		Population newPopulation = new Population(population_.getPopulationSize(), graph_);
		rewriteElite(newPopulation);
		return newPopulation;
	}
	
	private void rewriteElite(Population newPopulation){
		int count = (int)(elitismPercentage_ * population_.size());
		
		for(int i = 0; i< count; ++i){
			newPopulation.add(population_.get(i));
		}
	}
	
	public static void main(String[] args) {
		GeneticAlgorithm ga = new GeneticAlgorithm(1000, 40, 0.05, 0.10);
		Population p = ga.invoke(10001);
	}
}
