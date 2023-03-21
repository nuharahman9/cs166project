/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.text.ParseException; 
import java.text.SimpleDateFormat; 

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }
   public static boolean isValidDate(String input){
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
      format.setLenient(false); 
      try{
         format.parse(input.trim()); 
      } catch(ParseException e){
         return false; 
      }
      return true; 


   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                System.out.println("5. Update Room Information");
                System.out.println("6. View 5 recent Room Updates Info");
                System.out.println("7. View booking history of the hotel");
                System.out.println("8. View 5 regular Customers");
                System.out.println("9. Place room repair Request to a company");
                System.out.println("10. View room repair Requests history");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql, authorisedUser); break;
                   case 4: viewRecentBookingsfromCustomer(esql, authorisedUser); break;
                   case 5: updateRoomInfo(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewBookingHistoryofHotel(esql); break;
                   case 8: viewRegularCustomers(esql, authorisedUser); break;
                   case 9: placeRoomRepairRequests(esql, authorisedUser); break;
                   case 10: viewRoomRepairHistory(esql); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return userID;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

    public static boolean isManager(Hotel esql, String user){
      try {
         String query = String.format("select Users.userType from Users where Users.userID = %s;", user);
         List<List<String>> res = esql.executeQueryAndReturnResult(query);
         String UserType = "";
            for( List<String> l : res){
               for (String s : l) {
                  UserType = s;
               }
            }

        
         if (UserType.contains("manager")){
            return true; 
         }
         return false; 


        } catch(Exception e){
                System.err.println(e.getMessage());
               return false;
        }


   }


   public static void viewHotels(Hotel esql) {
      
      try{
         System.out.print("\tEnter latitude: "); 
         Float latitude = Float.parseFloat(in.readLine()); 
         System.out.print("\tEnter longitude: "); 
         Float longitude = Float.parseFloat(in.readLine()); 
         String query = String.format("select Hotel.hotelID, Hotel.hotelName, calculate_distance(Hotel.latitude, Hotel.longitude, %e, %e) as UnitsAway from Hotel where calculate_distance(Hotel.latitude, Hotel.longitude, %e, %e) < 30;", latitude, longitude, latitude, longitude);
         int rowCount = esql.executeQueryAndPrintResult(query); 

      }catch(Exception e){
         System.out.println("\tPlease enter a valid input.\t"); 
         return; 
      }

   }
   public static void viewRooms(Hotel esql) {
    

      // try{
      //    System.out.print("\tEnter date of stay (yyyy-MM-dd): "); 
      //    String dateStr = in.readLine(); 
      //    sqlDate = Date.valueOf(dateStr);

      // }catch(IllegalArgumentException e){
      //    System.err.println(e.getMessage()); 
      //    return; 
      // }

      // try{
      //    System.out.print("\tEnter hotelID: "); 
      //    Integer hotelID = Integer.parseInt(in.readLine());
      //    String query = String.format(""); 

      // }catch(Exception e){
      //    System.err.println (e.getMessage()); 
      //    returnß; 
      // }

   }
   public static void bookRooms(Hotel esql, String authorisedUser) {
      int hotelid = -1; 
      String dateSt = ""; 
      int roomnum = -1;  
      try{
	System.out.print("\tEnter Hotel ID: "); 
	hotelid = Integer.parseInt(in.readLine()); 
	System.out.print("\tEnter Room Number: "); 
	roomnum = Integer.parseInt(in.readLine()); 
	String query = String.format( "select * from Rooms where Rooms.roomNumber = %d and Rooms.hotelID = %d;", roomnum, hotelid); 	
	int rowCt = esql.executeQuery(query); 
	if (rowCt == 0) { 
		System.out.print("\tWe're sorry. This room and hotel do not exist in our database.\t"); 
		return; 
	}
	System.out.print("\tEnter the date of your stay (YYYY-MM-dd): "); 
	dateSt = in.readLine();
   if (!isValidDate(dateSt)){
      System.out.print("\tPlease enter a valid date according to the format (YYYY-MM-dd).\t");
      return; 
   }

	query = String.format("select * from RoomBookings WHERE RoomBookings.roomNumber = %d and RoomBookings.hotelID = %d and RoomBookings.bookingDate = '%s';", roomnum, hotelid, dateSt);
        rowCt = esql.executeQuery(query); 
	
	if (rowCt != 0) {
		System.out.print("\tWe're sorry. The room you requested is not available. Please try a different date or room.\n"); 
		return; 
	}
	query = String.format("insert into RoomBookings VALUES (DEFAULT, %s, %d, %d, '%s');", authorisedUser, hotelid, roomnum, dateSt); 
      	esql.executeUpdate(query);
       	System.out.print("\tBooking successful! Your total is: "); 	
	query = String.format("select Rooms.price from Rooms where Rooms.hotelID = %d and Rooms.roomNumber = %d;", hotelid, roomnum);
	List<List<String>> res = esql.executeQueryAndReturnResult(query);
        res.forEach(i -> {
	   System.out.println(i + "\t"); 
	});	
      }catch(Exception e){ 
		//System.out.println("\tIt appears that your input was invalid! Please try again.\t"); 
      		System.err.println(e.getMessage());
	      return; 
	}
     

   }
   public static void viewRecentBookingsfromCustomer(Hotel esql, String authorisedUser) {
      try{
         String query = String.format("select RoomBookings.hotelID, RoomBookings.roomNumber, RoomBookings.bookingDate, Rooms.price from Rooms, RoomBookings where Rooms.roomNumber = RoomBookings.roomNumber and Rooms.hotelID = RoomBookings.hotelID and RoomBookings.customerID = %s order by RoomBookings.bookingDate desc limit 5;",authorisedUser); 
         int rowCount = esql.executeQueryAndPrintResult(query); 
         
      }catch(Exception e){
         System.err.println(e.getMessage()); 
      }

   }
   public static void updateRoomInfo(Hotel esql) {
      //hotelID, roomNumber, price, imageURL
      try{
         System.out.print("\tEnter Hotel ID: ");
         int hotelID = Integer.parseInt(in.readLine());
         System.out.print("\tEnter Room Number: ");
         int roomNumber = Integer.parseInt(in.readLine());
         String query = String.format( "select * from Rooms where Rooms.roomNumber = %d and Rooms.hotelID = %d;", roomNumber, hotelID); 	
         int rowCt = esql.executeQuery(query); 
         if (rowCt == 0) { 
            System.out.print("\tWe're sorry. This room and hotel do not exist in our database.\t");  
         }

         boolean updating = true;
         while(updating) {
            System.out.println("1. Update Room Price");
            System.out.println("2. Update Room imageURL");
            System.out.println("...........................");
            System.out.println("3. Back");

            switch(readChoice()) {
               case 1: System.out.print("\tEnter New Room Price: ");
                       int price = Integer.parseInt(in.readLine());

                       query = String.format("UPDATE Rooms SET Rooms.price = %d WHERE Rooms.hotelID = %d AND Rooms.roomNumber = %d;", price, hotelID, roomNumber);
                       esql.executeUpdate(query);
                       System.out.println("\tRoom Price has been updated!");
                       break;
               case 2: System.out.print("\tEnter New Image URL: ");
                       String url = in.readLine();

                       query = String.format("UPDATE Rooms SET imageURL = %d WHERE hotelID = %d AND roomNumber = %d;", url, hotelID, roomNumber);
                       esql.executeUpdate(query);
                       System.out.println("\tRoom imageURL has been updated!");
                       break;
               case 3: updating = false; break;
               default: System.out.print("Invalid Input, please try again"); break;
               //INSERT stuff
            }
         }
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }

   public static void viewRecentUpdates(Hotel esql) {
      try{
         System.out.print("\tEnter Hotel ID: ");
         int hotelID = Integer.parseInt(in.readLine());
         String query = String.format("SELECT * FROM RoomUpdatesLog R WHERE R.hotelID = %d;", hotelID);
         int rowCt = esql.executeQuery(query);
         if(rowCt == 0) {
            System.out.print("Sorry, that hotel is not in our database.");
         }

         query = String.format("(SELECT * FROM RoomUpdatesLog WHERE hotelID = %s ORDER BY updated ON DESC LIMIT 5) ORDER BY updatedON ASC;", hotelID);
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewBookingHistoryofHotel(Hotel esql) {
      //bookingID, customer name, hotelID, roomNumber, bookingDate
      try{
         System.out.print("\tEnter Hotel ID: ");
         int hotelID = Integer.parseInt(in.readLine());
         String query = String.format( "SELECT * FROM Rooms WHERE Rooms.hotelID = %d;", hotelID); 	
         int rowCt = esql.executeQuery(query); 
         if (rowCt == 0) { 
            System.out.print("\tWe're sorry. This room and hotel do not exist in our database.");  
         }

         System.out.print("\tEnter Starting Booking Date: ");
         String sDate = in.readLine();
         if (!isValidDate(sDate)){
            System.out.print("\tPlease enter a valid date according to the format (YYYY-MM-dd).");
            return; }
         System.out.print("\tEnter Ending Booking Date: ");
         String eDate = in.readLine();
         if (!isValidDate(sDate)){
            System.out.print("\tPlease enter a valid date according to the format (YYYY-MM-dd).");
            return; }
            
         }catch(Exception e){
            System.err.println(e.getMessage());
         }
   }
   public static void viewRegularCustomers(Hotel esql, String authorisedUser) {
      try{
         if (!(isManager(esql, authorisedUser))){
            System.out.println("\tWhoops! We're sorry, this option is only available for managers."); 
            return; 
         };
         System.out.print("\tEnter hotelID: ");
         int hotelid = Integer.parseInt(in.readLine()); 
         String query = String.format("select * from Hotel where Hotel.managerID = %s and Hotel.hotelID = %d;", authorisedUser, hotelid); 
         int rowCount = esql.executeQuery(query); 
         if (rowCount == 0){
            System.out.print("\tWe're sorry. Please enter a valid hotel."); 
            return; 
         }
         query = String.format("select Users.userID, Users.name, count(distinct RoomBookings.bookingID) as numberBookings from Users, Hotel, RoomBookings where Users.userID = RoomBookings.customerID and RoomBookings.hotelID = %s group by Users.userID, Users.name order by count(RoomBookings.bookingID) desc limit 5;", authorisedUser); 
         System.out.print("\tThe top 5 customers in this hotel are: "); 
         esql.executeQueryAndPrintResult(query); 


      }catch(Exception e){
         System.err.println(e.getMessage()); 
         return; 
      }



   }
   public static void placeRoomRepairRequests(Hotel esql, String authorisedUser) {
      //hotelID, roomNumber, companyID
      try{
         System.out.print("Enter Hotel ID: ");
         int hotelID = Integer.parseInt(in.readLine());
         System.out.print("Enter Room Number: ");
         int roomNumber = Integer.parseInt(in.readLine());

         String query = String.format("SELECT * FROM Rooms WHERE Rooms.hotelID = %d AND Rooms.roomNumber = %d;", hotelID, roomNumber);
         int rowCt = esql.executeQuery(query); 
         if (rowCt == 0) { 
            System.out.print("\tWe're sorry. This room and hotel do not exist in our database.");  
         }

         System.out.print("Enter Company ID: ");
         int companyID = Integer.parseInt(in.readLine());
         query = String.format("SELECT * FROM MaintenanceCompany mc WHERE mc.companyID = %d;", companyID);
         rowCt = esql.executeQuery(query);
         if(rowCt == 0) {
            System.out.print("\tWe're sorry. This Maintenance Company does not exist in our database.");  
         }
         
        // query = String.format("INSERT INTO RoomRepairs VALUES (DEFAULT, %d, %d, %d, '%s');", companyID, hotelID, roomNumber, GETDATE())


      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewRoomRepairHistory(Hotel esql) {}

}//end Hotel

