package dev.software.textsecure;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class DatabaseHandler {
	
	private static DatabaseHandler instance;
	private Connection mConnect;
	private Statement mStatement;
	private PreparedStatement mPreparedStatement = null;
	
	private DatabaseHandler(){}
	
	public static DatabaseHandler getInstance(){
		if(instance == null)
			instance = new DatabaseHandler();
		return instance;
	}
	
	public boolean init() {
		boolean response = true;
	    try {
	      Class.forName("com.mysql.jdbc.Driver");
	      mConnect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/textsecure?"
	    		  + "user=root&password=feanylat17");
	      mStatement = (Statement) mConnect.createStatement();
	    } catch (Exception e) {
	      	System.out.println(e.getMessage());
	      	response = false;
	    } 
	    
	    return response;
	}
	
	public boolean verifySeed(String seed){
		boolean response = false;
		ResultSet resulset = null;
		try {
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("SELECT user_id FROM Seed WHERE seed = ?");
			mPreparedStatement.setString(1, seed);
			resulset = mPreparedStatement.executeQuery();
			if(resulset.first())
				response = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return response;
	}
	
	public void signUp(String seed, String imei, String phone){
		ResultSet resulset = null;
		int id = 0;
		try {
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("SELECT user_id FROM Seed WHERE seed = ?;");
			mPreparedStatement.setString(1, seed);
			resulset = mPreparedStatement.executeQuery();
			if(resulset.first()){
				id = resulset.getInt("user_id");
			}else{
				return;
			}
			mPreparedStatement.close();
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("INSERT INTO Device(imei, phone, user_id) VALUES (?, ?, ?)");
			mPreparedStatement.setString(1, imei);
			mPreparedStatement.setString(2, phone);
			mPreparedStatement.setInt(3, id);
			mPreparedStatement.executeUpdate();
			mPreparedStatement.close();
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("DELETE FROM Seed WHERE seed = ?");
			mPreparedStatement.setString(1, seed);
			mPreparedStatement.executeUpdate();
			mPreparedStatement.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public ArrayList<String[]> retrieveMessage(String phone){
		ResultSet resulset = null;
		ArrayList<String[]> messages = null;
		int id = 0;
		try {
			messages = new ArrayList<>();
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("SELECT user_id FROM Device WHERE phone = ?;");
			mPreparedStatement.setString(1, phone);
			resulset = mPreparedStatement.executeQuery();
			if(resulset.first()){
				id = resulset.getInt("user_id");
			}
			mPreparedStatement.close();
			resulset.close();
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("SELECT phone, content, image FROM Message WHERE user_id = ?");
			mPreparedStatement.setInt(1, id);
			resulset = mPreparedStatement.executeQuery();
			if(resulset.first()){
				String[] message = new String[3];
				message[0] = resulset.getString("phone");
				message[1] = resulset.getString("content");
				message[2] = resulset.getString("image");
				messages.add(message);
				while (resulset.next()) {
					message = new String[3];
					message[0] = resulset.getString("phone");
					message[1] = resulset.getString("content");
					message[2] = resulset.getString("image");
					messages.add(message);
				}
			}
			mPreparedStatement.close();
			resulset.close();
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("DELETE FROM Message WHERE user_id = ?");
			mPreparedStatement.setInt(1, id);
			mPreparedStatement.executeUpdate();
			mPreparedStatement.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return messages;
	}
	
	public void saveMessage(String sender, String remitter, String content, byte[] image){
		ResultSet resulset = null;
		int id = 0;
		try{
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("SELECT user_id FROM Device WHERE phone = ?;");
			mPreparedStatement.setString(1, remitter);
			resulset = mPreparedStatement.executeQuery();
			if(resulset.first()){
				id = resulset.getInt("user_id");
			}
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("INSERT INTO Message(phone, content, user_id) vALUES (?, ?, ?)");
			mPreparedStatement.setString(1, sender);
			mPreparedStatement.setString(2, content);
			mPreparedStatement.setInt(3, id);
			mPreparedStatement.executeUpdate();
			mPreparedStatement.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
