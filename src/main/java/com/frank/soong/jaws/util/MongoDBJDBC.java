package com.frank.soong.jaws.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDBJDBC{
	private static MongoDatabase mongoDatabase;
	private static MongoClient mongoClient;

	public static  MongoDatabase getDB(){
		if(mongoDatabase ==null){
			try{
				mongoClient = new MongoClient( "115.29.113.55" , 27017 );
				mongoDatabase = mongoClient.getDatabase("test");  
				System.out.println("Connect to database successfully");
			}catch(Exception e){
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			}
		}
		return mongoDatabase;
	}
	
	public static void closeClient(){
		if(null!=mongoClient){
			mongoClient.close();
		}
	}

	public static MongoCollection<Document> createCollection(String collectionName){
		try{
		MongoCollection<Document> collection=null;
		String cn="atest";
		if(null!=collectionName){
			cn=collectionName;
		}
		
			getDB();
			collection = mongoDatabase.getCollection(cn);
			System.out.println("collection： "+cn+" 连接成功");
			return collection;
		}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return null;
	}

	public static void insert(JsonArray ja,int start,String CollectionName){
		int s=0;
		if(0!=start){
			s=start;
		}
		MongoCollection<Document> collection=createCollection(CollectionName);
		List<Document> documents = new ArrayList<Document>();  
		for(int i=0;i<ja.size();i++){
			Document document = new Document("sort", s+i).  
					append("description", ja.get(i).toString()); 
			documents.add(document);   
		}
		
		collection.insertMany(documents);  
		System.out.println("数据插入成功");
	}

	public static void get(String collectionName){
		MongoCollection<Document> collection=createCollection(collectionName);
		FindIterable<Document> findIterable = collection.find();  
		MongoCursor<Document> mongoCursor = findIterable.iterator();  
		while(mongoCursor.hasNext()){  
			System.out.println(mongoCursor.next());  
		} 
	}
	public static void main( String args[] ){
		MongoClient mongoClient = new MongoClient( "115.29.113.55" , 27017 );
		try{   
			MongoDatabase mongoDatabase = mongoClient.getDatabase("test");  
			System.out.println("Connect to database successfully");
			
			MongoCollection<Document> collection = mongoDatabase.getCollection("20160422");
			/*Document document = new Document("title", "MongoDB").  
	         append("description", "database").  
	         append("likes", 100).  
	         append("by", "Fly");  
	         List<Document> documents = new ArrayList<Document>();  
	         documents.add(document);  
	         collection.insertMany(documents);  
	          */

			/*Gson gson=new Gson();
			BasicDBObject query = new BasicDBObject("sort", new BasicDBObject("$lt", 2));
			FindIterable<Document> findIterable = collection.find(query); 
			
			MongoCursor<Document> mongoCursor = findIterable.iterator();  
			while(mongoCursor.hasNext()){  
				//System.out.println(mongoCursor.next());  
				Document next=mongoCursor.next();
				System.out.println(next.get("sort"));
				Map<String,Object> reMap=gson.fromJson(next.get("description").toString(), new TypeToken<Map<String,Object>>(){}.getType());
				System.out.println(reMap);
			}*/
			System.out.println(collection.count());
			//collection.drop();
		}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}finally{
			mongoClient.close();
		}
	}
	
	public static void insetqunar(JsonArray ja,int start,String collectionName){
		MongoClient mongoClient = new MongoClient( "115.29.113.55" , 27017 );
		try{   
		MongoDatabase mongoDatabase = mongoClient.getDatabase("test");  
		System.out.println("Connect to database successfully");

		int s=0;
		if(0!=start){
			s=start;
		}
		String cn="test";
		if(null!=collectionName){
			cn=collectionName;
		}
		MongoCollection<Document> collection = mongoDatabase.getCollection(cn);
		List<Document> documents = new ArrayList<Document>();  
		for(int i=0;i<ja.size();i++){
			Document document = new Document("sort", s+i).  
					append("description", ja.get(i).toString()); 
			documents.add(document);   
		}
		collection.insertMany(documents); 
		}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}finally{
			mongoClient.close();
		}
	}
	
	public static FindIterable<Document> query(String collectionName,Integer start,Integer end){
		MongoCollection<Document> collection=createCollection(collectionName);
		BasicDBObject bo= new BasicDBObject();
		if(null!=start){
			bo.append("$gt", start);
		}
		if(null!=end){
			bo.append("$lte", end);
		}
		if(null==start&&null==end){
			FindIterable<Document> findIterable = collection.find();
			return findIterable;
		}else{
			BasicDBObject query = new BasicDBObject("sort", bo);
			FindIterable<Document> findIterable = collection.find(query);
			return findIterable;
		}
		
	}
}
