import java.util.HashMap;


public class Rule {
	//This Rule data structure uses a hash map to store the contents of the rule. 
	//The key is the antecedent, the value is the consequent
	HashMap<Tuple, Tuple> rule = new HashMap<Tuple, Tuple>(); 
	private Tuple Antecedent; 
	private Tuple Consequent; 
	
	public Rule(Tuple antecedent, Tuple consequent) {
		// TODO Auto-generated constructor stub
		//constructor needs antecedent and consequent input to make rule 
		Antecedent = antecedent; 
		Consequent = consequent; 
		rule.put(Antecedent, Consequent); 
	}
	
	public Tuple getConsequent(){ 
		//Input: - 
		//Output: Consequent tuple 
		return Consequent; 
	}
	
	public Tuple getAntecedent(){ 
		//Input: - 
		//Output: Antecedent tuple  
		return Antecedent; 
	}
	
	public boolean twoTuplesEqual(Tuple t){
		//Input: tuple 
		//Output: boolean true if the input tuple equals the current tuple 
		//boolean false if the input tuple equals the current tuple 
		if (Antecedent.tupleEquals(t)) 
			return true;
		else
			return false; 		
	}
	
	public void printRule(){ 
		//Input: - 
		//Output: void 
		//Prints the rule by printing both the antecedent and the consequent
		System.out.print("Rule fires: "); 
		String antecdent = Antecedent.parseTuple(); 
		System.out.print("If " + antecdent + ","); 
		System.out.print("then "); 
		String consequent = Consequent.parseTuple(); 
		System.out.println(consequent); 
		
		
		
	}
	
}
