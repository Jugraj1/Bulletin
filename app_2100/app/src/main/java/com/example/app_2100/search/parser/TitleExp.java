package com.example.app_2100.search.parser;

import com.example.app_2100.search.parser.Exp;

/**
 * TitleExp: it is extended from the abstract class Exp. This class is used to
 * represent the expression of division
 *
 * You are not required to implement any function inside this class. Please do
 * not change anything inside this class as well.
 *
 */

public class TitleExp extends Exp {

	private final Exp wordExp;
	private Exp titleExp;

	public TitleExp(Exp wordExp, Exp titleExp) {
		this.wordExp = wordExp;
		this.titleExp = titleExp;
	}

	public TitleExp(Exp wordExp) {
		this.wordExp = wordExp;
	}

	@Override
	public String show() {
		if (titleExp == null) {
			return wordExp.show();
		}
		return wordExp.show() + " " +titleExp.show();
	}

}