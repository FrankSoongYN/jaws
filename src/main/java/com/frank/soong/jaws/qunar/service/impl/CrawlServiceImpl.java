package com.frank.soong.jaws.qunar.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.frank.soong.jaws.qunar.bean.QunarInfoList;
import com.frank.soong.jaws.qunar.mappers.QunarInfoListMapper;
import com.frank.soong.jaws.qunar.service.ICrawlService;
import com.frank.soong.jaws.util.HttpUtil;
import com.frank.soong.jaws.util.MongoDBJDBC;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
@Service("crawlService")
public class CrawlServiceImpl implements ICrawlService{
	@Autowired
	private QunarInfoListMapper qunarInfoListMapper;
	private Gson gson=new Gson();
	public int queryListInfo() {

		return 0;

	}

	public  void qunarCrawlJob(){
		long total=getNumFound();
		for(long i=0;i<total;i=i+100){
			getQunarInfo(i,100)	;
		}
	}

	public void getQunarInfo(Long start,int limit){
		Date d= new Date();
		SimpleDateFormat sdf= new SimpleDateFormat ("yyyyMMdd");
		String s=sdf.format(d);
		String url="http://dujia.qunar.com/golfz/routeList/adaptors/pcTop?isTouch=0&t=all&o=pop-desc&lm="+start+"%2C"+limit+"&fhLimit=0%2C20&q=%E4%B8%BD%E6%B1%9F&d=%E4%B8%8A%E6%B5%B7&s=all&qs_ts=1459820617572&tm=ign_newb&sourcepage=list&qssrc=eyJ0cyI6IjE0NTk4MjA2MTc1NzIiLCJzcmMiOiJhbGwuZW52YiIsImFjdCI6InNjcm9sbCJ9&m=l%2CbookingInfo%2Clm&displayStatus=pc&lines6To10=0";
		String re=HttpUtil.doGet(url);
		System.out.println(re);
		if(null!=re){
			Map<String,Object> reMap=gson.fromJson(re, new TypeToken<Map<String,Object>>(){}.getType());
			System.out.println(reMap.get("ret")); 

			if(null!=reMap.get("ret")){
				boolean rb=(Boolean) reMap.get("ret");
				if(rb){
					@SuppressWarnings("unchecked")
					Map<String,Object> DataMap=(Map<String, Object>) reMap.get("data");
					System.out.println(""+DataMap.get("limit"));

					@SuppressWarnings("unchecked")
					Map<String,Object> LisMap=(Map<String, Object>) DataMap.get("list");
					if(null!=LisMap.get("results")){
						System.out.println(LisMap.get("numFound"));
						String qlistStr=gson.toJson(LisMap.get("results"));
						System.out.println("infoList:"+qlistStr);
						JsonArray ja=gson.fromJson(qlistStr,new TypeToken<JsonArray>(){}.getType());
						//List<QunarInfoList> infoList=g.fromJson(qlistStr, new TypeToken<List<QunarInfoList>>(){}.getType());

						MongoDBJDBC.insert(ja,start.intValue(),s);

					}

				}
			}
		}
	}

	public  Long getNumFound(){
		Long count=0l;
		String url="http://dujia.qunar.com/golfz/routeList/adaptors/pcTop?isTouch=0&t=all&o=pop-desc&lm=0%2C2&fhLimit=0%2C20&q=%E4%B8%BD%E6%B1%9F&d=%E4%B8%8A%E6%B5%B7&s=all&qs_ts=1459820617572&tm=ign_newb&sourcepage=list&qssrc=eyJ0cyI6IjE0NTk4MjA2MTc1NzIiLCJzcmMiOiJhbGwuZW52YiIsImFjdCI6InNjcm9sbCJ9&m=l%2CbookingInfo%2Clm&displayStatus=pc&lines6To10=0";
		String re=HttpUtil.doGet(url);
		if(null!=re){
			Gson g=new Gson();
			Map<String,Object> reMap=g.fromJson(re, new TypeToken<Map<String,Object>>(){}.getType());
			System.out.println(reMap.get("ret")); 

			if(null!=reMap.get("ret")){
				boolean rb=(Boolean) reMap.get("ret");
				if(rb){
					@SuppressWarnings("unchecked")
					Map<String,Object> DataMap=(Map<String, Object>) reMap.get("data");
					System.out.println(""+DataMap.get("limit"));

					@SuppressWarnings("unchecked")
					Map<String,Object> LisMap=(Map<String, Object>) DataMap.get("list");
					if(null!=LisMap.get("results")){
						System.out.println("产品总数："+LisMap.get("numFound"));
						Double nf=(Double) LisMap.get("numFound");
						count=nf.longValue();
					}

				}
			}
		}
		return count;
	}

	public List<QunarInfoList> findListFromMonggo(String collectionName,Integer start,Integer end){
		List<QunarInfoList> list=new ArrayList<QunarInfoList>();
		FindIterable<Document> findIterable=MongoDBJDBC.query(collectionName, start, end);
		MongoCursor<Document> mongoCursor = findIterable.iterator();  
		while(mongoCursor.hasNext()){  
			//System.out.println(mongoCursor.next());  
			Document next=mongoCursor.next();
			//System.out.println(next.get("sort"));
			Map<String,Object> reMap=gson.fromJson(next.get("description").toString(), new TypeToken<Map<String,Object>>(){}.getType());
			//System.out.println(reMap);
			QunarInfoList ql=trasferToinfo(reMap);
			ql.setSort(new Integer(next.get("sort")+""));
			ql.setCreateTime(collectionName);
			list.add(ql);
		}
		return list;
	}

	public int dealListInfo() {
		int a=0;
		for(int c=0;c<6100;c=c+100){
			int d=foreach(c,c+100);
			a=a+d;
		}
		return a;
	}
	
	private int foreach(int start,int end){
		Date d= new Date();
		SimpleDateFormat sdf= new SimpleDateFormat ("yyyyMMdd");
		String s=sdf.format(d);
		//String s="20160415";
		List<QunarInfoList> list=findListFromMonggo(s,start,end);
		int a=0;
		for(QunarInfoList qi:list){
			System.out.println(qi);
			try{
				int b=	qunarInfoListMapper.insert(qi);
				if(b>0){
					a++;
				}
			}catch(Exception e){
				qi.setSummary("");
				qi.setLocaldepartures("");
				qunarInfoListMapper.insert(qi);
			}
			
			
		}
		return a;
	}

	private QunarInfoList trasferToinfo(Map<String,Object> reMap){
		QunarInfoList ql=new QunarInfoList();
		if(reMap.containsKey("hotelPos"))
			ql.setHotelpos(reMap.get("hotelPos").toString());
		Double duv=(Double) reMap.get("duv");
		ql.setDuv( duv.intValue());
		ql.setDiscount(reMap.get("discount").toString());
		ql.setType(reMap.get("type").toString());
		ql.setDep(reMap.get("dep").toString());
		ql.setWrpparid(reMap.get("wrpparid").toString());
		if(reMap.containsKey("activitiesInfo"))
			ql.setActivitiesinfo(reMap.get("activitiesInfo").toString());
		Boolean istuan=(Boolean) reMap.get("isTuan");
		if(istuan){
			ql.setIstuan(1);
		}else{
			ql.setIstuan(0);
		}
		ql.setScore(reMap.get("score").toString());
		if(reMap.containsKey("visaPreBookDaysMax"))
			ql.setVisaprebookdaysmax(reMap.get("visaPreBookDaysMax").toString());
		Double price=(Double) reMap.get("price");
		ql.setPrice(price.intValue());
		ql.setTuanttsid(reMap.get("tuanTtsId").toString());
		ql.setLinesubjects(reMap.get("lineSubjects").toString());
		Boolean hasCallOnline=(Boolean) reMap.get("hasCallOnline");
		if(hasCallOnline){
			ql.setHascallonline(1);
		}else{
			ql.setHascallonline(0);
		}
		if(reMap.containsKey("bmapGroupId")){
			ql.setBmapgroupid(reMap.get("bmapGroupId").toString());
		}
		ql.setRoutegrade(reMap.get("routeGrade").toString());
		ql.setId(reMap.get("id").toString());
		if(reMap.containsKey("bmapGroupType")){
			ql.setBmapgrouptype(reMap.get("bmapGroupType").toString());
		}
		Boolean bookableTomorrow=(Boolean) reMap.get("bookableTomorrow");
		if(bookableTomorrow){
			ql.setBookabletomorrow(1);
		}else{
			ql.setBookabletomorrow(0);
		}
		if(reMap.containsKey("productFeatures"))
			ql.setProductfeatures(reMap.get("productFeatures").toString());
		//ql.setImgs(reMap.get("imgs").toString());
		Double childNum=(Double) reMap.get("childNum");
		ql.setChildnum(childNum.intValue());
		if(reMap.containsKey("totraffic"))
			ql.setTotraffic(reMap.get("totraffic").toString());
		ql.setHotelgradetext(reMap.get("hotelGradeText").toString());
		Boolean bookableWeekend=(Boolean) reMap.get("bookableWeekend");
		if(bookableWeekend){
			ql.setBookableweekend(1);
		}else{
			ql.setBookableweekend(0);
		}
		ql.setProductpromotions(reMap.get("productPromotions").toString());
		Double isSd=(Double) reMap.get("isSd");
		ql.setIssd(isSd.intValue());
		ql.setCandidatetitle(reMap.get("candidateTitle").toString());
		ql.setProductscore(reMap.get("productScore").toString());
		Double click=(Double) reMap.get("click");
		ql.setClick(click.intValue());
		ql.setEncodeid(reMap.get("encodeId").toString());
		Boolean selfOrder=(Boolean) reMap.get("selfOrder");
		if(selfOrder){
			ql.setSelforder(1);
		}else{
			ql.setSelforder(0);
		}
		ql.setTrafficinfo(reMap.get("trafficInfo").toString());
		ql.setHotelfee(reMap.get("hotelFee").toString());
		String freetripPickup=(String) reMap.get("freetripPickup");
		if("False".equals(freetripPickup)){
			ql.setFreetrippickup(0);
		}else{
			ql.setFreetrippickup(1);
		}
		ql.setAccominclude(reMap.get("accomInclude").toString());
		ql.setVisaurgentstate(reMap.get("visaUrgentState").toString());
		//ql.setExtensionimg(reMap.get("extensionImg").toString());
		ql.setSights(reMap.get("sights").toString());
		Boolean bookableToday=(Boolean) reMap.get("bookableToday");
		if(bookableToday){
			ql.setBookabletoday(1);
		}else{
			ql.setBookabletoday(0);
		}
		Double totalPrice=(Double) reMap.get("totalPrice");
		ql.setTotalprice(totalPrice.intValue());
		ql.setThumb(reMap.get("accomInclude").toString());
		ql.setTranslator(reMap.get("accomInclude").toString());
		if(reMap.containsKey("soldCount90")){
			Double soldCount15=(Double) reMap.get("soldCount15");
			ql.setSoldcount15(soldCount15.intValue());
		}
		if(reMap.containsKey("soldCount90")){
			Double soldCount90=(Double) reMap.get("soldCount90");
			ql.setSoldcount90(soldCount90.intValue());
		}
		
		ql.setHotelgrade(reMap.get("hotelGrade").toString());
		if(reMap.containsKey("sightPoint"))
			ql.setSightpoint(reMap.get("sightPoint").toString());
		Boolean b2c=(Boolean) reMap.get("b2c");
		if(b2c){
			ql.setB2c(1);
		}else{
			ql.setB2c(0);
		}
		if(reMap.containsKey("advanceDay")){
			Double advanceDay=(Double) reMap.get("advanceDay");
			ql.setAdvanceday(advanceDay.intValue());
		}
		ql.setProducttag(reMap.get("productTag").toString());
		ql.setBusitype(reMap.get("busiType").toString());
		ql.setLocaldepartures(reMap.get("localDepartures").toString());
		ql.setDateofdeparture(reMap.get("dateofdeparture").toString());
		if(reMap.containsKey("backtraffic"))
			ql.setBacktraffic(reMap.get("backtraffic").toString());
		Double adultNum=(Double) reMap.get("adultNum");
		ql.setAdultnum(adultNum.intValue());
		ql.setWifigetway(reMap.get("wifiGetWay").toString());
		Boolean accomInterview=(Boolean) reMap.get("accomInterview");
		if(accomInterview){
			ql.setAccominterview(1);
		}else{
			ql.setAccominterview(0);
		}
		ql.setDappsales(reMap.get("dappSales").toString());
		Double dpv=(Double) reMap.get("dpv");
		ql.setDpv(dpv.intValue());
		if(reMap.containsKey("mobFunction"))
			ql.setMobfunction(reMap.get("mobFunction").toString());
		if(reMap.containsKey("longPlanId"))
			ql.setLongplanid(reMap.get("longPlanId").toString());
		ql.setWrapperscore(reMap.get("wrapperScore").toString());
		ql.setUrl(reMap.get("url").toString());
		ql.setHighlights(reMap.get("highlights").toString());
		if(reMap.containsKey("wrapperStar"))
			ql.setWrapperstar(reMap.get("wrapperStar").toString());
		ql.setAlldate(reMap.get("allDate").toString());
		ql.setSightticket(reMap.get("sightTicket").toString());
		ql.setProductrate(reMap.get("productRate").toString());
		ql.setBookingnum(reMap.get("bookingNum").toString());
		if(reMap.containsKey("pid")){
			Double pid=(Double) reMap.get("pid");
			ql.setPid(pid.intValue());
		}
		ql.setSales(reMap.get("sales").toString());
		ql.setSightspotB(reMap.get("sightspot_b").toString());
		if(reMap.containsKey("soldCount")){
			Double soldCount=(Double) reMap.get("soldCount");
			ql.setSoldcount(soldCount.intValue());
		}
		ql.setRoomnum(reMap.get("roomNum").toString());
		if(reMap.containsKey("reviews"))
			ql.setReviews(reMap.get("reviews").toString());
		ql.setDetails(reMap.get("details").toString());
		ql.setTrafficfee(reMap.get("trafficFee").toString());
		Boolean complement=(Boolean) reMap.get("complement");
		if(complement){
			ql.setComplement(1);
		}else{
			ql.setComplement(0);
		}
		ql.setSearchTitle(reMap.get("search_title").toString());
		ql.setMonths(reMap.get("months").toString());
		if(reMap.containsKey("hotelTypes")){
			ql.setHoteltypes(reMap.get("hotelTypes").toString());
		}
		
		Boolean noExtraPayment=(Boolean) reMap.get("noExtraPayment");
		if(noExtraPayment){
			ql.setNoextrapayment(1);
		}else{
			ql.setNoextrapayment(0);
		}
		ql.setSourceurl(reMap.get("sourceurl").toString());
		ql.setHotelinfo(reMap.get("hotelInfo").toString());
		if(reMap.containsKey("teamType"))
			ql.setTeamtype(reMap.get("teamType").toString());
		ql.setTwoleveltype(reMap.get("twoLeveltype").toString());
		if(reMap.containsKey("bmapGroupDest")){
			ql.setBmapgroupdest(reMap.get("bmapGroupDest").toString());
		}
		//ql.setCandidateimage(reMap.get("candidateImage").toString());
		ql.setDateofexpire(reMap.get("dateofexpire").toString());
		Boolean groupGuarantee=(Boolean) reMap.get("groupGuarantee");
		if(groupGuarantee){
			ql.setGroupguarantee(1);
		}else{
			ql.setGroupguarantee(0);
		}
		ql.setFeaturetag(reMap.get("featureTag").toString());
		Boolean noShopping=(Boolean) reMap.get("noShopping");
		if(noShopping){
			ql.setNoshopping(1);
		}else{
			ql.setNoshopping(0);
		}
		Double originalPrice=(Double) reMap.get("originalPrice");
		ql.setOriginalprice(originalPrice.intValue());
		if(reMap.containsKey("candidateImageId"))
			ql.setCandidateimageid(reMap.get("candidateImageId").toString());
		ql.setTitle(reMap.get("title").toString());
		ql.setCitys(reMap.get("citys").toString());
		if(reMap.containsKey("longPlanTitle"))
			ql.setLongplantitle(reMap.get("longPlanTitle").toString());
		ql.setAroundshorttitle(reMap.get("aroundShortTitle").toString());
		ql.setPricedate(reMap.get("priceDate").toString());
		String freetripVisa=(String) reMap.get("freetripVisa");
		if("False".equals(freetripVisa)){
			ql.setFreetripvisa(0);
		}else{
			ql.setFreetripvisa(1);
		}
		ql.setArrive(reMap.get("arrive").toString());
		//if(reMap.containsKey("sightspotImages"))
		//	ql.setSightspotimages(reMap.get("sightspotImages").toString());
		ql.setRoutetype(reMap.get("routeType").toString());
		if(reMap.containsKey("cityPoint"))
			ql.setCitypoint(reMap.get("cityPoint").toString());
		ql.setSummary(reMap.get("summary").toString());
		
		//System.out.println(ql);
		return ql;
	}

}
