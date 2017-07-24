package com.example.superclassroom;

import java.util.*;

import org.dom4j.*;


public class VipStoreDict{
    public static final String vipStore_XML_PATH = "res/exchange.xml";
    
    protected static VipStoreDict instance;
    protected List<List<VipStoreInfo>> vipStoreLists = new ArrayList<List<VipStoreInfo>>();
    protected List<VipStoreInfo> vipStoreList= new ArrayList<VipStoreInfo>();
    protected VipStoreDict(){
    }

    public static VipStoreDict getInstance(){
        if(null == instance){
            Element rootNode = CommonFunc.loadDictFile(vipStore_XML_PATH);
            VipStoreDict inst = new VipStoreDict();
            inst.loadInfos(rootNode);
            instance = inst;
        }
        return instance;
    }

    protected void loadInfos(Element rootNode){
    	vipStoreLists.clear();
        try{
            @SuppressWarnings("unchecked")
			Iterator<Element> iter = rootNode.elementIterator();
            while(iter.hasNext()){
            	
                Element typeNode = iter.next();
                @SuppressWarnings("unchecked")
				Iterator<Element> typeIter = typeNode.elementIterator();
                while(typeIter.hasNext()){
                    Element currentNode = typeIter.next();
                    VipStoreInfo info = new VipStoreInfo();
					info.id = Integer.parseInt(currentNode.attributeValue("id"));
					info.oldMoney = Integer.parseInt(currentNode.attributeValue("oldRmb"));
					info.countGoldNum = Integer.parseInt(currentNode.attributeValue("countGold"));
					info.money = Integer.parseInt(currentNode.attributeValue("rmb"));
					info.goldNum = Integer.parseInt(currentNode.attributeValue("gold"));
                    info.name = currentNode.attributeValue("name");
                    info.consumecode = currentNode.attributeValue("consumecode");
                    info.productid = currentNode.attributeValue("productid");
					info.isVisible = currentNode.attributeValue("isVisible").equals("true")?true:false; 
                    vipStoreList.add(info);
                }
             this.vipStoreLists.add(vipStoreList);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public List<VipStoreInfo> getVipStoreInfos(int type){
        return this.vipStoreLists.get(type);
    }
    
    public VipStoreInfo getVipStoreInfo(int id){
    	VipStoreInfo info=new VipStoreInfo();
    	for(VipStoreInfo info1:vipStoreList){
    		if(info1.id==id){
    			info=info1;
    		}
    	}
        return info;
    }
    
    public static class VipStoreInfo{
        public int id;
		public int goldNum;
		public int money;
		public int oldMoney;
        public String name;
        public int countGoldNum;
        public boolean isVisible;
        public String consumecode;
        public String productid;
    }

	public List<VipStoreInfo> getRecommendList() {
		List<VipStoreInfo> recommendList = new ArrayList<VipStoreInfo>();
		for(int i=0;i<vipStoreLists.size();i++){
			List<VipStoreInfo> vipStores = vipStoreLists.get(i);
			for(int j=0;j<vipStores.size();j++){
				VipStoreInfo vipStore = vipStores.get(j);
				if(vipStore.isVisible){
					recommendList.add(vipStore);
				}
			}
		}
		return recommendList;
	}
	

}
