/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lehouillier.feature_trees;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author frank
 */
public class Rule {

	protected String name;
	protected Object output;
	protected boolean immediate_return;
	protected HashMap<Feature,Boolean> features;
	
	public String name()
	{
		return name;
	}

	public boolean immediateReturn()
	{
		return immediate_return;
	}

	public Object output()
	{
		return output;
	}

	public HashMap<Feature,Boolean> features()
	{
		return features;
	}

	public Rule(String n, HashMap<Feature,Boolean> f, Object o)
	{
		name = n;
		features = f;
		output = o;
	}

	public Rule strip_feature(Feature exclude)
	{
		Iterator<Feature> f = features.keySet().iterator();
		HashMap<Feature,Boolean> new_features = new HashMap<Feature,Boolean>();
		while (f.hasNext())
		{
			Feature feature = f.next();
			if (feature != exclude )
			{
				new_features.put(feature, features.get(feature));
			}
		}

		return new Rule(name,new_features,output);

	}
	
}
