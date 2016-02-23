import java.util.ArrayList;


public class WorkingMemory {
	//working memory is an array list data structure that holds tuple facts
	ArrayList<Tuple> workingMemory = new ArrayList<Tuple>(); 

	
	public void addToWorkingMemory(Tuple tup){ 
		//Input: tuple fact to add to the workingMemory. 
		//Output: void 
		//If the input Tuple is not yet in the workingMemory, 
		//add a Tuple fact to the workingMemory 
		if (!inWorkingMemory(tup)){ 
			workingMemory.add(tup); 
		}	
	}
	
	public boolean inWorkingMemory(Tuple goal){ 
		//Input: Goal Tuple
		//Output: boolean, true if the goal tuple is in the workingMemory
		//False if the goal tuple is NOT in the working memory 
		
		for(Tuple tup: workingMemory){ 
			if (goal.tupleEquals(tup)){
				return true; 				
			}
		}
		return false;	
	}
	
	public void printWorkingMemory(){ 
		//Input: - 
		//Output: Void
		//This function prints the tuples in the workingMemory
		System.out.println("Printing the working memory:");
		for(Tuple fact: workingMemory){ 
			fact.printTuple(); 
		}	
	}
}
