package com.example.moudle;

public class User {
	private String name;
	private int money;
	private int rank;// ��Ա�ȼ�
	private int giftMoney;// ÿ�յ�¼���͵Ľ��
	private String time;//  �ϴ����ͽ�ҵ�ʱ��
	public User(String name) {
		super();
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getGiftMoney() {
		return giftMoney;
	}
	public void setGiftMoney(int giftMoney) {
		this.giftMoney = giftMoney;
	}
	
	
}
