/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lehouillier.feature_trees;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author frank
 */
public class FeatureTree {

	protected HashSet<Object> outputs;
	protected HashSet<FeatureTree> affirmative_branch;
	protected HashSet<FeatureTree> negative_branch;
	protected Feature test;
	protected Rule rule;

	public FeatureTree(HashSet<Object> the_outputs, Rule the_rule, HashSet<FeatureTree> the_affirmative_branch,
		HashSet<FeatureTree> the_negative_branch,Feature the_test_feature)
	{
		outputs = the_outputs;
		affirmative_branch = the_affirmative_branch;
		negative_branch = the_negative_branch;
		rule = the_rule;
		test = the_test_feature;
	}


	public HashSet<Object> outputs()
	{
		return outputs;
	}

	public boolean immediateReturn()
	{
		return rule.immediateReturn();
	}

	public HashSet<Object> getResult(HashMap<String,Object> input)
	{
		HashSet<Object> collected_objects = new HashSet<Object>();
		if (outputs != null)
		{
			collected_objects.addAll(outputs);
			if (immediateReturn())
			{
				return collected_objects;
			}
		}

		if (test.call(input))
		{
			Iterator<FeatureTree> affirmative_iter = affirmative_branch.iterator();
			while (affirmative_iter.hasNext())
			{
				FeatureTree subtree = affirmative_iter.next();
				HashSet<Object> subtree_results = subtree.getResult(input);
				if (subtree_results != null)
				{
					collected_objects.addAll(subtree_results);
					if (subtree.immediateReturn())
					{
						return collected_objects;
					}
				}
			}
			return collected_objects;
		}
		else
		{
			Iterator<FeatureTree> negative_iter = negative_branch.iterator();
			while (negative_iter.hasNext())
			{
				FeatureTree subtree = negative_iter.next();
				HashSet<Object> subtree_results = subtree.getResult(input);
				if (subtree_results != null)
				{
					collected_objects.addAll(subtree_results);
					if (subtree.immediateReturn())
					{
						return collected_objects;
					}
				}
			}
			return collected_objects;
		}
	}
}
