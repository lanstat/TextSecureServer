package dev.software.textsecure;

import java.sql.DriverManager;
import java.sql.ResultSet;

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
}
