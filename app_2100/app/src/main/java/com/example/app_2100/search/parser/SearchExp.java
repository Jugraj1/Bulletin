package com.example.app_2100.search.parser;

/**
 * SearchExp: it is extended from the abstract class Exp.
 *         This class is used to represent the expression of addition
 *
 * You are not required to implement any function inside this class.
 * Please do not change anything inside this class as well.
 */

public class SearchExp extends Exp {

	private final Exp titleExp;
	private Exp authorExp;
	
	public SearchExp(Exp titleExp, Exp authorExp) {
		this.titleExp = titleExp;
		this.authorExp = authorExp;
	}

	public SearchExp(Exp titleExp) {
		this.titleExp = titleExp;
	}

	@Override
	public String show() {
		if (authorExp == null) {
			return "\n[Title]:" + titleExp.show();
		}
		return "\n[Title]:" + titleExp.show() + "\n[Author]:" + authorExp.show();
	}
}