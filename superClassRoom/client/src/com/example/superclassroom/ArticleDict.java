package com.example.superclassroom;

import java.util.*;

import org.dom4j.*;


public class ArticleDict{
    public static final String ARTICLE_XML_PATH = "res/article.xml";
    
    protected static ArticleDict instance;
    protected List<List<ArticleInfo>> articleLists = new ArrayList<List<ArticleInfo>>();
    protected List<String> typeList = new ArrayList<String>();
    protected ArticleDict(){
    }

    public static ArticleDict getInstance(){
        if(null == instance){
            Element rootNode = CommonFunc.loadDictFile(ARTICLE_XML_PATH);
            ArticleDict inst = new ArticleDict();
            inst.loadInfos(rootNode);
            instance = inst;
        }
        return instance;
    }

    protected void loadInfos(Element rootNode){
    	articleLists.clear();
        try{
            @SuppressWarnings("unchecked")
			Iterator<Element> iter = rootNode.elementIterator();
            while(iter.hasNext()){
            	
            	List<ArticleInfo> articleList = new ArrayList<ArticleInfo>();
                Element typeNode = iter.next();
                String typeName = typeNode.attributeValue("typeName");
                @SuppressWarnings("unchecked")
				Iterator<Element> typeIter = typeNode.elementIterator();
                while(typeIter.hasNext()){
                    Element currentNode = typeIter.next();
                    ArticleInfo info = new ArticleInfo();
					info.id = Integer.parseInt(currentNode.attributeValue("id"));
                    info.goldNum = Integer.parseInt(currentNode.attributeValue("gold"));
                    info.title = currentNode.attributeValue("title");
                    info.path = currentNode.attributeValue("path");
					info.isRecommend = currentNode.attributeValue("isRecommend").equals("true")?true:false; 
                    articleList.add(info);
                }
             this.typeList.add(typeName);
             this.articleLists.add(articleList);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public List<ArticleInfo> getArticleInfos(int type){
        return this.articleLists.get(type);
    }
    
    public ArticleInfo getArticleInfo(int type, int id){
        return this.articleLists.get(type).get(id);
    }
    
    public static class ArticleInfo{
        public int id;
		public int goldNum;
        public String title;
        public String path;
        public boolean isCollection;
        public boolean isBought;
        public boolean isRecommend;
        
    }

	public List<ArticleInfo> getRecommendList() {
		List<ArticleInfo> recommendList = new ArrayList<ArticleInfo>();
		for(int i=0;i<articleLists.size();i++){
			List<ArticleInfo> articles = articleLists.get(i);
			for(int j=0;j<articles.size();j++){
				ArticleInfo article = articles.get(j);
				if(article.isRecommend){
					recommendList.add(article);
				}
			}
		}
		return recommendList;
	}
	public List<ArticleInfo> getArticleList(List<Integer> list) {
		List<ArticleInfo> recommendList = new ArrayList<ArticleInfo>();
		for(int i=0;i<articleLists.size();i++){
			List<ArticleInfo> articles = articleLists.get(i);
			for(int j=0;j<articles.size();j++){
				ArticleInfo article = articles.get(j);
				if(article.isRecommend && list.contains(article.id)){
					recommendList.add(article);
				}
			}
		}
		return recommendList;
	}

}
