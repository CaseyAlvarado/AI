import java.util.ArrayList;


public class InferenceEngine {
	//instantiate instances of the WorkingMemory and RuleBase classes 
	WorkingMemory workingMemory = new WorkingMemory(); 
	RuleBase ruleBase = new RuleBase(); 
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//then MATCH: find a rule from the rule base whose consequent matches the goal. 
		//The first discovered rule with such a characteristic should be taken
		//and its antecedents should be promoted as subgoals which have to be proven
		//The inference mechanism should be re-invoked recursively on each of these sub-goals. 
		//When all the antecedents, promoted as subgoals are satisfied, this rule fires. 
		//then SELECT
		//then ACT
		InferenceEngine ie = new InferenceEngine(); 
		Tuple goal = ie.setup(); 
		ie.select(goal); 
		ie.Act(goal); 
	}
	
	public Tuple setup(){ 
		//Input: - 
		//Output: Tuple goal 
		
		//This function makes all of the rules and adds them to the RuleBase. 
		//This function also makes all the Tuple facts and adds them to the workingMemory. 
		
		// Make new rules 
		Rule rule1 = new Rule(new Tuple(new Tuple ("f", "h"), new Tuple ("a", "c", "d")), new Tuple("b", "g"));
		Rule rule2 = new Rule(new Tuple("n", "s"), new Tuple("e", "m"));
		Rule rule3 = new Rule(new Tuple("r", "t"), new Tuple("p", "q"));
		Rule rule4 = new Rule(new Tuple(new Tuple("d", "j"), new Tuple("e", "m"), new Tuple("k", "i")), new Tuple("a", "c", "d")); 
		Rule rule5 = new Rule(new Tuple("p", "q"), new Tuple("n", "s"));
		Rule rule6 = new Rule(new Tuple("u", "v"), new Tuple("k", "i")); 
		
		//add all the rules to the rulebase 
		ruleBase.addToRuleBase(rule1);
		ruleBase.addToRuleBase(rule2);
		ruleBase.addToRuleBase(rule3);
		ruleBase.addToRuleBase(rule4);
		ruleBase.addToRuleBase(rule5);
		ruleBase.addToRuleBase(rule6);
		
		//make facts
		Tuple fact1 = new Tuple("f", "h");
		Tuple fact2 = new Tuple("d", "j");
		Tuple fact3 = new Tuple("u", "v");
		Tuple fact4 = new Tuple("r", "t");
		
		
		//add facts to the working memory 
		workingMemory.addToWorkingMemory(fact1); 
		workingMemory.addToWorkingMemory(fact2);
		workingMemory.addToWorkingMemory(fact3); 
		workingMemory.addToWorkingMemory(fact4);
		
		//define goal 
		Tuple goal = new Tuple("b", "g"); 
		return goal; 	
	}
	
	public void select(Tuple goal){
		//Input: Tuple goal to prove 
		//Output: void		
		boolean tuplesInside = goal.tuplesInside(); 
		
		//If tuplesInside is false, then no Tuples inside, only Strings. 
		//It is important to identify if there are Strings OR Tuples inside because 
		//the Tuple class allows there to be both. Different actions need to be taken for either
		if (!tuplesInside){
			recurse(goal); 
		}
		else { 
			//There were cascading tuples inside the original goal tuple 
			//For each of the tuples inside the original goal tuple, call the recurse function again with each of the inner tuples
			for (int j = 0; j < goal.getSize(); j++){ 
				Tuple insideTuple = (Tuple) goal.getElementByInteger(j); 
				recurse(insideTuple); 
			}
		}
	}
	
	public boolean recurse(Tuple goal){
		//Input: tuple goal to prove
		//Output: boolean that multiples together the result of trying to prove each tuple/part of the rule
		
		boolean result = true; 
		//If the goal is in the working memory, boolean result is multiplied with true
		if (workingMemory.inWorkingMemory(goal)){ 
			System.out.println("Goal:" + goal.parseTuple() + " is in working memory, satisfied"); 
			return (result & true); 
		}
		
		//else the goal is not in workingMemory, therefore: 
		// 1. Search rule base for the first rule whose consequent matches the goal
		// 2. Make the antecedent of the found rule, the new subgoal 
		// 3. Call this recursive function again but with the subgoal as the new goal. 
		System.out.println("Goal:" + goal.parseTuple() + " is NOT in the working memory"); 
		// 1. Search rule base for the first rule whose consequent matches the goal
		Rule firstMatchingRule = ruleBase.matchConsequent(goal);
		
		if (firstMatchingRule != null){ 
			firstMatchingRule.printRule(); 
			Tuple antecedentofMatchingRule = firstMatchingRule.getAntecedent();
			System.out.print("	New antecedent goals, TRY:"); 
			antecedentofMatchingRule.printTuple(); 
			
			// 2. Make the antecedent of the found rule, the new subgoal 
			//If there are tuples inside call this recurse function with each of the tuples inside
			if (antecedentofMatchingRule.tuplesInside()){ 
				for (int n = 0; n < antecedentofMatchingRule.getSize(); n ++){ 
					Tuple tupleInsideAntecedent = (Tuple) antecedentofMatchingRule.getElementByInteger(n); 
					// 3. Call this recursive function again but with the subgoal as the new goal
					boolean trueOrNot = recurse(tupleInsideAntecedent); 
					if (trueOrNot){ 
						System.out.println("Subgoal " + tupleInsideAntecedent.parseTuple() + " was proven true"); 
						workingMemory.addToWorkingMemory(tupleInsideAntecedent); 	
						workingMemory.printWorkingMemory(); 
						//Multiple the result boolean with true because successfully proved part of the antecedent 
						result = result & true; 	
					}
					else { 
						System.out.println("Subgoal " + tupleInsideAntecedent.parseTuple() + " was proven false"); 
						//Multiply the result total boolean with false because could not prove part of the antecedent
						//part of the antecedent was proven false. 
						result = result & false;  
					}	
				}
				//this result boolean is the original boolean multiplied with the outcome of  
				//proof from trying to prove each part of the antecedents in the rules. 
				return result;  
			}
			else 
			{ 
				//If there are no tuples inside the antecedent tuple, then call this recurse
				//function with just the antecedent
				// 3. Call this recursive function again but with the subgoal as the new goal
				return recurse(antecedentofMatchingRule); 
			}
		} 
		else //No rule could be found that matched the consequent. 
		{ 
			System.out.println("FAILURE:" + goal.parseTuple() + " the following goal is neither a consequent in the rule base nor in the working memory:");
			//Multiply the result boolean with false because could not prove tuple goal. 
			return (result & false); 
		}	
	}
	
	public void Act(Tuple goal){ 
		//Input: Tuple goal to prove 
		//Output: void 
		
		//this boolean bit is true until multiplied with a false if 
		//one of the parts of the antecedent is not in the working memory 
		boolean bit = true; 
		String nullrule = "not existant, null"; 
		Rule goalRule = ruleBase.matchConsequent(goal);
		if (goalRule != null){ 
			//states which rule to prove
			System.out.print("Rule to proven goal: ");
			goalRule.printRule(); 
			Tuple antecedent = goalRule.getAntecedent(); 
			for (int k = 0; k < antecedent.getSize(); k++){ 
				Object obj = antecedent.getElementByInteger(k); 
				//if the goal rule's antecedent has strings in it, check if the antecedent is in the working memory 
				if (obj instanceof String){ 
					boolean antecedentInWM = workingMemory.inWorkingMemory(antecedent); 
					if (antecedentInWM){ 
						bit = bit & true; 
						//provedAnt += antecedent.parseTuple(); 
						System.out.println(antecedent.parseTuple() + " in working memory"); 
					}
					else { 
						bit = bit & false; 
						System.out.println(antecedent.parseTuple() + " NOT in working memory"); 
					}
				}
				else 
				{ 
					//if the goal rule's antecedent has tuples in it, check if each of the tuples 
					//is in the working memory 
					
					Tuple t = (Tuple) antecedent.getElementByInteger(k); 
					if(workingMemory.inWorkingMemory(t)){ 
						bit = bit & true; 
						//provedAnt += t.parseTuple(); 
						System.out.print("antecedent" + t.parseTuple() + " in working memory");
						System.out.println(); 
					}
					else { 
						bit = bit & false; 
						System.out.println(t.parseTuple() + " NOT in working memory"); 
					}
				}
			}
		}
		else { 
			bit = bit & false; 
		}
		
		//printing the final verdict 
		System.out.println("		Goal" + goal.parseTuple() + " was proven: " + String.valueOf(bit));
		if (bit){ 
			System.out.println("		These were all proven " + String.valueOf(bit) + ":"  + goalRule.getAntecedent().parseTuple());
			System.out.println("		Adding goal " + goal.parseTuple() + "to working Memory"); 
			workingMemory.addToWorkingMemory(goal); 
		}
		else { 
			System.out.println("		Not all of these were proven " + nullrule);
			System.out.println("		NOT adding goal " + goal.parseTuple() + "to working Memory"); 
		}
		
		workingMemory.printWorkingMemory(); 
	
	}
}
