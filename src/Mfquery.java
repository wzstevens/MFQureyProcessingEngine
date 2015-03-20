
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 *
 * main class
 *
 */
public class Mfquery {

    // attributes
    private int groupingVarNumber = 0; // grouping variables

    // ArrayList
    private ArrayList<String> selectedOutput = new ArrayList(); // aggregation
    // functions in
    // select
    private ArrayList<String> suchthat = new ArrayList(); // such that

    private String where = ""; // where clause

    private int selectedAggNum = 0; // quantity of aggregation functions in
    // select
    private String groupingAttributes = ""; // grouping attributes
    private static Map<String, String> attrFuncMap;
    private static Map<String, String> whereRsetMap;

    private String mainClassMapKey = ""; // generated program map key
    private ArrayList<String> having = new ArrayList();

	// private String inputFileorString;//global variable
    // txt file paths
    final private String connFileSourcePath = "New Folder/waitinglist1.txt";
    final private String intiFileSourcePath = "New Folder/waitinglist2(1).txt";
    final private String intiFileSourcePath2 = "New Folder/waitinglist2(2).txt";
    final private String intiFileSourcePath3 = "New Folder/waitinglist2(2)(2).txt";
    final private String mapOpSourcePath = "New Folder/waitinglist3(1).txt";
    final private String mapOpSourcePath2 = "New Folder/waitinglist3(2).txt";
    final private String mapOpSourcePath3 = "New Folder/waitinglist3(2)(2).txt";
    final private String salesRecordClassPath = "New Folder/waitinglist4.txt";
    final private String repostRecordAttr = "New Folder/reportRecordAttr.txt";
    final private String repostRecordAttr2 = "New Folder/reportRecordAttr(2).txt";
    final private String repostRecordmethods = "New Folder/repostRecordmethods.txt";
    final private String repostRecordmethods2 = "New Folder/waitinglist5(1).txt";
	// target java file path: under yufeibanben/New
    // Folder/ProductSaleRecords.java
    final private String tarFilePath = "New Folder/ProductSaleRecords.java";

	// methods
    // main
    public void main() throws Exception {

        // construct maps
        attrFuncMap = new HashMap<String, String>();
        attrFuncMap.put("cust", "aSale.getCust()");
        attrFuncMap.put("prod", "aSale.getProd()");
        attrFuncMap.put("state", "aSale.getState()");
        attrFuncMap.put("month", "aSale.getMonth()");
        attrFuncMap.put("year", "aSale.getYear()");
        attrFuncMap.put("day", "aSale.getDay()");
        attrFuncMap.put("quant", "aSale.getQuant()");

        whereRsetMap = new HashMap<String, String>();
        whereRsetMap.put("cust", "rset.getString(\"cust\")");
        whereRsetMap.put("prod", "rset.getString(\"prod\")");
        whereRsetMap.put("state", "rset.getString(\"state\")");
        whereRsetMap.put("day", "rset.getInt(\"day\")");
        whereRsetMap.put("month", "rset.getInt(\"month\")");
        whereRsetMap.put("year", "rset.getInt(\"year\")");
        whereRsetMap.put("quant", "rset.getInt(\"quant\")");

        GenerateDataType gdt = new GenerateDataType();

        System.out.println("Starting Mfquerys ...");
        // load generate connection codes
        generateConnectionCode();
        // load main method
        processing();
    }

    // generate connection codes
    public void generateConnectionCode() throws IOException {
        outputFunction(connFileSourcePath);
        outputFunction("\n");
    }

    // output method
    public void outputFunction(String inputFileorString) throws IOException {
        File sourceFile = new File(inputFileorString);
        FileWriter fw = new FileWriter(tarFilePath, true);
        BufferedWriter output = new BufferedWriter(fw);
        if (sourceFile.isFile()) {
            Scanner input = new Scanner(sourceFile);
            while (input.hasNext()) {
                String s1 = input.nextLine();
                output.write(s1);
                output.newLine();
            }
            input.close();
        } else {
            output.write(inputFileorString);
            output.newLine();

        }
        output.close();
    }

    // get map key
    public void generateMapKey() {

        getColumnName gcn = new getColumnName(groupingAttributes);
        gcn.transferGroupingAttributes();
        mainClassMapKey = gcn.generateMapKey();

    }

    // main logical method
    void processing() throws Exception {

        AggregationFunctions af = new AggregationFunctions();
        af.outputSuchThat();

        String outPutMapAttr = "";
        // generate map attribute in generated program
        for (int i = 0; i < groupingVarNumber; i++) {
            outPutMapAttr = "Map<String, ReportRecord" + i + ">" + " "
                    + "customerProductMap" + i + ";";
            outputFunction(outPutMapAttr);
        }

        outputFunction("\n");
        outputFunction(intiFileSourcePath);
        for (int i = 0; i < groupingVarNumber; i++) {
            outputFunction("customerProductMap" + i
                    + " = new HashMap<String, ReportRecord" + i + ">();");
        }

        // this is where**************************************************
        outputFunction(intiFileSourcePath2);
        outputFunction(IFline(where));
        outputFunction(intiFileSourcePath3);
        // ***********************************************************
        generateMapKey();
        outputFunction(mainClassMapKey);

        // add element in generated arraylist
        outputFunction("if(!mapkey.contains(customerProduct))");
        outputFunction("mapkey.add(customerProduct);");

        outputFunction("\n");
        for (int i = 0; i < groupingVarNumber; i++) {
            // output constructor
            outputFunction("ReportRecord" + i + " " + "savedReportRecord" + i
                    + " = " + "customerProductMap" + i
                    + ".get(customerProduct);");
        }

        outputFunction("\n");

        for (int i = 0; i < groupingVarNumber; i++) {

            // output main logic
            outputFunction("if(savedReportRecord" + i + " == null) {");
            outputFunction(af.thisSuchThatJava.get(i) + " {");
            outputFunction("savedReportRecord" + i + " = new ReportRecord" + i
                    + "(aSale);");
            outputFunction("customerProductMap" + i
                    + ".put(customerProduct, savedReportRecord" + i + ");}");
            outputFunction("} else {");
            outputFunction(af.thisSuchThatJava.get(i) + " {");
            outputFunction("savedReportRecord" + i + ".updateSales(aSale);");
            outputFunction("customerProductMap" + i
                    + ".put(customerProduct, savedReportRecord" + i + ");}");
            outputFunction("}");

        }

        outputFunction("\n");
        outputFunction(mapOpSourcePath);

        for (int i = 0; i < groupingVarNumber; i++) {
            outputFunction("log.info(\"customerProductMap: \" + customerProductMap"
                    + i + ".size());");
        }
        outputFunction(mapOpSourcePath2);
		// ************************************************

        // ************************************************
        outputFunction("for (int i = 0; i < mapkey.size(); i++) {");
        String temp2 = "if (";
        for (int i = 0; i < groupingVarNumber; i++) {
            temp2 += "customerProductMap" + i + ".containsKey(mapkey.get(i))||";
        }

        temp2 = temp2.substring(0, temp2.length() - 3);
        temp2 += ")) {";
        outputFunction(temp2);
        outputFunction("Convert(mapkey.get(i));");

        String temp3 = "";

        for (int i = 0; i < groupingVarNumber; i++) {
            for (int j = 0; j < selectedOutput.size(); j++) {
                if (Integer.parseInt(selectedOutput.get(j).substring(0, 1)) == (i + 1)) {
                    outputFunction("if (customerProductMap" + i
                            + ".containsKey(mapkey.get(i))) {");
					// output format method
                    // ************************************************
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("max")) {
                        outputFunction("System.out.printf(\"%-10d\",customerProductMap"
                                + i + ".get(mapkey.get(i)).getMax());");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("min")) {
                        outputFunction("System.out.printf(\"%-10d\",customerProductMap"
                                + i + ".get(mapkey.get(i)).getMin());");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("sum")) {
                        outputFunction("System.out.printf(\"%-10d\",customerProductMap"
                                + i + ".get(mapkey.get(i)).getSum());");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("avg")) {
                        outputFunction("System.out.printf(\"%-10d\",customerProductMap"
                                + i + ".get(mapkey.get(i)).getAvg());");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("cnt")) {
                        outputFunction("System.out.printf(\"%-10d\",customerProductMap"
                                + i + ".get(mapkey.get(i)).getCnt());");
                    }

                    // ************************************************
                    outputFunction("} else if (!customerProductMap" + i
                            + ".containsKey(mapkey.get(i))) {");
                    outputFunction("System.out.printf(\"%-10s\",\"0\");");
                    outputFunction("}");
                }
            }
        }

        outputFunction("System.out.println();");

        outputFunction("}");
        // ************************************************
        outputFunction("}");
        outputFunction("}");

        outputFunction(salesRecordClassPath);
        outputFunction("\n");

        // generate ReportRecord class
        for (int i = 0; i < groupingVarNumber; i++) {

            outputFunction("class ReportRecord" + i + " {");
            outputFunction(repostRecordAttr);
            // output ReportRecord class attributes
            for (int j = 0; j < selectedOutput.size(); j++) {
                if (Integer.parseInt(selectedOutput.get(j).substring(0, 1)) == (i + 1)) {
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("max")) {
                        outputFunction("int max"
                                + selectedOutput.get(j).substring(6) + " = 0;");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("min")) {
                        outputFunction("int min"
                                + selectedOutput.get(j).substring(6) + " = 0;");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("sum")) {
                        outputFunction("int sum"
                                + selectedOutput.get(j).substring(6) + " = 0;");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("avg")) {
                        outputFunction("int totalsales"
                                + selectedOutput.get(j).substring(6) + " = 0;");
                        outputFunction("int totalcount = 0;");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("cnt")) {
                        outputFunction("int cnt"
                                + selectedOutput.get(j).substring(6) + " = 0;");
                    }
                }
            }

            outputFunction(repostRecordAttr2);
            // output ReportRecord class constructor
            outputFunction("public ReportRecord" + i + "(SalesRecord aSale) {");
            outputFunction("setCust(aSale.getCust());");
            outputFunction("setProd(aSale.getProd());");
            outputFunction("setState(aSale.getState());");
            outputFunction("setDay(aSale.getDay());");
            outputFunction("setMonth(aSale.getMonth());");
            outputFunction("setYear(aSale.getYear());");
            outputFunction("setQuant(aSale.getQuant());");
            // **************************************************************
            for (int j = 0; j < selectedOutput.size(); j++) {

                if (Integer.parseInt(selectedOutput.get(j).substring(0, 1)) == (i + 1)) {
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("max")) {
                        outputFunction("max"
                                + selectedOutput.get(j).substring(6)
                                + " = "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("avg")) {
                        outputFunction("totalsales"
                                + selectedOutput.get(j).substring(6)
                                + " += "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                        outputFunction("totalcount += 1;");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("min")) {
                        outputFunction("min"
                                + selectedOutput.get(j).substring(6)
                                + " = "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("sum")) {
                        outputFunction("sum"
                                + selectedOutput.get(j).substring(6)
                                + " = "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("cnt")) {
                        outputFunction("cnt"
                                + selectedOutput.get(j).substring(6) + " = 1;");
                    }
                }
            }
            outputFunction(repostRecordmethods2);
            // **************************************************************
            af.getFunctionsOutput();

            // output update method in ReportRecord class
            for (int j = 0; j < selectedOutput.size(); j++) {

                if (Integer.parseInt(selectedOutput.get(j).substring(0, 1)) == (i + 1)) {
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("max")) {
                        outputFunction("if("
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + " > max"
                                + selectedOutput.get(j).substring(6) + ")");
                        outputFunction("max"
                                + selectedOutput.get(j).substring(6)
                                + " = "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("min")) {
                        outputFunction("if("
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + " < min"
                                + selectedOutput.get(j).substring(6) + ")");
                        outputFunction("min"
                                + selectedOutput.get(j).substring(6)
                                + " = "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("avg")) {
                        outputFunction("totalsales"
                                + selectedOutput.get(j).substring(6)
                                + " += "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                        outputFunction("totalcount += 1;");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("sum")) {
                        outputFunction("sum"
                                + selectedOutput.get(j).substring(6)
                                + " += "
                                + attrFuncMap.get(selectedOutput.get(j)
                                        .substring(6)) + ";");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("cnt")) {
                        outputFunction("cnt"
                                + selectedOutput.get(j).substring(6) + " += 1;");
                    }

                }
            }
            outputFunction("}");
            // output get set method in Reportrecord class
            for (int j = 0; j < selectedOutput.size(); j++) {
                if (Integer.parseInt(selectedOutput.get(j).substring(0, 1)) == (i + 1)) {
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("max")) {
                        outputFunction("int getMax() {");
                        outputFunction("return " + "max"
                                + selectedOutput.get(j).substring(6) + ";");
                        outputFunction("}");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("min")) {
                        outputFunction("int getMin() {");
                        outputFunction("return " + "min"
                                + selectedOutput.get(j).substring(6) + ";");
                        outputFunction("}");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("sum")) {
                        outputFunction("int getSum() {");
                        outputFunction("return " + "sum"
                                + selectedOutput.get(j).substring(6) + ";");
                        outputFunction("}");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("avg")) {
                        outputFunction("int getAvg() {");
                        outputFunction("return " + "totalsales"
                                + selectedOutput.get(j).substring(6)
                                + "/totalcount" + ";");
                        outputFunction("}");
                    }
                    if (selectedOutput.get(j).substring(2, 5)
                            .equalsIgnoreCase("cnt")) {
                        outputFunction("int getCnt() {");
                        outputFunction("return " + "cnt"
                                + selectedOutput.get(j).substring(6) + ";");
                        outputFunction("}");
                    }
                }
            }
            outputFunction("}");
        }
        outputFunction("}");
    }

    // tranfer where clause
    public static String IFline(String where) {
        Map<String, String> whereRsetMap;
        String finalIF = "";
        String finalIFline = "";

        ArrayList<String> array = new ArrayList<String>();
        whereRsetMap = new HashMap<String, String>();
        whereRsetMap.put("cust", "rset.getString(\"cust\")");
        whereRsetMap.put("prod", "rset.getString(\"prod\")");
        whereRsetMap.put("state", "rset.getString(\"state\")");
        whereRsetMap.put("day", "rset.getInt(\"day\")");
        whereRsetMap.put("month", "rset.getInt(\"month\")");
        whereRsetMap.put("year", "rset.getInt(\"year\")");
        whereRsetMap.put("quant", "rset.getInt(\"quant\")");

        Scanner scan = new Scanner(where);
        scan.useDelimiter(" ");
        int k = 0;
        while (scan.hasNext()) {
            array.add(scan.next().toString());
            k++;
        }
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < whereRsetMap.size(); j++) {
                if (whereRsetMap.containsKey(array.get(i))) {
                    array.set(i, whereRsetMap.get(array.get(i)));
                }
            }
            if (array.get(i).equals("=")) {
                if (array.get(i + 1).charAt(0) >= 48
                        && array.get(i + 1).charAt(0) <= 57) {
                    array.set(i, "==");
                } else {
                    array.set(i, ".equals");
                }
            } else if (array.get(i).equals("<>")) {
                if (array.get(i + 1).charAt(0) >= 48
                        && array.get(i + 1).charAt(0) <= 57) {
                    array.set(i, "!=");
                } else if (array.get(i + 1).substring(0, 1).equals("'")) {
                    array.set(i, ".equals");
                    array.set(i - 1, "!" + array.get(i - 1));
                }
            }
            if (array.get(i).equals("and")) {
                array.set(i, "&&");
            } else if (array.get(i).equals("or")) {
                array.set(i, "||");
            }
            if (array.get(i).substring(0, 1).equals("'")) {
                array.set(
                        i,
                        "(\""
                        + array.get(i).substring(1,
                                array.get(i).length() - 1) + "\")");
            }
        }

        for (int i = 0; i < array.size(); i++) {
            finalIF += array.get(i);
        }
        finalIFline = "if(" + finalIF + "){";
        // System.out.println(finalIFline);
        return finalIFline;
    }

    // get set methods
    public int getGroupingVarNumber() {
        return groupingVarNumber;
    }

    public void setGroupingVarNumber(int groupingVarNumber) {
        this.groupingVarNumber = groupingVarNumber;
    }

    public ArrayList<String> getSelectedOutput() {
        return selectedOutput;
    }

    public void setSelectedOutput(ArrayList<String> selectedOutput) {
        this.selectedOutput = selectedOutput;
    }

    public ArrayList<String> getSuchthat() {
        return suchthat;
    }

    public void setSuchthat(ArrayList<String> suchthat) {
        this.suchthat = suchthat;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public int getSelectedAggNum() {
        return selectedAggNum;
    }

    public void setSelectedAggNum(int selectedAggNum) {
        this.selectedAggNum = selectedAggNum;
    }

    public String getGroupingAttributes() {
        return groupingAttributes;
    }

    public void setGroupingAttributes(String groupingAttributes) {
        this.groupingAttributes = groupingAttributes;
    }

    public ArrayList<String> getHaving() {
        return having;
    }

    public void setHaving(ArrayList<String> having) {
        this.having = having;
    }

    static// **************************************************************************************
            // transfer grouping attributes string to independent att
            // **************************************************************************************
            class getColumnName {

        static String[] groupingAttributes;
        String inputGroupAttribute;

        getColumnName(String inputGroupAttribute) {
            setInputGroupAttribute(inputGroupAttribute);
            groupingAttributes = new String[countAttr()];
        }

        public int countAttr() {
            int count = 1;
            for (int i = 0; i < inputGroupAttribute.length(); i++) {
                if (inputGroupAttribute.charAt(i) == '|') {
                    count++;
                }
            }
            return count;
        }

        public void transferGroupingAttributes() {
            int i = 0;

            groupingAttributes[0] = "";
            for (int j = 0; j < inputGroupAttribute.length(); j++) {
                if (inputGroupAttribute.charAt(j) != '|') {
                    groupingAttributes[i] += inputGroupAttribute.charAt(j) + "";
                } else if (inputGroupAttribute.charAt(j) == '|') {
                    i++;
                    groupingAttributes[i] = "";
                }
            }
        }

        public int getgroupingAttributesArraySize() {
            return groupingAttributes.length;

        }

        public void setInputGroupAttribute(String inputGroupAttribute) {
            this.inputGroupAttribute = inputGroupAttribute;
        }

        // tranfer map key form
        public String generateMapKey() {

            String mapKey = "customerProduct = ";

            for (int i = 0; i < groupingAttributes.length; i++) {
                mapKey += (attrFuncMap.get(groupingAttributes[i]) + " + "
                        + "\"|\"" + " + ");
            }

            mapKey = mapKey.substring(0, mapKey.lastIndexOf(")") + 1) + ";";
            return mapKey; // return transfered map key

        }

    }

	// **************************************************************************************
    // connect DBMS and columns name and datatype
    // **************************************************************************************
    class GenerateDataType {

        String usr = "postgres";
        String pwd = "1234";
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String cust_name, product;
        int day, month, year, quant, flag = 0;
        ArrayList allAttributes = new ArrayList();

        Map<String, String> transferDataType = new HashMap<String, String>();
        String dataBaseKey = "";

        GenerateDataType() {// should be
            // writen in
            connect();
            retreive();

            allAttributes.add("cust");
            allAttributes.add("prod");
            allAttributes.add("state");
            allAttributes.add("day");
            allAttributes.add("month");
            allAttributes.add("year");
            allAttributes.add("quant");
        }

        // Function to connect to the database
        void connect() {
            try {
                Class.forName("org.postgresql.Driver"); // Loads the required
                // driver
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

		// Function to retreive from the database and process on the resultset
        void retreive() {

            try {
                Connection con = DriverManager.getConnection(url, usr, pwd);
                ResultSet rset; // resultset object gets the set of values
                // retreived from the database
                Statement st = con.createStatement(); // statement created to
                // execute the query
                String ret = "select column_name,data_type "
                        + "from information_schema.columns "
                        + "where table_name = 'sales'";

                rset = st.executeQuery(ret); // executing the query

                while (rset.next()) {
                    dataBaseKey = rset.getString(1);
                    allAttributes.add(dataBaseKey); // get column name from DBMS

                    if (rset.getString(1) == "cust"
                            || rset.getString(1) == "prod"
                            || rset.getString(1) == "state") {
                        transferDataType.put(dataBaseKey, "String");

                    } else {
                        transferDataType.put(dataBaseKey, "int");
                    }
                }
                con.close();

            } catch (SQLException e) {
                System.out
                        .println("Connection URL or username or password errors!");
                e.printStackTrace();
            }
        }

        // for outside to enter the column name and get the datatype
        public String getDataType(String columnName) {
            return transferDataType.get(columnName);
        }

        // test if the column in array[][] is valid
        public boolean testInputColumnName(String input) {

            if (allAttributes.contains(input)) {
                return true;
            } else {
                return false;
            }
        }
    }

	// **************************************************************************************
    // when the main program confront the first F the object of this class
    // should be generate
    // **************************************************************************************
    class AggregationFunctions {

        private String ifelse = "";
        private String aggreAttrSubString = "";
        private String currentSuchthat = "";
        private String If = "";
        private String finalIf = "";
        private String finalWhere = "";

        ArrayList<String> thisSelect; // for temp select
        String thisWhere = ""; // for temp where
        ArrayList<String> thisSuchThat;
        ArrayList<String> thisSuchThatJava = new ArrayList<String>();

        AggregationFunctions() { // constructor
            thisSelect = new ArrayList<String>(selectedOutput);
            thisSuchThat = new ArrayList<String>(suchthat);
            thisWhere = where;
        }

        public String getIfelse() {
            return ifelse;
        }

        public void getFunctionsOutput() throws Exception {
            for (int i = 0; i < thisSelect.size(); i++) {
                if (thisSelect.get(i).substring(2, 4).equalsIgnoreCase("sum")) {
                    outputSum();

                } else if (thisSelect.get(i).substring(2, 4)
                        .equalsIgnoreCase("avg")) {
                    outputAvg();

                } else if (thisSelect.get(i).substring(2, 4)
                        .equalsIgnoreCase("max")) {
                    outputMax();

                } else if (thisSelect.get(i).substring(2, 4)
                        .equalsIgnoreCase("min")) {
                    outputMin();

                } else {
                }
            }
        }

        private void getAggregationFuntionAttribute() {
            for (int i = 0; i < currentSuchthat.length(); i++) {
                if (groupingVarNumber == Integer.parseInt(currentSuchthat
                        .substring(0, 1))) {
                    aggreAttrSubString = thisSelect.get(i).substring(6,
                            thisSelect.get(i).length());
                }
            }
        }

        // I need the result of such so I defined a String IF which acquired
        private void outputMin() throws Exception {
            getAggregationFuntionAttribute();
            ifelse = "{" + minAlgorithm() + "}";
        }

        private String minAlgorithm() {
            String output = "\n		if(minQuant < "
                    + attrFuncMap.get(aggreAttrSubString)
                    + ")minQuant = aSale.getQuant();\n";
            return output;
        }

        private void outputMax() throws Exception {
            getAggregationFuntionAttribute();
            ifelse = "{" + maxAlgorithm() + "}";
        }

        private String maxAlgorithm() {
            String output = "\n		if(maxQuant < "
                    + attrFuncMap.get(aggreAttrSubString)
                    + ")maxQuant = aSale.getQuant();\n";
            return output;
        }

        private void outputSum() throws Exception {
            getAggregationFuntionAttribute();
            ifelse = "{" + sumAlgorithm() + "}";
        }

        private String sumAlgorithm() {
            String output = "sumQuant = " + attrFuncMap.get(aggreAttrSubString)
                    + ";";
            return output;
        }

        private void outputAvg() throws Exception {
            getAggregationFuntionAttribute();
            ifelse = "{" + avgAlgorithm() + "}";
        }

        private String avgAlgorithm() {
            String output = "avgQuant = sumQuant/cntQuant";
            return output;
        }

        // loop transfer such that
        public void outputSuchThat() throws Exception {

            for (int i = 0; i < thisSuchThat.size(); i++) {

                currentSuchthat = thisSuchThat.get(i);
                // suchThatTransfer;
                thisSuchThatJava.add(suchThatTransfer());
                currentSuchthat = "";
                finalIf = "";
                If = "";
            }
        }

        public void ouputWhere() throws Exception {
            finalWhere = suchThatTransfer();
        }

        // transfer such that
        public String suchThatTransfer() throws Exception {

            String[] zhegezu = new String[20];
            zhegezu = currentSuchthat.split(" ");
            for (int m = 0; m < zhegezu.length; m++) {
            }
            GenerateDataType gdt2 = new GenerateDataType();

            for (int i = 0; i < zhegezu.length; i++) {

                if (zhegezu[i].contains(".")) {
                    zhegezu[i] = zhegezu[i]
                            .substring(zhegezu[i].indexOf(".") + 1);
                    if (gdt2.testInputColumnName(zhegezu[i])) {
                        zhegezu[i] = attrFuncMap.get(zhegezu[i]
                                .substring(zhegezu[i].indexOf(".") + 1));
                    } else {
                        System.out.print("check the attributes name entered");
                    }
                } else if (zhegezu[i].contains("'")) {
                    zhegezu[i] = zhegezu[i].replaceAll("'", "\"");
                } else if (zhegezu[i].equalsIgnoreCase("=")) {
                    if (zhegezu[i + 1].contains("'")) {
                        zhegezu[i + 1] = zhegezu[i + 1].replaceAll("'", "\"");
                        zhegezu[i] = ".equalsIgnoreCase(" + zhegezu[i + 1]
                                + ")";
                        zhegezu[i + 1] = "";
                    } else {
                        zhegezu[i] = "==";
                    }
                } else if (zhegezu[i].equalsIgnoreCase("and")) {
                    zhegezu[i] = "&&";
                } else if (zhegezu[i].equalsIgnoreCase("or")) {
                    zhegezu[i] = "||";
                } else if (zhegezu[i].equalsIgnoreCase("<>")) {
                    zhegezu[i] = "!=";
                } else if (zhegezu[i].equalsIgnoreCase("like")) {
                    if (zhegezu[i - 1].equalsIgnoreCase("not")) {
                        zhegezu[i - 2] = "!" + zhegezu[i - 2];
                        String temp = "";
                    }

                } else if (zhegezu[i].equalsIgnoreCase("in")) {
                    if (zhegezu[i - 1].equalsIgnoreCase("not")) {
                        zhegezu[i - 1] = "";
                        if (zhegezu[i + 1].contains("'")) {
                            zhegezu[i + 1] = zhegezu[i + 1].replaceAll("'",
                                    "\"");
                            zhegezu[i] = ".equalsIgnoreCase(" + zhegezu[i + 1]
                                    + ")";
                            zhegezu[i + 1] = "";
                            zhegezu[i - 2] = "!" + zhegezu[i - 2];
                        } else {
                            zhegezu[i] = "==";
                        }
                    } else {
                        if (zhegezu[i + 1].contains("'")) {
                            zhegezu[i + 1] = zhegezu[i + 1].replaceAll("'",
                                    "\"");
                            zhegezu[i] = ".equalsIgnoreCase(" + zhegezu[i + 1]
                                    + ")";
                            zhegezu[i + 1] = "";
                        } else {
                            zhegezu[i] = "==";
                        }
                    }

                }
            }
            for (int i = 0; i < zhegezu.length; i++) {
                if (zhegezu[i] == null) {
                    throw new Exception();
                }
                If += zhegezu[i];
            }
            finalIf = "if(" + If + ")";
            return finalIf;
        }

        // get finalWhere
        public String getFinalWhere() {
            return finalWhere;
        }

        // get finalIf
        public String getFinalIf() {
            return finalIf;
        }
    }

}
