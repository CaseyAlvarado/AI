import java.util.ArrayList;

public class Tuple {
	//This Tuple data structure is broad enough that it can hold Strings or Tuples inside. 
	
	//This Tuple data structure is an array list that holds Objects.
	private ArrayList<Object> tupleContents = new ArrayList<Object>(); 
	
	public Tuple(Object...args) {
		// TODO Auto-generated constructor stub
		//Constructor Input: An unlimited number of objects 
		
		//For each object in the input arguments, add each object to the ArrayList tupleContents
		for (Object o : args){ 
			tupleContents.add(o); 
		}
	}
	
	public void printTuple(){ 
		//Input: - 
		//Output: void 
		//This function calls the function prints the return of the parseTuple() function 
		System.out.println(parseTuple()); 
	}
	
	public boolean tupleEquals(Tuple t){ 
		//Input: Tuple t
		//Output: boolean, true if the current Tuple and the input Tuple equal. 
		//False if the current Tuple and the input Tuple do NOT equal. 
		
		//If the tupleContent ArrayList sizes are not the same, then easily they are not equal. 
		//Return false if they do not have the same number of tuple contents 
		if (t.getSize() != tupleContents.size())
			return false; 
		
		//else go through each element in the tupleContents arrayList 
		//If each object in the arrayList is not equal, then return false. 
		for (int i = 0; i < tupleContents.size(); i++){ 
			if (tupleContents.get(i) != t.getElementByInteger(i)){ 
				return false; 
			}
		}
		
		//if it passes both of the tests above, then assume they are equal. 
		return true; 	
	}
	
	public Object getElementByInteger(int num){ 
		//Input: index number of desired element
		//Output: the Object at that index
		return tupleContents.get(num); 
	}
	
	public int getSize(){ 
		//Input: - 
		//Output: the size of the tupleContent arrayList, the number of tuple contents
		return tupleContents.size(); 
	}
	
	public String parseTuple(){
		//Input: - 
		//Output: String at the very inside of the cascading tuples 
		
		String output = "(" + "";
		
		//go through each object in tupleContent
		//if the object inside the tuple is a string, call the buildString function 
		//if the object inside the tuple is another tuple (cascading tuples), call findInnerTuple 
		//to recurse through to find the most inner tuple. 
		for(Object o : tupleContents){
			if (o instanceof String){ 
				String str = (String) o; 
				output += buildString(str); 
			}
			else if (o instanceof Tuple){
				output += "" + "(" + ""; 
				Tuple tup = (Tuple) o; 
				output += findInnerTuple(tup);
			} 
		}
		output+="" + ")"; 
		return output; 
	}
	
	private String buildString(String str){ 
		//Input: String str
		//Output: String stringContent 
		//Basically a string builder helped function 
		String stringContent = ""; 
		stringContent += str; 
		return stringContent; 
	}
	
	private String concatLettersInTuple(Tuple t){ 
		//Input: Tuple t with no inner tuples, only String letters inside. 
		//Output: String 
		
		//For each letter inside the tuple, add it to the output string 
		String stringContent = ""; 
		for (int k = 0; k < t.getSize(); k++){ 
			Object o = t.getElementByInteger(k);  
			if (o instanceof String){ 
				stringContent += o.toString();  
			}
		}	
		
		stringContent += ")";
		return stringContent; 
	}
	
	private String findInnerTuple(Tuple t){ 
		//Input: Tuple t with tuples inside, cascading or not 
		//Output: String with the entire inner tuple content 
		
		String innerTupleString = ""; 
		//For each object in input Tuple t, determine if each object is Tuple or String 
		//If the element is Tuple, then it becomes the new Tuple t
		//	and it calls this function again with this inside Tuple as the new Tuple t
		//If the element is String, calls concatLettersInTuple() to string together 
		//	all of the letters inside the tuple. 
		
		for (int j = 0; j < t.getSize(); j++){
			Object o = t.getElementByInteger(j); 
			if (o instanceof Tuple){ 
				Tuple innerTuple = (Tuple) o; 
				return findInnerTuple(innerTuple); 
			}
			else if (o instanceof String){ 
				innerTupleString = concatLettersInTuple(t); 
			}				
		}
		return innerTupleString;
	}
	
	public boolean tuplesInside(){ 
		//Input: - 
		//Output: boolean, true if the current Tuple has tuples inside of it
		//					false if the current Tuple has Strings inside of it 
		for (Object o: tupleContents){ 
			if (o instanceof Tuple){ 
				return true; 
			}
		}
		return false; 
	}
	
	
}
