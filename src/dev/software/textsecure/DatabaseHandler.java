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
		try {
			mPreparedStatement = (PreparedStatement) mConnect
			          .prepareStatement("DELETE Seed  WHERE seed = ?");
			mPreparedStatement.setString(1, seed);
			mPreparedStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
