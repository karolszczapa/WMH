import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class GeneticAlgorithm {
	private double mutationPropability_;
	private double elitismPercentage_;
    private BufferedWriter output_;

	private Graph graph_;
	private Population population_;
	private Random random_;

	public GeneticAlgorithm(int populationSize, int graphSize, double mutationPropability, double elitismPercentage, BufferedWriter output){
		graph_ = new Graph(graphSize);
		population_ = new Population(populationSize, graph_);
		random_ = new Random();
		
		mutationPropability_ = mutationPropability;
		elitismPercentage_ = elitismPercentage;
        output_ = output;
	}
	
	/**
	 * Starts genetic algorithm in give iteration count
	 * @param iterationCount
	 * @return
	 * @throws IOException
	 */
	public Population invoke(int iterationCount2) throws IOException {
		randomize();
		long start = System.currentTimeMillis();   
		int bestPathCost = 0;//graph_.findBestPathCost();
		long newStart = System.currentTimeMillis();
		long elapsedTime = newStart - start;
		start = newStart;
        output_.write("Iteration,Best Cost,Average Cost,Elapsed Time\n");
		sort();
		int actualCost;
//		int oldCost = 99999;
		int iterationCount = iterationCount2;
		for(int i = 0; i<iterationCount; ++i){
			if(i%1000 == 0){
				newStart = System.currentTimeMillis();
				elapsedTime = newStart - start;
				start = newStart;
				actualCost = population_.get(0).countCost();
                output_.write("" + i + "," + actualCost + "," + population_.averageCost() + "," + elapsedTime+"\n");
                output_.flush();
//                if(actualCost < oldCost)
//                {
//                	oldCost = actualCost;
//                	iterationCount += iterationCount2;
//                }
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
		return p <= mutationPropability_;
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
        try {
            BufferedWriter output = fileOutput();
            GeneticAlgorithm ga = new GeneticAlgorithm(1000, 40, 0.05, 0.10, output);
            Population p = ga.invoke(10001);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    private static BufferedWriter fileOutput() throws IOException {
        String fileName = new SimpleDateFormat("'TSP_GA_'yyyyMMddHHmmss'.csv'").format(new Date());
        java.nio.file.Path path = FileSystems.getDefault().getPath(".", fileName);
        return Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
    }

    private static BufferedWriter stdOutput() {
        return new BufferedWriter(new OutputStreamWriter(System.out));
    }
}
