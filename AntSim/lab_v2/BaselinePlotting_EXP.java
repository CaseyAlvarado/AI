package lab_v2;


import java.util.ArrayList;

import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.JavaPlot;

public class BaselinePlotting_EXP {

	//how many steps to find food 
	//int[][] stepsToFoodForEachAlg = new int[2][2]; //makes number of rows first, 4 algorithms
	//int [][] stepsToFood = new int[50][2]; 
	 
	/**
	 * @param args
	 */
	GNUPlot gnup = new GNUPlot("/usr/bin/gnuplot");
	JavaPlot jp = new JavaPlot("/usr/bin/gnuplot");
	
	void record(AntWorld aw){
		jp.addPlot(convertResults(aw.bestResults)); 
		
		
		jp.plot(); 
	
		
		
//			if (aw.alg < 4){
//				stepsToFood[aw.runs][0] = aw.runs; 
//				stepsToFood[aw.runs][1] = aw.its; 
//				aw.alg++; 
//			}
//			else{
//				jp.addPlot(stepsToFood); 
//				jp.setTitle("Steps to find food for algorithm 1 with food in center position");
//				jp.set("xlabel", "'x'");
//				jp.set("ylabel", "'y'");
//				jp.plot(); 
//				aw.setup(); 
//				aw.draw();
//			}	
	}
	
	int[][] convertResults(ArrayList<Integer> results){
		int[][] out = new int[results.size()][2];
		for (int k = 0; k < (results.size()-1); k++){ 
			out[k][0] = k;  
			out[k][1] = results.get(k); 
		}
		return out; 		
	}
//	void record(AntWorld aw){
//
//		System.out.println("alg number" + aw.alg); 
//		System.out.println("its number" + aw.its);
//		if (aw.foodIndexY < 2){ 
//			System.out.println("foox index" + aw.foodIndexY); 
//			if (aw.alg <2){ 
//				//System.out.println("i, algorithm number" + i); 
//				stepsToFoodForEachAlg[1][0] = aw.its;
//				System.out.println("aw.alg" + aw.alg); 
//				aw.alg++; 
//				System.out.println("algorithm number updated" + aw.alg); 
//				aw.setup(); 
//				aw.draw(); 
//			}
//			else{
//				aw.alg = 0; 
//				aw.foodIndexY++; 
//				aw.setup(); 
//				aw.draw();
//			}
//			
//		}
//		else{ 
//			jp.addPlot(stepsToFoodForEachAlg); 
//			jp.setTitle("merp");
//			jp.plot(); 
//			
//		}
	

//	void newPlot(){ 
//		jp.addPlot(stepsToFoodForEachAlg); 
//		jp.setTitle("merp"); 		
//	}
	
	
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}

}
