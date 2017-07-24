package com.zancheng.callphonevideoshow.assembly.org.nsg.util;

import java.util.Comparator;

import com.zancheng.callphonevideoshow.object.Linkmen;



public class PinyinComparator implements Comparator<Linkmen>
{

	public int compare(Linkmen o1, Linkmen o2)
	{
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) 
			return -1;
		else if (o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) 
			return 1;
	    else 
			return o1.getSortLetters().compareTo(o2.getSortLetters());
	}

}
