import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author apple
 */
public class Phi_Operator {
	// To save the content of Phi Operator
	private ArrayList Select = new ArrayList(); // Listed of projected
												// attributes for the query
												// output
	private int N_Group_ = 0; // Number of grouping variables
	private String Where_Con = null;// Where Content
	private ArrayList Group_Attr = new ArrayList(); // List of Grouping
													// Attributes
	private ArrayList Functions = new ArrayList(); // list of sets of aggregate
													// functions
	private ArrayList Condition_Vect = new ArrayList(); // list of predicates to
														// define the range for
														// the grouping
														// variables.
	private ArrayList Having = new ArrayList(); // Predicate for the having
												// clause.
	private int Error_Num = 0;

	/**
	 * @return the Select
	 */
	public ArrayList getSelect() {
		return Select;
	}

	/**
	 * @param Select
	 *            the Select to set
	 */
	public void setSelect(ArrayList Select) {
		this.Select = Select;
	}

	/**
	 * @return the N_Group_
	 */
	public int getN_Group_() {
		return N_Group_;
	}

	/**
	 * @param N_Group_
	 *            the N_Group_ to set
	 */
	public void setN_Group_(int N_Group_) {
		this.N_Group_ = N_Group_;
	}

	/*
	 * @return the Where_Con
	 */
	public String getWhere_Con() {
		return Where_Con;
	}

	/*
	 * @param Where_Con the Where_Con to set
	 */

	public void setWhere_Con(String where_Con) {
		Where_Con = where_Con;
	}

	/**
	 * @return the Group_Attr
	 */
	public ArrayList getGroup_Attr() {
		return Group_Attr;
	}

	/**
	 * @param Group_Attr
	 *            the Group_Attr to set
	 */
	public void setGroup_Attr(ArrayList Group_Attr) {
		this.Group_Attr = Group_Attr;
	}

	/**
	 * @return the Functions
	 */
	public ArrayList getFunctions() {
		return Functions;
	}

	/**
	 * @param Functions
	 *            the Functions to set
	 */
	public void setFunctions(ArrayList Functions) {
		this.Functions = Functions;
	}

	/**
	 * @return the Condition_Vect
	 */
	public ArrayList getCondition_Vect() {
		return Condition_Vect;
	}

	/**
	 * @param Condition_Vect
	 *            the Condition_Vect to set
	 */
	public void setCondition_Vect(ArrayList Condition_Vect) {
		this.Condition_Vect = Condition_Vect;
	}

	/**
	 * @return the Having
	 */
	public ArrayList getHaving() {
		return Having;
	}

	/**
	 * @param Having
	 *            the Having to set
	 */
	public void setHaving(ArrayList Having) {
		this.Having = Having;
	}

	/**
	 * @return the error_Num
	 */

	public int getError_Num() {
		return Error_Num;
	}

	/**
	 * @param error_Num
	 *            the error_Num to set
	 */

	public void setError_Num(int error_Num) {
		Error_Num = error_Num;
	}

	/*
	 * Function to print out the content of the specific arraylist.
	 */
	public void Print_Array(ArrayList list) {
		int num = 0;
		while (num < list.size()) {
			System.out.println(list.get(num));
			num++;
		}
	}

	public void Print_Int(int n) {
		System.out.println(n);
	}

	/*
	 * Function to remove the null content in a specific arraylist
	 */
	public void Remove_Content(ArrayList list, ArrayList Content) {
		for (int tmp = 0; tmp < Content.size(); tmp++) {
			if (list.contains(Content.get(tmp).toString())) {
				list.remove(Content.get(tmp));
			} else
				continue;
		}
	}

	// Check method
	public void Check_Content(ArrayList New, ArrayList Original) {
		if (!(New.get(0).toString().equals("null") && New.size() == 1)) {
			for (int tmp = 0; tmp < New.size(); tmp++) {
				if (!Original.contains(New.get(tmp).toString())) {
					JOptionPane.showMessageDialog(null, "Fail to Analysis!",
							"Error", JOptionPane.ERROR_MESSAGE);
					int num = getError_Num();
					num++;
					setError_Num(num);
					break;
				} else
					continue;
			}
		}
	}

	// get having contents
	public ArrayList Get_Having(ArrayList list, String Spilter_Outer,
			String Spilter_Inner) {
		ArrayList Rtu = new ArrayList();
		if (list.get(0).toString().equals("null")) {
			return Rtu = list;
		} else {
			for (int tmp = 0; tmp < list.size(); tmp++) {
				Scanner scan = new Scanner(list.get(tmp).toString());
				scan.useDelimiter(Spilter_Outer);
				String Str = scan.next();
				scan = new Scanner(Str);
				scan.useDelimiter("\\" + Spilter_Inner);
				scan.next();
				Rtu.add(scan.next());
			}
		}
		return Rtu;
	}

	// deal with null value
	public ArrayList Null_To_True(ArrayList list) {
		if (list.size() == 1 && list.get(0).toString().equals("null"))
			list.set(0, "true");
		return list;
	}

	public String Null_To_True(String input) {
		if (input.equals("null"))
			input = "true";
		return input;
	}

}
