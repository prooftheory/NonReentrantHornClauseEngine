/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lehouillier.feature_trees;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author frank
 */
public class FeatureTreeBuilder {


	protected static Boolean trueFunction(HashMap<String, Object> input)  
	{
		return Boolean.TRUE;
	}
	
	protected Feature true_feature = new Feature(FeatureTreeBuilder::trueFunction,"True");
	protected Rule root_rule = new Rule("Root",null,null);

	public Feature selectNodeFeature(HashSet<Rule> rules)
	{
		HashMap<Feature,Integer> feature_count = new HashMap<Feature,Integer>();
		Iterator<Rule> rule_iter = rules.iterator();
		while (rule_iter.hasNext())
		{
			Rule rule = rule_iter.next();
			Iterator<Feature> features = rule.features().keySet().iterator();
			while (features.hasNext())
			{
				Feature feature = features.next();
				if (feature_count.containsKey(feature))
				{
					feature_count.put(feature, new Integer(feature_count.get(feature).intValue() + 1));
				}
			}
		}

		Feature node_feature = null;
		int number_called = 0;

		Iterator<Feature> keys = feature_count.keySet().iterator();

		while (keys.hasNext())
		{
			Feature feature = keys.next();
			if (feature_count.get(feature).intValue() > number_called)
			{
				node_feature = feature;
				number_called = feature_count.get(feature).intValue();
			}
		}

		return node_feature;
	}

	public HashSet<Rule> eleminateWrongRules(HashSet<Rule> rules, Feature feature, boolean value)
	{
		HashSet<Rule> output = new HashSet<Rule>();

		Iterator<Rule> rule_iter = rules.iterator();
		while (rule_iter.hasNext())
		{
			Rule rule = rule_iter.next();
			HashMap<Feature,Boolean> features = rule.features();
			if (!(features.containsKey(feature) && features.get(feature)))
			{
				output.add(rule);
			}
		}
		return output;
	}

	public Pair<HashSet<Rule>, HashSet<Rule> > partitionRulesForFeature(HashSet<Rule> rules, Feature feature)
	{
		HashSet<Rule> rules_with_feature = new HashSet<Rule>();
		HashSet<Rule> rules_without_feature = new HashSet<Rule>();
		Iterator<Rule> rules_iter = rules.iterator();
		while(rules_iter.hasNext())
		{
			Rule rule = rules_iter.next();
			if (rule.features().containsKey(feature))
			{
				rules_with_feature.add(rule);
			}
			else
			{
				rules_without_feature.add(rule);
			}
		}
		
		return new ImmutablePair<HashSet<Rule>, HashSet<Rule> >(rules_with_feature, rules_without_feature);
	}

	public Vector<Feature> featuresInRules(HashSet<Rule> rules)
	{
		Vector<Feature> output = new Vector<Feature>();
		Iterator<Rule> rules_iter = rules.iterator();
		while (rules_iter.hasNext())
		{
			Rule rule = rules_iter.next();
			output.addAll(rule.features().keySet());
		}

		return output;
	}

	public HashSet<Rule> clusterRules(HashSet<Rule> rules, Feature feature)
	{
		Pair<HashSet<Rule>, HashSet<Rule> > partitioned_rules = partitionRulesForFeature(rules,feature);
		HashSet<Rule> output = partitioned_rules.getLeft();
		HashSet<Rule> rules_without_feature = partitioned_rules.getRight();
		Vector<Feature> featuresLeft = featuresInRules(output);
		featuresLeft.remove(feature);
		Vector<Feature> featuresAccountedFor = new Vector<Feature>();
		while (!featuresLeft.isEmpty())
		{
			Feature currentFeature = featuresLeft.remove(0);
			featuresAccountedFor.add(currentFeature);
			Iterator<Rule> rules_iter = rules_without_feature.iterator();
			while (rules_iter.hasNext())
			{
				Rule rule = rules_iter.next();
				if (rule.features().keySet().contains(currentFeature))
				{
					output.add(rule);
					if (!featuresAccountedFor.contains(currentFeature))
					{
						featuresAccountedFor.add(currentFeature);
					}
				}
			}
		}

		return output;
	}

	public HashSet<FeatureTree> buildLeaves(HashSet<Rule> ruleset)
	{
		HashSet<FeatureTree> output = new HashSet<FeatureTree>();
		Iterator<Rule> rules = ruleset.iterator();
		while (rules.hasNext())
		{
			Rule rule = rules.next();
			HashSet<FeatureTree> affirmative_branch = new HashSet<FeatureTree>();
			HashSet<FeatureTree> negative_branch = new HashSet<FeatureTree>();
			HashSet<Object> outputs = new HashSet<Object>();
			outputs.add(rule.output());

			if (rule.features().isEmpty())
			{
				FeatureTree new_tree = new FeatureTree(outputs,rule,affirmative_branch,negative_branch, null);
			}
		}

		return output;
	}

	public HashSet<FeatureTree> buildBranches(HashSet<Rule> rules)
	{
		HashSet<FeatureTree> output = new HashSet<FeatureTree>();
		if (rules.isEmpty())
		{
			return output;
		}


		Feature node_feature = selectNodeFeature(rules);
		HashSet<Rule> true_rules = eleminateWrongRules(rules,node_feature,true); 
		HashSet<Rule> true_cluster = clusterRules(true_rules,node_feature); 
		HashSet<Rule> false_rules = eleminateWrongRules(rules,node_feature,false); 
		HashSet<Rule> false_cluster = clusterRules(true_rules,node_feature); 

		HashSet<Rule> raw_affirmative_branch = new HashSet<Rule>(true_cluster);
		HashSet<Rule> left_over_affirmative_branch = new HashSet<Rule>(true_rules);
		left_over_affirmative_branch.retainAll(false_rules);
		raw_affirmative_branch.addAll(left_over_affirmative_branch);
		HashSet<Rule> affirmative_branch_rules = new HashSet<Rule>();
		Iterator<Rule> affirmative_rules = raw_affirmative_branch.iterator();
		while (affirmative_rules.hasNext())
		{
			Rule rule = affirmative_rules.next();
			affirmative_branch_rules.add(rule.strip_feature(node_feature));

		}
		HashSet<FeatureTree> affirmative_branch = buildBranches(affirmative_branch_rules);
		
		HashSet<Rule> raw_negative_branch = new HashSet<Rule>(false_cluster);
		HashSet<Rule> left_over_negative_branch = new HashSet<Rule>(false_rules);
		left_over_negative_branch.retainAll(true_rules);
		raw_negative_branch.addAll(left_over_negative_branch);
		HashSet<Rule> negative_branch_rules = new HashSet<Rule>();
		Iterator<Rule> negative_rules = raw_negative_branch.iterator();
		while (negative_rules.hasNext())
		{
			Rule rule = negative_rules.next();
			negative_branch_rules.add(rule.strip_feature(node_feature));
		}
		HashSet<FeatureTree> negative_branch = buildBranches(negative_branch_rules);

		HashSet<Rule> true_and_false_rules = new HashSet<Rule>(true_rules);
		true_and_false_rules.addAll(false_rules);
		HashSet<Rule> left_over_rules = new HashSet<Rule>(rules);
		left_over_rules.removeAll(true_and_false_rules);
		HashSet<FeatureTree> branches_from_leftovers = this.buildBranches(left_over_rules);
		output.addAll(branches_from_leftovers);
		FeatureTree new_feature_tree = new FeatureTree(null, null, affirmative_branch, negative_branch, node_feature);
		output.add(new_feature_tree);
		
		return output;
	}

	public FeatureTree buildRoot(HashSet<Rule> rules)
	{
		return new FeatureTree(null,root_rule,buildBranches(rules),new HashSet<FeatureTree>(),true_feature);

	}
	
}
