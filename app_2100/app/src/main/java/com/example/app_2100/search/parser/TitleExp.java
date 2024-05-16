package com.example.app_2100.search.parser;

import com.example.app_2100.search.parser.Exp;

/**
 * This class builds upon codes from lab exercises however overall content has been modified to fit our own requirement.
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
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