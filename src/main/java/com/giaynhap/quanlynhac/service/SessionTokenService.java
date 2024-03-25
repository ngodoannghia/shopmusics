package com.giaynhap.quanlynhac.service;

import com.amazonaws.util.Md5Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SessionTokenService {
    Map<String,String> tokens = new HashMap<>();
    @Value("${jwt.secret}")
    private String secret;
     public static class ObjectToken{
        public String username;
        public String uuid;
        public Long createAt;
        public Long expire;
        public Boolean enable;
        public String action;
    }
    public void addToken(String uuid,String token){
        tokens.put(uuid,token);
	System.out.println("add token "+uuid+"   "+token);
    }

    public void deleteToken(String uuid){
        tokens.remove("uuid");
    }

    public boolean checkToken(String token){
       return tokens.containsValue(token);
    }
    public String  refreshToken(String token){
        if (!checkToken(token)){
		
            return null;
        }
        ObjectToken objToken =  parseToken(token);
        if (objToken == null){

            return null;
        }
        if (!objToken.enable){
            return null;
        }
        return makeToken(objToken.username, objToken.uuid);
    }
    public ObjectToken validate(String token){
        System.out.println("Token validate  "+ token);
        if (!checkToken(token)){
             System.out.println("Token not found");
            return null;
        }
        try {
			ObjectToken objToken =  parseToken(token);
			@SuppressWarnings("unused")
			Long now =  LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (!objToken.enable){
				 System.out.println(" token not enable ");
				 System.out.println(token);
				 
                return null;
            }
         
          return objToken;

        } catch (Exception e){
		    System.out.println("error Token");
            return null;
        }
    }
    public ObjectToken validateDisable(String token){
        System.out.println("Token validateDisable  "+ token);

        try {
            ObjectToken objToken =  parseToken(token);
            Long now =  LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            if (now >= objToken.expire){
                System.out.println("  Token expire");
                return null;
            }
            return objToken;

        } catch (Exception e){
            System.out.println("error Token");
            return null;
        }
    }
    public ObjectToken parseToken(String token){
        String [] xf = token.split("\\.");
        if (xf.length< 2){
            return null;
        }
        try {
	
            String head = xf[0];
            String localHash = Md5Utils.md5AsBase64((head + secret).getBytes("UTF-8"));
            if (!localHash.equals(xf[1])){
		        System.out.println("hash error ");
                return null;
            }
            String strJson = new String( Base64.getDecoder().decode(head), "UTF-8");
            Gson gson = new Gson();
            ObjectToken objToken =  gson.fromJson(strJson, ObjectToken.class);
            return objToken;
        } catch(Exception e){
		System.out.println("parseToken  error ");
            return null;
        }
    }
    public String makeToken(String userName,String uuid ){
        return makeToken(userName, uuid, true , "login");
    }
    public String makeToken(String userName,String uuid , Boolean isEnable ){
        return makeToken(userName, uuid, isEnable , "register");
    }
    public String makeToken(String userName,String uuid, Boolean isEnable , String action){
        JsonObject  obj  = new JsonObject();
        obj.addProperty("username", userName);
        obj.addProperty("uuid", uuid);
        Long now =  LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        obj.addProperty("create_at", now);
        obj.addProperty("enable", isEnable);
        
        obj.addProperty("action", action);
		
		if (action != null &&  (action.equals("register")  || action.equals("forget"))){
			 obj.addProperty("expire", now + 1000*60*60*3);
		} else {
			obj.addProperty("expire", now + 1000*60*60*3);
		}
        String strJson = obj.toString();
		
       try{

           String head = Base64.getEncoder().encodeToString(strJson.getBytes("UTF-8"));
           String hash = Md5Utils.md5AsBase64((head+secret).getBytes("UTF-8"));
            return head +"."+hash;
       }catch (Exception e){

       }
        return null;
    }


}
