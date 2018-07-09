/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lehouillier.feature_trees;
import java.util.function.Function;
import java.util.HashMap;


/**
 *
 * @author frank
 */
public class Feature {
	
	protected Function< HashMap<String, Object>, Boolean > function;
	protected String name;

	public Feature(Function< HashMap<String, Object>, Boolean> the_function, String the_name)
	{
		name = the_name;
		function = the_function;

	}

	public boolean call(HashMap<String, Object> input)
	{
		return function.apply(input).booleanValue();
	}

}
