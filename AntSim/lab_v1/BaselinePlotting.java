package lab_v1;


import java.util.ArrayList;
import java.util.Arrays;

import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;

public class BaselinePlotting {

	//how many steps to find food 
	//int[][] stepsToFoodForEachAlg = new int[2][2]; //makes number of rows first, 4 algorithms 
	 
	/*
	 * @param args
	 */
	int runCount = 50; 
	GNUPlot gnup = new GNUPlot("/usr/bin/gnuplot");
	JavaPlot jp = new JavaPlot("/usr/bin/gnuplot");
	DataSetPlot ds; 
	int[][] steps; 
	PlotStyle ps = new PlotStyle(); 
	ArrayList<DataSetPlot> holder = new ArrayList<DataSetPlot>(); 
	
	void record(AntWorld aw){
		if (steps == null){ 
			 steps = new int[runCount][2]; 
		}
		System.out.println(); 
		
		if (aw.runs == (runCount -1)){  
			steps[aw.runs][0] = aw.runs; 
			steps[aw.runs][1] = aw.its; 
			ds = new DataSetPlot(steps); 
			ds.setTitle("Number of steps to food first time for algorithm: " + (aw.alg-1) + "/" + "Mean: " + calculateMeanForRunBest(steps) + "/" + "Standard Deviation: " + calculateStandardDeviation(steps));
			jp.addPlot(ds); 
			aw.runs = 0; 
//			aw.terminate2 = true; 
//			jp.plot();
			if (aw.alg <= 3){ //if there are more algorithms 
				steps = new int[runCount][2]; 
				aw.alg++; 
				aw.setup(); 
				aw.draw(); 
			}
			else{ //if no more algorithms 
				//then plot, data complete
				
//				addAllDSPToPlot(); 
				
				//ds.setPlotStyle(ps); 
				//ps.setPointSize(100); 
				aw.pause = true; 
				jp.set("xlabel", "'number of runs'"); 
				jp.set("ylabel", "'first length to food'"); 
				jp.setTitle("First step to food for 30 runs and all 4 algorithms"); 
								
				jp.getAxis("x").setBoundaries(0, runCount + 1); 
				int[] a = seperate2DArray(steps); 
				Arrays.sort(a); 
				jp.getAxis("y").setBoundaries(0, a[a.length-1] + 7);
				
				jp.plot(); 
			}
			
		}
		else {  
			steps[aw.runs][0] = aw.runs; 
			steps[aw.runs][1] = aw.its; 
			aw.runs++;
			aw.setup(); 
			aw.draw(); 		
		}
	}
	
	int[] seperate2DArray(int[][] rb){ 
		System.out.println(rb); 
		System.out.println(rb.length); 
		int[] out = new int[rb.length -1]; 
		for(int d = 0; d < (rb.length -1); d ++){
			out[d] = rb[d][1]; 	
		}
		return out; 
	}
	
	
	double calculateMeanForRunBest(int[][] r){ 
		double mean = 0; 
		for (int k = 0; k < (r.length - 1); k++){ 
			mean+=r[k][1]; 
		}
		mean /=r.length; 
		return mean; 	
	}
	
	double calculateStandardDeviation(int[][] r){ 
		double mean = calculateMeanForRunBest(r); 
		double standardDev =0; 
		
		for (int j = 0; j <(r.length -1); j++){ 
			double xi = r[j][1]; 
			standardDev += Math.pow((xi - mean), 2); 
		}
		standardDev /=r.length; 
		return Math.sqrt(standardDev); 
	}
	
}
