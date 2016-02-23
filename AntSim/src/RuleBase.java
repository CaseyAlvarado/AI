import java.util.ArrayList;


public class RuleBase {
	//The tule base data structure is an arraylist that holds rules. 
	ArrayList<Rule> ruleBase = new ArrayList<>(); 
	
	public void addToRuleBase(Rule rule){ 
		//Input: new rule 
		//Output: void 
		//This function adds the new rule to the rule base 
		if (!inRuleBase(rule)){ 
			ruleBase.add(rule); 
		}	
	}
	
	public boolean inRuleBase(Rule incomingRule){ 
		//Input: new rule 
		//Output: boolean, true if the rule is in the rule base
		//false if the rule is not in the rule base. 
		for(Rule r: ruleBase){ 
			if (r.getAntecedent().tupleEquals(incomingRule.getAntecedent()) 
				&& r.getConsequent().tupleEquals(incomingRule.getConsequent()))
			{
				return true; 					
			}
		}
		return false;
	}
	
	public void printRuleBase(){ 
		//Input: -
		//Output: void 
		//This function prints each rule in the rule base 
		for (Rule r: ruleBase){ 
			r.printRule(); 
		}
	}
	
	public Rule matchConsequent(Tuple goal){
		for (Rule r : ruleBase){ 
			Tuple cons = r.getConsequent(); 
			if (cons.tupleEquals(goal)){ 
				return r; 
			}	
		}
		return null; 
	}
	
	public Rule matchAntecdent(Tuple goal){
		for (Rule r : ruleBase){ 
			Tuple ants = r.getAntecedent(); 
			if (ants.tupleEquals(goal)){ 
				return r; 
			}	
		}
		return null; 
	}
	
}
