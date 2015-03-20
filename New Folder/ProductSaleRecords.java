import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductSaleRecords {
	private String salesDbUrl,salesDbDriver, salesDbUser, salesDbPwd;
	Connection salesDBConnection = null;


Map<String, ReportRecord0> customerProductMap0;
Map<String, ReportRecord1> customerProductMap1;


FileHandler hand;
	Logger log;
	String logLevel="1000";
	Properties props;
	String logFileName = "";

	String custProdReportFile;  //save report to plain text file
    
	public static void main(String[] args) {
		ProductSaleRecords sale = new ProductSaleRecords();
        if (args.length < 1) {
            System.out.println("Usage: \r\n Correct parameters is:" + "<SalesPropertyFile>");
            System.exit(1);
        }
        System.out.println("Starting ProductSaleRecords ...");
		sale.init(args[0]);
		sale.processSales();
	}
	
	ProductSaleRecords() {//constructor.
customerProductMap0 = new HashMap<String, ReportRecord0>();
customerProductMap1 = new HashMap<String, ReportRecord1>();
   
	}

	void init(String propertiesFile){      
        props = new Properties(); //load property file
        
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            System.out.println("Program cannot run without a properties file");
            e.printStackTrace();
           System.exit(1);
        }
        
        //file for logger outputs
        logFileName = props.getProperty("logFileName", "SALES.LOG");
        logLevel = props.getProperty("logLevel", "INFO"); //default to INFO 
        custProdReportFile = props.getProperty("custProdReportFile", "CUSTPROD.txt");
        
        
		try {
			hand = new FileHandler(logFileName);
			log = Logger.getLogger("ProductSaleRecords");
			log.addHandler(hand);
			log.setLevel(Level.parse(logLevel));		      
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectSalesDb();
	}
	void connectSalesDb(){
		salesDbUrl = props.getProperty("salesDbUrl");
		salesDbDriver = props.getProperty("salesDbDriver");
		salesDbUser = props.getProperty("salesDbUser");
		salesDbPwd = props.getProperty("salesDbPwd");

		log.info("connect to: "+ salesDbUrl);
		try{
			Class.forName(salesDbDriver);
			salesDBConnection = DriverManager.getConnection(salesDbUrl, salesDbUser, salesDbPwd);

		} catch (java.lang.ClassNotFoundException e)
		{
			log.severe("fail to connect to sales databases." );
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

// retrieve data from sales table and group data by customer and product.
	public static void Convert(String input)
    {
        int tmp=0;
        int tmpp=0;
		String Rtu="";
		String temp="";
        ArrayList list=new ArrayList();
       while(tmp<input.length()){
    	   if(input.substring(tmp, tmp+1).equals("|")){
    		   list.add(input.substring(tmpp, tmp));
    		   tmpp=tmp+1;
    	   }
    	   else if(tmp==input.length()-1){
    		   list.add(input.substring(tmpp, input.length()));
    	   }
    	   tmp++;    	   
       }
       for(int i=0;i<list.size();i++){
    	   temp=list.get(i).toString();
    	   if(temp.length()<7){
               for (int length=0;length<7-temp.toString().length();length++)
               {
                   temp=temp+" ";
               } 
               list.set(i, temp);
    	   }
    	   
       }
       for(int in=0;in<list.size();in++){
    	   Rtu=Rtu+list.get(in);
       }
       for(int i=0;i<list.size();i++){
    	   System.out.printf("%-7s",list.get(i).toString());
       }
       System.out.print("  ");

    }

	
// retrieve data from sales table and group data by customer and product.
	private void processSales(){
		ResultSet rset;
		log.entering("ProductSaleRecords", "processSalesRecords");
		String sql;
		SalesRecord aSale = null;
		String customerProduct=""; //customer and product key.
		
		sql = "select * from sales";

		log.info(sql);  

		ArrayList<String> mapkey = new ArrayList<String>();

		try {
			rset = salesDBConnection.createStatement().executeQuery(sql);
			while(rset.next()){
				aSale = new SalesRecord();
if(rset.getInt("year")==1997){
				aSale.setCust(rset.getString(1).trim()); // get cust trim it in case there are space there.
				aSale.setProd(rset.getString(2).trim()); //prod
				aSale.setDay(rset.getInt(3)); //date
				aSale.setMonth(rset.getInt(4)); //month
				aSale.setYear(rset.getInt(5)); //year
				aSale.setState(rset.getString(6).trim()); //state
				aSale.setQuant(rset.getInt(7)); //quant
customerProduct = aSale.getCust();
if(!mapkey.contains(customerProduct))
mapkey.add(customerProduct);


ReportRecord0 savedReportRecord0 = customerProductMap0.get(customerProduct);
ReportRecord1 savedReportRecord1 = customerProductMap1.get(customerProduct);


if(savedReportRecord0 == null) {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0 = new ReportRecord0(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
} else {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0.updateSales(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
}
if(savedReportRecord1 == null) {
if(aSale.getState().equalsIgnoreCase("NJ")) {
savedReportRecord1 = new ReportRecord1(aSale);
customerProductMap1.put(customerProduct, savedReportRecord1);}
} else {
if(aSale.getState().equalsIgnoreCase("NJ")) {
savedReportRecord1.updateSales(aSale);
customerProductMap1.put(customerProduct, savedReportRecord1);}
}



				
				}
				aSale = null;
			}
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			try {
				salesDBConnection.close();//release resource
			} catch (SQLException e1) {
				log.severe(" Failed to close database connection.");
				e1.printStackTrace();
			} 
			System.exit(1);
		}finally{
			try {
				salesDBConnection.close();
			} catch (SQLException e) {
				log.severe(" Failed to close database connection. Can't release resource.");
				e.printStackTrace();
			}//release resource
		}
log.info("customerProductMap: " + customerProductMap0.size());
log.info("customerProductMap: " + customerProductMap1.size());
//here all the data from database table sales are processed. need print them out

		//createCustomerProductReports();
		
		System.out.println("Reports genrated.");		
		log.exiting("processSalesRecords", "processSalesRecords");
//	}
//	generate customer product report
for (int i = 0; i < mapkey.size(); i++) {
if (customerProductMap0.containsKey(mapkey.get(i))||customerProductMap1.containsKey(mapkey.get(i))) {
Convert(mapkey.get(i));
if (customerProductMap0.containsKey(mapkey.get(i))) {
System.out.printf("%-10d",customerProductMap0.get(mapkey.get(i)).getSum());
} else if (!customerProductMap0.containsKey(mapkey.get(i))) {
System.out.printf("%-10s","0");
}
if (customerProductMap1.containsKey(mapkey.get(i))) {
System.out.printf("%-10d",customerProductMap1.get(mapkey.get(i)).getSum());
} else if (!customerProductMap1.containsKey(mapkey.get(i))) {
System.out.printf("%-10s","0");
}
System.out.println();
}
}
}
class SalesRecord{
		//getters and setters
		String getCust() {
			return cust;
		}
		void setCust(String cust) {
			this.cust = cust;
		}

		String getProd() {
			return prod;
		}
		void setProd(String prod) {
			this.prod = prod;
		}

		String getState() {
			return state;
		}
		void setState(String state) {
			this.state = state;
		}

		int getMonth() {
			return month;
		}
		void setMonth(int month) {
			this.month = month;
		}

		int getDay() {
			return day;
		}
		void setDay(int day) {
			this.day = day;
		}

		int getYear() {
			return year;
		}
		void setYear(int year) {
			this.year = year;
		}

		int getQuant() {
			return quant;
		}
		void setQuant(int quant) {
			this.quant = quant;
		}

		//attributes
		String cust="";
		String prod="";
		String state="";		
		int month=0;
		int day=0;
		int year=0;
		int quant=0;
	}


class TotalAmountCount{
		int getTotalSaleQuant() {
			return totalSaleQuant;
		}
		void setTotalSaleQuant(int totalSaleQuant) {
			this.totalSaleQuant = totalSaleQuant;
		}
		int getTotalSaleCount() {
			return totalSaleCount;
		}
		void setTotalSaleCount(int totalSaleCount) {
			this.totalSaleCount = totalSaleCount;
		}
		double getAvg() {//calculate average.
			avg = (double)totalSaleQuant/(double)totalSaleCount;
			return avg;
		}
		
		int totalSaleQuant=0;
		int totalSaleCount=0;
		double avg=0.0;
	}


class ReportRecord0 {
//attributes
		String cust="";
		String prod="";
		String state="";
		int day = 0;
		int month = 0;
		int year = 0;
		int quant = 0;		
int sumquant = 0;
		int totalCount=0;
		int totalSales=0;
public ReportRecord0(SalesRecord aSale) {
setCust(aSale.getCust());
setProd(aSale.getProd());
setState(aSale.getState());
setDay(aSale.getDay());
setMonth(aSale.getMonth());
setYear(aSale.getYear());
setQuant(aSale.getQuant());
sumquant = aSale.getQuant();
}

		String getCust() {
			return cust;
		}
		void setCust(String cust) {
			this.cust = cust;
		}

		int getDay() {
			return day;
		}
		void setDay(int day) {
			this.day = day;
		}
		int getQuant() {
			return quant;
		}
		void setQuant(int quant) {
			this.quant = quant;
		}
		int getMonth() {
			return month;
		}
		void setMonth(int month) {
			this.month = month;
		}
		int getYear() {
			return year;
		}
		void setYear(int year) {
			this.year = year;
		}
		String getProd() {
			return prod;
		}
		void setProd(String prod) {
			this.prod = prod;
		}
		String getState() {
			return state;
		}
		void setState(String state) {
			this.state = state;
		}				

		void updateSales(SalesRecord aSale) {
sumquant += aSale.getQuant();
}
int getSum() {
return sumquant;
}
}
class ReportRecord1 {
//attributes
		String cust="";
		String prod="";
		String state="";
		int day = 0;
		int month = 0;
		int year = 0;
		int quant = 0;		
int sumquant = 0;
		int totalCount=0;
		int totalSales=0;
public ReportRecord1(SalesRecord aSale) {
setCust(aSale.getCust());
setProd(aSale.getProd());
setState(aSale.getState());
setDay(aSale.getDay());
setMonth(aSale.getMonth());
setYear(aSale.getYear());
setQuant(aSale.getQuant());
sumquant = aSale.getQuant();
}

		String getCust() {
			return cust;
		}
		void setCust(String cust) {
			this.cust = cust;
		}

		int getDay() {
			return day;
		}
		void setDay(int day) {
			this.day = day;
		}
		int getQuant() {
			return quant;
		}
		void setQuant(int quant) {
			this.quant = quant;
		}
		int getMonth() {
			return month;
		}
		void setMonth(int month) {
			this.month = month;
		}
		int getYear() {
			return year;
		}
		void setYear(int year) {
			this.year = year;
		}
		String getProd() {
			return prod;
		}
		void setProd(String prod) {
			this.prod = prod;
		}
		String getState() {
			return state;
		}
		void setState(String state) {
			this.state = state;
		}				

		void updateSales(SalesRecord aSale) {
sumquant += aSale.getQuant();
}
int getSum() {
return sumquant;
}
}
}
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductSaleRecords {
	private String salesDbUrl,salesDbDriver, salesDbUser, salesDbPwd;
	Connection salesDBConnection = null;


Map<String, ReportRecord0> customerProductMap0;
Map<String, ReportRecord1> customerProductMap1;
Map<String, ReportRecord2> customerProductMap2;


FileHandler hand;
	Logger log;
	String logLevel="1000";
	Properties props;
	String logFileName = "";

	String custProdReportFile;  //save report to plain text file
    
	public static void main(String[] args) {
		ProductSaleRecords sale = new ProductSaleRecords();
        if (args.length < 1) {
            System.out.println("Usage: \r\n Correct parameters is:" + "<SalesPropertyFile>");
            System.exit(1);
        }
        System.out.println("Starting ProductSaleRecords ...");
		sale.init(args[0]);
		sale.processSales();
	}
	
	ProductSaleRecords() {//constructor.
customerProductMap0 = new HashMap<String, ReportRecord0>();
customerProductMap1 = new HashMap<String, ReportRecord1>();
customerProductMap2 = new HashMap<String, ReportRecord2>();
   
	}

	void init(String propertiesFile){      
        props = new Properties(); //load property file
        
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            System.out.println("Program cannot run without a properties file");
            e.printStackTrace();
           System.exit(1);
        }
        
        //file for logger outputs
        logFileName = props.getProperty("logFileName", "SALES.LOG");
        logLevel = props.getProperty("logLevel", "INFO"); //default to INFO 
        custProdReportFile = props.getProperty("custProdReportFile", "CUSTPROD.txt");
        
        
		try {
			hand = new FileHandler(logFileName);
			log = Logger.getLogger("ProductSaleRecords");
			log.addHandler(hand);
			log.setLevel(Level.parse(logLevel));		      
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectSalesDb();
	}
	void connectSalesDb(){
		salesDbUrl = props.getProperty("salesDbUrl");
		salesDbDriver = props.getProperty("salesDbDriver");
		salesDbUser = props.getProperty("salesDbUser");
		salesDbPwd = props.getProperty("salesDbPwd");

		log.info("connect to: "+ salesDbUrl);
		try{
			Class.forName(salesDbDriver);
			salesDBConnection = DriverManager.getConnection(salesDbUrl, salesDbUser, salesDbPwd);

		} catch (java.lang.ClassNotFoundException e)
		{
			log.severe("fail to connect to sales databases." );
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

// retrieve data from sales table and group data by customer and product.
	public static void Convert(String input)
    {
        int tmp=0;
        int tmpp=0;
		String Rtu="";
		String temp="";
        ArrayList list=new ArrayList();
       while(tmp<input.length()){
    	   if(input.substring(tmp, tmp+1).equals("|")){
    		   list.add(input.substring(tmpp, tmp));
    		   tmpp=tmp+1;
    	   }
    	   else if(tmp==input.length()-1){
    		   list.add(input.substring(tmpp, input.length()));
    	   }
    	   tmp++;    	   
       }
       for(int i=0;i<list.size();i++){
    	   temp=list.get(i).toString();
    	   if(temp.length()<7){
               for (int length=0;length<7-temp.toString().length();length++)
               {
                   temp=temp+" ";
               } 
               list.set(i, temp);
    	   }
    	   
       }
       for(int in=0;in<list.size();in++){
    	   Rtu=Rtu+list.get(in);
       }
       for(int i=0;i<list.size();i++){
    	   System.out.printf("%-7s",list.get(i).toString());
       }
       System.out.print("  ");

    }

	
// retrieve data from sales table and group data by customer and product.
	private void processSales(){
		ResultSet rset;
		log.entering("ProductSaleRecords", "processSalesRecords");
		String sql;
		SalesRecord aSale = null;
		String customerProduct=""; //customer and product key.
		
		sql = "select * from sales";

		log.info(sql);  

		ArrayList<String> mapkey = new ArrayList<String>();

		try {
			rset = salesDBConnection.createStatement().executeQuery(sql);
			while(rset.next()){
				aSale = new SalesRecord();
if(true){
				aSale.setCust(rset.getString(1).trim()); // get cust trim it in case there are space there.
				aSale.setProd(rset.getString(2).trim()); //prod
				aSale.setDay(rset.getInt(3)); //date
				aSale.setMonth(rset.getInt(4)); //month
				aSale.setYear(rset.getInt(5)); //year
				aSale.setState(rset.getString(6).trim()); //state
				aSale.setQuant(rset.getInt(7)); //quant
customerProduct = aSale.getCust();
if(!mapkey.contains(customerProduct))
mapkey.add(customerProduct);


ReportRecord0 savedReportRecord0 = customerProductMap0.get(customerProduct);
ReportRecord1 savedReportRecord1 = customerProductMap1.get(customerProduct);
ReportRecord2 savedReportRecord2 = customerProductMap2.get(customerProduct);


if(savedReportRecord0 == null) {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0 = new ReportRecord0(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
} else {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0.updateSales(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
}
if(savedReportRecord1 == null) {
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductSaleRecords {
	private String salesDbUrl,salesDbDriver, salesDbUser, salesDbPwd;
	Connection salesDBConnection = null;


Map<String, ReportRecord0> customerProductMap0;
Map<String, ReportRecord1> customerProductMap1;
Map<String, ReportRecord2> customerProductMap2;


FileHandler hand;
	Logger log;
	String logLevel="1000";
	Properties props;
	String logFileName = "";

	String custProdReportFile;  //save report to plain text file
    
	public static void main(String[] args) {
		ProductSaleRecords sale = new ProductSaleRecords();
        if (args.length < 1) {
            System.out.println("Usage: \r\n Correct parameters is:" + "<SalesPropertyFile>");
            System.exit(1);
        }
        System.out.println("Starting ProductSaleRecords ...");
		sale.init(args[0]);
		sale.processSales();
	}
	
	ProductSaleRecords() {//constructor.
customerProductMap0 = new HashMap<String, ReportRecord0>();
customerProductMap1 = new HashMap<String, ReportRecord1>();
customerProductMap2 = new HashMap<String, ReportRecord2>();
   
	}

	void init(String propertiesFile){      
        props = new Properties(); //load property file
        
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            System.out.println("Program cannot run without a properties file");
            e.printStackTrace();
           System.exit(1);
        }
        
        //file for logger outputs
        logFileName = props.getProperty("logFileName", "SALES.LOG");
        logLevel = props.getProperty("logLevel", "INFO"); //default to INFO 
        custProdReportFile = props.getProperty("custProdReportFile", "CUSTPROD.txt");
        
        
		try {
			hand = new FileHandler(logFileName);
			log = Logger.getLogger("ProductSaleRecords");
			log.addHandler(hand);
			log.setLevel(Level.parse(logLevel));		      
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectSalesDb();
	}
	void connectSalesDb(){
		salesDbUrl = props.getProperty("salesDbUrl");
		salesDbDriver = props.getProperty("salesDbDriver");
		salesDbUser = props.getProperty("salesDbUser");
		salesDbPwd = props.getProperty("salesDbPwd");

		log.info("connect to: "+ salesDbUrl);
		try{
			Class.forName(salesDbDriver);
			salesDBConnection = DriverManager.getConnection(salesDbUrl, salesDbUser, salesDbPwd);

		} catch (java.lang.ClassNotFoundException e)
		{
			log.severe("fail to connect to sales databases." );
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

// retrieve data from sales table and group data by customer and product.
	public static void Convert(String input)
    {
        int tmp=0;
        int tmpp=0;
		String Rtu="";
		String temp="";
        ArrayList list=new ArrayList();
       while(tmp<input.length()){
    	   if(input.substring(tmp, tmp+1).equals("|")){
    		   list.add(input.substring(tmpp, tmp));
    		   tmpp=tmp+1;
    	   }
    	   else if(tmp==input.length()-1){
    		   list.add(input.substring(tmpp, input.length()));
    	   }
    	   tmp++;    	   
       }
       for(int i=0;i<list.size();i++){
    	   temp=list.get(i).toString();
    	   if(temp.length()<7){
               for (int length=0;length<7-temp.toString().length();length++)
               {
                   temp=temp+" ";
               } 
               list.set(i, temp);
    	   }
    	   
       }
       for(int in=0;in<list.size();in++){
    	   Rtu=Rtu+list.get(in);
       }
       for(int i=0;i<list.size();i++){
    	   System.out.printf("%-7s",list.get(i).toString());
       }
       System.out.print("  ");

    }

	
// retrieve data from sales table and group data by customer and product.
	private void processSales(){
		ResultSet rset;
		log.entering("ProductSaleRecords", "processSalesRecords");
		String sql;
		SalesRecord aSale = null;
		String customerProduct=""; //customer and product key.
		
		sql = "select * from sales";

		log.info(sql);  

		ArrayList<String> mapkey = new ArrayList<String>();

		try {
			rset = salesDBConnection.createStatement().executeQuery(sql);
			while(rset.next()){
				aSale = new SalesRecord();
if(true){
				aSale.setCust(rset.getString(1).trim()); // get cust trim it in case there are space there.
				aSale.setProd(rset.getString(2).trim()); //prod
				aSale.setDay(rset.getInt(3)); //date
				aSale.setMonth(rset.getInt(4)); //month
				aSale.setYear(rset.getInt(5)); //year
				aSale.setState(rset.getString(6).trim()); //state
				aSale.setQuant(rset.getInt(7)); //quant
customerProduct = aSale.getCust();
if(!mapkey.contains(customerProduct))
mapkey.add(customerProduct);


ReportRecord0 savedReportRecord0 = customerProductMap0.get(customerProduct);
ReportRecord1 savedReportRecord1 = customerProductMap1.get(customerProduct);
ReportRecord2 savedReportRecord2 = customerProductMap2.get(customerProduct);


if(savedReportRecord0 == null) {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0 = new ReportRecord0(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
} else {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0.updateSales(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
}
if(savedReportRecord1 == null) {
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductSaleRecords {
	private String salesDbUrl,salesDbDriver, salesDbUser, salesDbPwd;
	Connection salesDBConnection = null;


Map<String, ReportRecord0> customerProductMap0;
Map<String, ReportRecord1> customerProductMap1;
Map<String, ReportRecord2> customerProductMap2;


FileHandler hand;
	Logger log;
	String logLevel="1000";
	Properties props;
	String logFileName = "";

	String custProdReportFile;  //save report to plain text file
    
	public static void main(String[] args) {
		ProductSaleRecords sale = new ProductSaleRecords();
        if (args.length < 1) {
            System.out.println("Usage: \r\n Correct parameters is:" + "<SalesPropertyFile>");
            System.exit(1);
        }
        System.out.println("Starting ProductSaleRecords ...");
		sale.init(args[0]);
		sale.processSales();
	}
	
	ProductSaleRecords() {//constructor.
customerProductMap0 = new HashMap<String, ReportRecord0>();
customerProductMap1 = new HashMap<String, ReportRecord1>();
customerProductMap2 = new HashMap<String, ReportRecord2>();
   
	}

	void init(String propertiesFile){      
        props = new Properties(); //load property file
        
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            System.out.println("Program cannot run without a properties file");
            e.printStackTrace();
           System.exit(1);
        }
        
        //file for logger outputs
        logFileName = props.getProperty("logFileName", "SALES.LOG");
        logLevel = props.getProperty("logLevel", "INFO"); //default to INFO 
        custProdReportFile = props.getProperty("custProdReportFile", "CUSTPROD.txt");
        
        
		try {
			hand = new FileHandler(logFileName);
			log = Logger.getLogger("ProductSaleRecords");
			log.addHandler(hand);
			log.setLevel(Level.parse(logLevel));		      
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectSalesDb();
	}
	void connectSalesDb(){
		salesDbUrl = props.getProperty("salesDbUrl");
		salesDbDriver = props.getProperty("salesDbDriver");
		salesDbUser = props.getProperty("salesDbUser");
		salesDbPwd = props.getProperty("salesDbPwd");

		log.info("connect to: "+ salesDbUrl);
		try{
			Class.forName(salesDbDriver);
			salesDBConnection = DriverManager.getConnection(salesDbUrl, salesDbUser, salesDbPwd);

		} catch (java.lang.ClassNotFoundException e)
		{
			log.severe("fail to connect to sales databases." );
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

// retrieve data from sales table and group data by customer and product.
	public static void Convert(String input)
    {
        int tmp=0;
        int tmpp=0;
		String Rtu="";
		String temp="";
        ArrayList list=new ArrayList();
       while(tmp<input.length()){
    	   if(input.substring(tmp, tmp+1).equals("|")){
    		   list.add(input.substring(tmpp, tmp));
    		   tmpp=tmp+1;
    	   }
    	   else if(tmp==input.length()-1){
    		   list.add(input.substring(tmpp, input.length()));
    	   }
    	   tmp++;    	   
       }
       for(int i=0;i<list.size();i++){
    	   temp=list.get(i).toString();
    	   if(temp.length()<7){
               for (int length=0;length<7-temp.toString().length();length++)
               {
                   temp=temp+" ";
               } 
               list.set(i, temp);
    	   }
    	   
       }
       for(int in=0;in<list.size();in++){
    	   Rtu=Rtu+list.get(in);
       }
       for(int i=0;i<list.size();i++){
    	   System.out.printf("%-7s",list.get(i).toString());
       }
       System.out.print("  ");

    }

	
// retrieve data from sales table and group data by customer and product.
	private void processSales(){
		ResultSet rset;
		log.entering("ProductSaleRecords", "processSalesRecords");
		String sql;
		SalesRecord aSale = null;
		String customerProduct=""; //customer and product key.
		
		sql = "select * from sales";

		log.info(sql);  

		ArrayList<String> mapkey = new ArrayList<String>();

		try {
			rset = salesDBConnection.createStatement().executeQuery(sql);
			while(rset.next()){
				aSale = new SalesRecord();
if(true){
				aSale.setCust(rset.getString(1).trim()); // get cust trim it in case there are space there.
				aSale.setProd(rset.getString(2).trim()); //prod
				aSale.setDay(rset.getInt(3)); //date
				aSale.setMonth(rset.getInt(4)); //month
				aSale.setYear(rset.getInt(5)); //year
				aSale.setState(rset.getString(6).trim()); //state
				aSale.setQuant(rset.getInt(7)); //quant
customerProduct = aSale.getCust();
if(!mapkey.contains(customerProduct))
mapkey.add(customerProduct);


ReportRecord0 savedReportRecord0 = customerProductMap0.get(customerProduct);
ReportRecord1 savedReportRecord1 = customerProductMap1.get(customerProduct);
ReportRecord2 savedReportRecord2 = customerProductMap2.get(customerProduct);


if(savedReportRecord0 == null) {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0 = new ReportRecord0(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
} else {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0.updateSales(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
}
if(savedReportRecord1 == null) {
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductSaleRecords {
	private String salesDbUrl,salesDbDriver, salesDbUser, salesDbPwd;
	Connection salesDBConnection = null;


Map<String, ReportRecord0> customerProductMap0;
Map<String, ReportRecord1> customerProductMap1;
Map<String, ReportRecord2> customerProductMap2;


FileHandler hand;
	Logger log;
	String logLevel="1000";
	Properties props;
	String logFileName = "";

	String custProdReportFile;  //save report to plain text file
    
	public static void main(String[] args) {
		ProductSaleRecords sale = new ProductSaleRecords();
        if (args.length < 1) {
            System.out.println("Usage: \r\n Correct parameters is:" + "<SalesPropertyFile>");
            System.exit(1);
        }
        System.out.println("Starting ProductSaleRecords ...");
		sale.init(args[0]);
		sale.processSales();
	}
	
	ProductSaleRecords() {//constructor.
customerProductMap0 = new HashMap<String, ReportRecord0>();
customerProductMap1 = new HashMap<String, ReportRecord1>();
customerProductMap2 = new HashMap<String, ReportRecord2>();
   
	}

	void init(String propertiesFile){      
        props = new Properties(); //load property file
        
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            System.out.println("Program cannot run without a properties file");
            e.printStackTrace();
           System.exit(1);
        }
        
        //file for logger outputs
        logFileName = props.getProperty("logFileName", "SALES.LOG");
        logLevel = props.getProperty("logLevel", "INFO"); //default to INFO 
        custProdReportFile = props.getProperty("custProdReportFile", "CUSTPROD.txt");
        
        
		try {
			hand = new FileHandler(logFileName);
			log = Logger.getLogger("ProductSaleRecords");
			log.addHandler(hand);
			log.setLevel(Level.parse(logLevel));		      
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectSalesDb();
	}
	void connectSalesDb(){
		salesDbUrl = props.getProperty("salesDbUrl");
		salesDbDriver = props.getProperty("salesDbDriver");
		salesDbUser = props.getProperty("salesDbUser");
		salesDbPwd = props.getProperty("salesDbPwd");

		log.info("connect to: "+ salesDbUrl);
		try{
			Class.forName(salesDbDriver);
			salesDBConnection = DriverManager.getConnection(salesDbUrl, salesDbUser, salesDbPwd);

		} catch (java.lang.ClassNotFoundException e)
		{
			log.severe("fail to connect to sales databases." );
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

// retrieve data from sales table and group data by customer and product.
	public static void Convert(String input)
    {
        int tmp=0;
        int tmpp=0;
		String Rtu="";
		String temp="";
        ArrayList list=new ArrayList();
       while(tmp<input.length()){
    	   if(input.substring(tmp, tmp+1).equals("|")){
    		   list.add(input.substring(tmpp, tmp));
    		   tmpp=tmp+1;
    	   }
    	   else if(tmp==input.length()-1){
    		   list.add(input.substring(tmpp, input.length()));
    	   }
    	   tmp++;    	   
       }
       for(int i=0;i<list.size();i++){
    	   temp=list.get(i).toString();
    	   if(temp.length()<7){
               for (int length=0;length<7-temp.toString().length();length++)
               {
                   temp=temp+" ";
               } 
               list.set(i, temp);
    	   }
    	   
       }
       for(int in=0;in<list.size();in++){
    	   Rtu=Rtu+list.get(in);
       }
       for(int i=0;i<list.size();i++){
    	   System.out.printf("%-7s",list.get(i).toString());
       }
       System.out.print("  ");

    }

	
// retrieve data from sales table and group data by customer and product.
	private void processSales(){
		ResultSet rset;
		log.entering("ProductSaleRecords", "processSalesRecords");
		String sql;
		SalesRecord aSale = null;
		String customerProduct=""; //customer and product key.
		
		sql = "select * from sales";

		log.info(sql);  

		ArrayList<String> mapkey = new ArrayList<String>();

		try {
			rset = salesDBConnection.createStatement().executeQuery(sql);
			while(rset.next()){
				aSale = new SalesRecord();
if(true){
				aSale.setCust(rset.getString(1).trim()); // get cust trim it in case there are space there.
				aSale.setProd(rset.getString(2).trim()); //prod
				aSale.setDay(rset.getInt(3)); //date
				aSale.setMonth(rset.getInt(4)); //month
				aSale.setYear(rset.getInt(5)); //year
				aSale.setState(rset.getString(6).trim()); //state
				aSale.setQuant(rset.getInt(7)); //quant
customerProduct = aSale.getCust();
if(!mapkey.contains(customerProduct))
mapkey.add(customerProduct);


ReportRecord0 savedReportRecord0 = customerProductMap0.get(customerProduct);
ReportRecord1 savedReportRecord1 = customerProductMap1.get(customerProduct);
ReportRecord2 savedReportRecord2 = customerProductMap2.get(customerProduct);


if(savedReportRecord0 == null) {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0 = new ReportRecord0(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
} else {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0.updateSales(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
}
if(savedReportRecord1 == null) {
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductSaleRecords {
	private String salesDbUrl,salesDbDriver, salesDbUser, salesDbPwd;
	Connection salesDBConnection = null;


Map<String, ReportRecord0> customerProductMap0;
Map<String, ReportRecord1> customerProductMap1;
Map<String, ReportRecord2> customerProductMap2;


FileHandler hand;
	Logger log;
	String logLevel="1000";
	Properties props;
	String logFileName = "";

	String custProdReportFile;  //save report to plain text file
    
	public static void main(String[] args) {
		ProductSaleRecords sale = new ProductSaleRecords();
        if (args.length < 1) {
            System.out.println("Usage: \r\n Correct parameters is:" + "<SalesPropertyFile>");
            System.exit(1);
        }
        System.out.println("Starting ProductSaleRecords ...");
		sale.init(args[0]);
		sale.processSales();
	}
	
	ProductSaleRecords() {//constructor.
customerProductMap0 = new HashMap<String, ReportRecord0>();
customerProductMap1 = new HashMap<String, ReportRecord1>();
customerProductMap2 = new HashMap<String, ReportRecord2>();
   
	}

	void init(String propertiesFile){      
        props = new Properties(); //load property file
        
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            System.out.println("Program cannot run without a properties file");
            e.printStackTrace();
           System.exit(1);
        }
        
        //file for logger outputs
        logFileName = props.getProperty("logFileName", "SALES.LOG");
        logLevel = props.getProperty("logLevel", "INFO"); //default to INFO 
        custProdReportFile = props.getProperty("custProdReportFile", "CUSTPROD.txt");
        
        
		try {
			hand = new FileHandler(logFileName);
			log = Logger.getLogger("ProductSaleRecords");
			log.addHandler(hand);
			log.setLevel(Level.parse(logLevel));		      
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectSalesDb();
	}
	void connectSalesDb(){
		salesDbUrl = props.getProperty("salesDbUrl");
		salesDbDriver = props.getProperty("salesDbDriver");
		salesDbUser = props.getProperty("salesDbUser");
		salesDbPwd = props.getProperty("salesDbPwd");

		log.info("connect to: "+ salesDbUrl);
		try{
			Class.forName(salesDbDriver);
			salesDBConnection = DriverManager.getConnection(salesDbUrl, salesDbUser, salesDbPwd);

		} catch (java.lang.ClassNotFoundException e)
		{
			log.severe("fail to connect to sales databases." );
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

// retrieve data from sales table and group data by customer and product.
	public static void Convert(String input)
    {
        int tmp=0;
        int tmpp=0;
		String Rtu="";
		String temp="";
        ArrayList list=new ArrayList();
       while(tmp<input.length()){
    	   if(input.substring(tmp, tmp+1).equals("|")){
    		   list.add(input.substring(tmpp, tmp));
    		   tmpp=tmp+1;
    	   }
    	   else if(tmp==input.length()-1){
    		   list.add(input.substring(tmpp, input.length()));
    	   }
    	   tmp++;    	   
       }
       for(int i=0;i<list.size();i++){
    	   temp=list.get(i).toString();
    	   if(temp.length()<7){
               for (int length=0;length<7-temp.toString().length();length++)
               {
                   temp=temp+" ";
               } 
               list.set(i, temp);
    	   }
    	   
       }
       for(int in=0;in<list.size();in++){
    	   Rtu=Rtu+list.get(in);
       }
       for(int i=0;i<list.size();i++){
    	   System.out.printf("%-7s",list.get(i).toString());
       }
       System.out.print("  ");

    }

	
// retrieve data from sales table and group data by customer and product.
	private void processSales(){
		ResultSet rset;
		log.entering("ProductSaleRecords", "processSalesRecords");
		String sql;
		SalesRecord aSale = null;
		String customerProduct=""; //customer and product key.
		
		sql = "select * from sales";

		log.info(sql);  

		ArrayList<String> mapkey = new ArrayList<String>();

		try {
			rset = salesDBConnection.createStatement().executeQuery(sql);
			while(rset.next()){
				aSale = new SalesRecord();
if(true){
				aSale.setCust(rset.getString(1).trim()); // get cust trim it in case there are space there.
				aSale.setProd(rset.getString(2).trim()); //prod
				aSale.setDay(rset.getInt(3)); //date
				aSale.setMonth(rset.getInt(4)); //month
				aSale.setYear(rset.getInt(5)); //year
				aSale.setState(rset.getString(6).trim()); //state
				aSale.setQuant(rset.getInt(7)); //quant
customerProduct = aSale.getCust();
if(!mapkey.contains(customerProduct))
mapkey.add(customerProduct);


ReportRecord0 savedReportRecord0 = customerProductMap0.get(customerProduct);
ReportRecord1 savedReportRecord1 = customerProductMap1.get(customerProduct);
ReportRecord2 savedReportRecord2 = customerProductMap2.get(customerProduct);


if(savedReportRecord0 == null) {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0 = new ReportRecord0(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
} else {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0.updateSales(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
}
if(savedReportRecord1 == null) {
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductSaleRecords {
	private String salesDbUrl,salesDbDriver, salesDbUser, salesDbPwd;
	Connection salesDBConnection = null;


Map<String, ReportRecord0> customerProductMap0;
Map<String, ReportRecord1> customerProductMap1;
Map<String, ReportRecord2> customerProductMap2;


FileHandler hand;
	Logger log;
	String logLevel="1000";
	Properties props;
	String logFileName = "";

	String custProdReportFile;  //save report to plain text file
    
	public static void main(String[] args) {
		ProductSaleRecords sale = new ProductSaleRecords();
        if (args.length < 1) {
            System.out.println("Usage: \r\n Correct parameters is:" + "<SalesPropertyFile>");
            System.exit(1);
        }
        System.out.println("Starting ProductSaleRecords ...");
		sale.init(args[0]);
		sale.processSales();
	}
	
	ProductSaleRecords() {//constructor.
customerProductMap0 = new HashMap<String, ReportRecord0>();
customerProductMap1 = new HashMap<String, ReportRecord1>();
customerProductMap2 = new HashMap<String, ReportRecord2>();
   
	}

	void init(String propertiesFile){      
        props = new Properties(); //load property file
        
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            System.out.println("Program cannot run without a properties file");
            e.printStackTrace();
           System.exit(1);
        }
        
        //file for logger outputs
        logFileName = props.getProperty("logFileName", "SALES.LOG");
        logLevel = props.getProperty("logLevel", "INFO"); //default to INFO 
        custProdReportFile = props.getProperty("custProdReportFile", "CUSTPROD.txt");
        
        
		try {
			hand = new FileHandler(logFileName);
			log = Logger.getLogger("ProductSaleRecords");
			log.addHandler(hand);
			log.setLevel(Level.parse(logLevel));		      
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectSalesDb();
	}
	void connectSalesDb(){
		salesDbUrl = props.getProperty("salesDbUrl");
		salesDbDriver = props.getProperty("salesDbDriver");
		salesDbUser = props.getProperty("salesDbUser");
		salesDbPwd = props.getProperty("salesDbPwd");

		log.info("connect to: "+ salesDbUrl);
		try{
			Class.forName(salesDbDriver);
			salesDBConnection = DriverManager.getConnection(salesDbUrl, salesDbUser, salesDbPwd);

		} catch (java.lang.ClassNotFoundException e)
		{
			log.severe("fail to connect to sales databases." );
		} catch (SQLException e) {
			log.severe("SQL STATE: " + e.getSQLState());
			log.severe("ERROR CODE: " + e.getErrorCode());
			log.severe("MESSAGE: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

// retrieve data from sales table and group data by customer and product.
	public static void Convert(String input)
    {
        int tmp=0;
        int tmpp=0;
		String Rtu="";
		String temp="";
        ArrayList list=new ArrayList();
       while(tmp<input.length()){
    	   if(input.substring(tmp, tmp+1).equals("|")){
    		   list.add(input.substring(tmpp, tmp));
    		   tmpp=tmp+1;
    	   }
    	   else if(tmp==input.length()-1){
    		   list.add(input.substring(tmpp, input.length()));
    	   }
    	   tmp++;    	   
       }
       for(int i=0;i<list.size();i++){
    	   temp=list.get(i).toString();
    	   if(temp.length()<7){
               for (int length=0;length<7-temp.toString().length();length++)
               {
                   temp=temp+" ";
               } 
               list.set(i, temp);
    	   }
    	   
       }
       for(int in=0;in<list.size();in++){
    	   Rtu=Rtu+list.get(in);
       }
       for(int i=0;i<list.size();i++){
    	   System.out.printf("%-7s",list.get(i).toString());
       }
       System.out.print("  ");

    }

	
// retrieve data from sales table and group data by customer and product.
	private void processSales(){
		ResultSet rset;
		log.entering("ProductSaleRecords", "processSalesRecords");
		String sql;
		SalesRecord aSale = null;
		String customerProduct=""; //customer and product key.
		
		sql = "select * from sales";

		log.info(sql);  

		ArrayList<String> mapkey = new ArrayList<String>();

		try {
			rset = salesDBConnection.createStatement().executeQuery(sql);
			while(rset.next()){
				aSale = new SalesRecord();
if(true){
				aSale.setCust(rset.getString(1).trim()); // get cust trim it in case there are space there.
				aSale.setProd(rset.getString(2).trim()); //prod
				aSale.setDay(rset.getInt(3)); //date
				aSale.setMonth(rset.getInt(4)); //month
				aSale.setYear(rset.getInt(5)); //year
				aSale.setState(rset.getString(6).trim()); //state
				aSale.setQuant(rset.getInt(7)); //quant
customerProduct = aSale.getCust();
if(!mapkey.contains(customerProduct))
mapkey.add(customerProduct);


ReportRecord0 savedReportRecord0 = customerProductMap0.get(customerProduct);
ReportRecord1 savedReportRecord1 = customerProductMap1.get(customerProduct);
ReportRecord2 savedReportRecord2 = customerProductMap2.get(customerProduct);


if(savedReportRecord0 == null) {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0 = new ReportRecord0(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
} else {
if(aSale.getState().equalsIgnoreCase("NY")) {
savedReportRecord0.updateSales(aSale);
customerProductMap0.put(customerProduct, savedReportRecord0);}
}
if(savedReportRecord1 == null) {
