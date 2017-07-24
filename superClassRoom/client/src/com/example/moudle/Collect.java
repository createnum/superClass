package com.example.moudle;

public class Collect {
	public int id;
	public String name;
	public int article_id;
	
	public Collect(String name, int article_id) {
		this.name = name;
		this.article_id = article_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getArticle_id() {
		return article_id;
	}
	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}
	
}
