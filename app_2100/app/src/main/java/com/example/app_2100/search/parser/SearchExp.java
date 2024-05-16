package com.example.app_2100.search.parser;

/**
 * This class builds upon codes from lab exercises however overall content has been modified to fit our own requirement.
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
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

	public String getTitle() {
		return titleExp.show();
	}

	public String getAuthor() {
		return authorExp.show();
	}
}