package lab;


import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.JavaPlot;

public class BaselinePlotting {

	//how many steps to find food 
	//int[][] stepsToFoodForEachAlg = new int[2][2]; //makes number of rows first, 4 algorithms
	int [][] stepsToFood = new int[50][2]; 
	 
	/**
	 * @param args
	 */
	GNUPlot gnup = new GNUPlot("/usr/bin/gnuplot");
	JavaPlot jp = new JavaPlot("/usr/bin/gnuplot");
	
//	void record(AntWorld aw){
//		System.out.println("its number" + aw.its);
//		System.out.println("runs number" + aw.runs); 
//		
//			if (aw.runs < 50){
//				stepsToFood[aw.runs][0] = aw.runs; 
//				stepsToFood[aw.runs][1] = aw.its; 
//				aw.runs++; 
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
//
//		
//	}
//	
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
