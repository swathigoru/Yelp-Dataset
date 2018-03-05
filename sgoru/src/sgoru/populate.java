package sgoru;

import java.awt.List;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.json.*;

public class populate {

	public static ArrayList<String> Categories = new ArrayList<String>(
			Arrays.asList("Active Life", "Arts & Entertainment", "Automotive", "Car Rental",
					"Cafes", "Beauty & Spas", "Convenience Stores", "Dentists", "Doctors", "Drugstores", "Department Stores",
					"Education", "Event Planning & Services", "Flowers & Gifts", "Food", "Health & Medical", "Home Services",
					"Home & Garden", "Hospitals", "Hotels & Travel", "Hardware Stores", "Grocery", "Medical Centers",
					"Nurseries & Gardening", "Nightlife", "Restaurants", "Shopping", "Transportation"));
	

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		String yelp_business_file;
		String yelp_review_file;
		String yelp_checkin_file;
		String yelp_user;
		if ( (args.length == 4)) {
			yelp_business_file = args[0];
			yelp_review_file = args[1];
			yelp_checkin_file = args[2];
			yelp_user = args[3];
		} else {
			yelp_business_file = "C:/Users/lakkip/Desktop/yelp_business.json";
			yelp_review_file = "C:/Users/lakkip/Desktop/yelp_review.json";
			yelp_checkin_file = "C:/Users/lakkip/Desktop/yelp_checkin.json";
			yelp_user = "C:/Users/lakkip/Desktop/yelp_user.json";
		}

		Connection dbConn = null;
		try {
			dbConn = dbOpen();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Started preprocessing data..");
		
	    populate_business(dbConn, yelp_business_file, yelp_checkin_file);
	    System.out.println("Populated business, business_hours, categories, battributes tables!");
	    populate_reviews(dbConn, yelp_review_file);
	    System.out.println("Populated reviews table!");
	    populate_users(dbConn, yelp_user);
	    System.out.println("Populated user table!");
	    
	    System.out.println("Completed populating database. Exiting.");
		dbClose(dbConn);
	}

    /** 
     * @return a database connection 
     * @throws java.sql.SQLException when there is an error when trying to connect database 
     * @throws ClassNotFoundException when the database driver is not found. 
     */ 
    private static Connection dbOpen() throws SQLException, ClassNotFoundException { 
        // Load the Oracle database driver 
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

        String host = "localhost"; 
        String port = "1521"; 
        String dbName = "orcl"; 
        String userName = "sgoru"; 
        String password = "sgoru"; 
 
        // Construct the JDBC URL 
        String dbURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName; 
        return DriverManager.getConnection(dbURL, userName, password); 
    } 
 
    /** 
     * Close the database connection 
     * @param con 
     */ 
    private static void dbClose(Connection con) { 
        try { 
            con.close(); 
        } catch (SQLException e) { 
            System.err.println("Unable to close connection: " + e.getMessage()); 
        } 
    }
    
    public static void populate_users(Connection conn, String path) {
    	PreparedStatement prepStmtUser = null;
    	try {
			conn.setAutoCommit(false);
			
			String truncateUsersTable = "TRUNCATE TABLE users";
			Statement stmnt = conn.createStatement();
			System.out.println("Truncating user table..");
			stmnt.executeUpdate(truncateUsersTable);
			stmnt.close();
			System.out.println("Populating user table..");
			prepStmtUser = conn.prepareStatement("INSERT INTO users (user_id, user_name) VALUES (?, ?)");
			BufferedReader userBR = new BufferedReader(new FileReader(path));
			String sLine = null;
			while ((sLine = userBR.readLine()) != null) {
				JSONObject users = new JSONObject(sLine);
				prepStmtUser.setString(1, users.getString("user_id"));
				prepStmtUser.setString(2, users.getString("name"));
				prepStmtUser.addBatch();
			}
			prepStmtUser.executeBatch();
			userBR.close();
			prepStmtUser.close();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    public static void populate_reviews(Connection conn, String path) {
    	PreparedStatement prepStmtReview = null;
    	try {
			conn.setAutoCommit(false);
			String truncateReviewsTable = "TRUNCATE TABLE reviews";
			Statement stmnt = conn.createStatement();
			System.out.println("Truncating reviews table..");
			stmnt.executeUpdate(truncateReviewsTable);
			stmnt.close();
			prepStmtReview = conn.prepareStatement("INSERT INTO reviews (review_id, user_id, stars, publish_date, bid, text, funny_votes, useful_votes, cool_votes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			System.out.println("Populating reviews table..");
			BufferedReader reviewBR = new BufferedReader(new FileReader(path));
			String sLine = null;
			while ((sLine = reviewBR.readLine()) != null) {
				JSONObject reviews = new JSONObject(sLine);
				JSONObject votes = reviews.getJSONObject("votes");
				prepStmtReview.setString(1, reviews.getString("review_id"));
				prepStmtReview.setString(2, reviews.getString("user_id"));
				prepStmtReview.setInt(3, reviews.getInt("stars"));
				prepStmtReview.setString(4,reviews.getString("date"));
				prepStmtReview.setString(5, reviews.getString("business_id"));
				prepStmtReview.setString(6, reviews.getString("text"));
				prepStmtReview.setLong(7, votes.getLong("funny"));
				prepStmtReview.setLong(8, votes.getLong("useful"));
				prepStmtReview.setLong(9, votes.getLong("cool"));
				prepStmtReview.executeUpdate();
			}
			reviewBR.close();
			prepStmtReview.close();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    public static void populate_business(Connection conn, String path, String checkinPath) {
    	
		PreparedStatement prepStmtBusiness = null;
		PreparedStatement prepstmtBusinessCategories = null;
		PreparedStatement prepstmtBusinessHours = null;
		PreparedStatement prepstmtBusinessAttributes = null;
		PreparedStatement businessUpdatePS = null;

		try {
			
			conn.setAutoCommit(false);

			String truncateBusinessHoursTable = "TRUNCATE TABLE business_hours";
			Statement stmnt = conn.createStatement();
			System.out.println("Truncating business_hours table..");
			stmnt.executeUpdate(truncateBusinessHoursTable);
			stmnt.close();
			
			String truncateBusinessCategories = "TRUNCATE TABLE categories";
			stmnt = conn.createStatement();
			System.out.println("Truncating categories table..");
			stmnt.executeUpdate(truncateBusinessCategories);
			stmnt.close();
			
			String truncateAttributes = "TRUNCATE TABLE battributes";
			stmnt = conn.createStatement();
			System.out.println("Truncating battributes table..");
			stmnt.executeUpdate(truncateAttributes);
			stmnt.close();			
			
			String truncateBusinessTable = "DELETE business";
			stmnt = conn.createStatement();
			System.out.println("Truncating business table..");
			stmnt.executeUpdate(truncateBusinessTable);
			stmnt.close();
			
			prepStmtBusiness = conn.prepareStatement("INSERT INTO business (bid, full_address, open_status, breview_count, bname, loc, bstars, checkin_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			prepstmtBusinessHours = conn.prepareStatement("INSERT INTO business_hours (bid, dayname, openhr, closehr, loc) VALUES (?, ?, ?, ?, ?)");
			prepstmtBusinessCategories = conn.prepareStatement("INSERT INTO categories (bid, cat_name, subcat_name) VALUES (?, ?, ?)");
			prepstmtBusinessAttributes = conn.prepareStatement("INSERT INTO battributes (bid, att_name) VALUES (?, ?)");
			businessUpdatePS = conn.prepareStatement("UPDATE business SET checkin_count = ? WHERE bid = ?");
			
			System.out.println("Populating business, business_hours, categories, battributes tables...");
			String sLine;
			BufferedReader br = new BufferedReader(new FileReader(path));
			while ((sLine = br.readLine()) != null) {
				JSONObject business = new JSONObject(sLine);

				String b_id = business.getString("business_id");
				String loc = business.getString("city") + "," + business.getString("state");
				float stars = ((Double) business.get("stars")).floatValue();
				prepStmtBusiness.setString(1, b_id);
				prepStmtBusiness.setString(2,  business.getString("full_address"));
				prepStmtBusiness.setString(3,  business.getBoolean("open")? "true" : "false");
				prepStmtBusiness.setInt(4, business.getInt("review_count"));
				prepStmtBusiness.setString(5, business.getString("name"));
				prepStmtBusiness.setString(6, loc);
				prepStmtBusiness.setFloat(7, stars);
				prepStmtBusiness.setInt(8, 0);
				
				prepStmtBusiness.addBatch();
				
				if (business.has("hours")) {
					JSONObject openHours = business.getJSONObject("hours");
					Iterator<?> hoursKeys = openHours.keys();

					String str;
					while (hoursKeys.hasNext()) {
						String key = (String) hoursKeys.next();
                        String openTime = new String();
                        String closeTime = new String();
						switch (key) {
						case "Monday":
							prepstmtBusinessHours.setString(2, "Monday");
							openTime = openHours.getJSONObject("Monday").getString("open");
							closeTime = openHours.getJSONObject("Monday").getString("close");
							break;
						case "Tuesday":
							prepstmtBusinessHours.setString(2, "Tuesday");
							openTime = openHours.getJSONObject("Tuesday").getString("open");
							closeTime = openHours.getJSONObject("Tuesday").getString("close");
							break;
						case "Wednesday":
							prepstmtBusinessHours.setString(2, "Wednesday");
							openTime = openHours.getJSONObject("Wednesday").getString("open");
							closeTime = openHours.getJSONObject("Wednesday").getString("close");
							break;
						case "Thursday":
							prepstmtBusinessHours.setString(2, "Thursday");
							openTime = openHours.getJSONObject("Thursday").getString("open");
							closeTime = openHours.getJSONObject("Thursday").getString("close");
							break;
						case "Friday":
							prepstmtBusinessHours.setString(2, "Friday");
							openTime = openHours.getJSONObject("Friday").getString("open");
							closeTime = openHours.getJSONObject("Friday").getString("close");
							break;
						case "Saturday":
							prepstmtBusinessHours.setString(2, "Saturday");
							openTime = openHours.getJSONObject("Saturday").getString("open");
							closeTime = openHours.getJSONObject("Saturday").getString("close");
							break;
						case "Sunday":
							prepstmtBusinessHours.setString(2, "Sunday");
							openTime = openHours.getJSONObject("Sunday").getString("open");
							closeTime = openHours.getJSONObject("Sunday").getString("close");
							break;
						default:
							break;
						}
 
						openTime = openTime.replace(":", ".");
						closeTime = closeTime.replace(":", ".");
						prepstmtBusinessHours.setString(1, b_id);
						prepstmtBusinessHours.setFloat(3, Float.parseFloat(openTime));
						prepstmtBusinessHours.setFloat(4, Float.parseFloat(closeTime));
						prepstmtBusinessHours.setString(5, loc);
						prepstmtBusinessHours.addBatch();
					}
				}
				
				if (business.has("categories")) {
					JSONArray catObj = business.getJSONArray("categories");
					ArrayList <String> mainCatList = new ArrayList<String>();
					ArrayList <String> subCatList = new ArrayList<String>();
					String category = null;
					
					for (int i =0; i < catObj.length(); i++) {
						category =  catObj.get(i).toString();
						if (Categories.contains(category)) {
							mainCatList.add(category);
						} else {
							subCatList.add(category);
						}

					}
					if (!mainCatList.isEmpty()) {
						for (String mainCat : mainCatList ) {
							for(String subCat : subCatList) {
								prepstmtBusinessCategories.setString(1, b_id);
								prepstmtBusinessCategories.setString(2, mainCat);
								prepstmtBusinessCategories.setString(3, subCat);
								prepstmtBusinessCategories.addBatch();
							}
						}
					}
				}
				
				
				if (business.has("attributes")) {				
					JSONObject attributes = business.getJSONObject("attributes");
					Iterator<?> keys = attributes.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						if (attributes.get(key) instanceof JSONObject) {
							JSONObject innerAttributes = attributes.getJSONObject(key);
							Iterator<?> innerKeys = innerAttributes.keys();
							while (innerKeys.hasNext()) {
								String innerKey = (String) innerKeys.next();
								innerKey = key + "_" + innerKey + "_" + innerAttributes.get(innerKey);
								prepstmtBusinessAttributes.setString(1, b_id);
								prepstmtBusinessAttributes.setString(2, innerKey);
								prepstmtBusinessAttributes.addBatch();
						    }
						} else {
							key = key + "_" + attributes.get(key);
							prepstmtBusinessAttributes.setString(1, b_id);
							prepstmtBusinessAttributes.setString(2, key);
							prepstmtBusinessAttributes.addBatch();
							}
						}
					}
			}

			BufferedReader checkinBr = new BufferedReader(new FileReader(checkinPath));
			while ((sLine = checkinBr.readLine()) != null) {
				JSONObject checkin = new JSONObject(sLine);
				
				String b_id = checkin.getString("business_id");
				JSONObject checkin_info = checkin.getJSONObject("checkin_info");
				Iterator<?> keys = checkin_info.keys();
				int totcheckin = 0;
				while (keys.hasNext()) {
					totcheckin += checkin_info.getInt((String) keys.next());
				}
                
				businessUpdatePS.setInt(1, totcheckin);
				businessUpdatePS.setString(2, b_id);
				businessUpdatePS.addBatch();
			}

			br.close();
			checkinBr.close();
			prepStmtBusiness.executeBatch();
			businessUpdatePS.executeBatch();
			prepstmtBusinessHours.executeBatch();
			prepstmtBusinessCategories.executeBatch();
			prepstmtBusinessAttributes.executeBatch();
			conn.commit();
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prepStmtBusiness != null && !prepStmtBusiness.isClosed()) {
					prepStmtBusiness.close();
				}
				if (prepstmtBusinessCategories != null && !prepstmtBusinessCategories.isClosed()) {
					prepstmtBusinessCategories.close();
				}
				if (prepstmtBusinessHours != null && !prepstmtBusinessHours.isClosed()) {
					prepstmtBusinessHours.close();
				}
				if (prepstmtBusinessAttributes != null && !prepstmtBusinessAttributes.isClosed()) {
					prepstmtBusinessAttributes.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }   
}
