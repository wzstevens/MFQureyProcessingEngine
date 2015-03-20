import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Analysis {
	private String filepath = null;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void main() throws FileNotFoundException {
		Phi_Operator Phi = new Phi_Operator();
		try {
			// Retrieve related file content and store into a vector
			Phi.setSelect(Insert_Keywords(
					Get_Value("SELECT ATTRIBUTE(S):", getFilepath()), ","));
			Phi.setN_Group_(Integer.parseInt(Get_Value(
					"NUMBER OF GROUPING VARIABLES(n):", getFilepath())));

			Phi.setWhere_Con(Get_Value("WHERE CONTENT", getFilepath()));

			Phi.setGroup_Attr(Insert_Keywords(
					Get_Value("GROUPING ATTRIBUTES(V):", getFilepath()), ","));

			Phi.setFunctions(Insert_Keywords(
					Get_Value("F-VECT([F]):", getFilepath()), ","));

			Phi.setCondition_Vect(Insert_Keywords(
					Get_Value("SELECT CONDITION-VECT([T]):", getFilepath()),
					";"));
			Execuate(Phi.getCondition_Vect(), getFilepath());

			Phi.setHaving(Insert_Keywords(
					Get_Value("HAVING CLAUSE(G):", getFilepath()), ";"));
			Execuate(Phi.getHaving(), getFilepath());

			Phi.Check_Content(Phi.Get_Having(Phi.getHaving(), " ", "."),
					Phi.getSelect());

			// Check whether the grouping attributes are included in the select
			// clause
			Phi.Check_Content(Phi.getGroup_Attr(), Phi.getSelect());

			// Remove the same content as grouping attributes from select clause
			Phi.Remove_Content(Phi.getSelect(), Phi.getGroup_Attr());

			// operation to the null value
			Phi.setWhere_Con(Phi.Null_To_True(Phi.getWhere_Con()));
			Phi.setCondition_Vect(Phi.Null_To_True(Phi.getCondition_Vect()));

			// start to parse if no errors
			if (Phi.getError_Num() == 0) {

				Mfquery mfq = new Mfquery();
				mfq.setSelectedOutput(Phi.getSelect());
				mfq.setGroupingVarNumber(Phi.getN_Group_());
				mfq.setWhere(Phi.getWhere_Con());
				mfq.setGroupingAttributes(transferAttr(Phi.getGroup_Attr()));
				mfq.setSuchthat(Phi.getCondition_Vect());
				mfq.setHaving(Phi.getHaving());
				mfq.main();
			}

		} catch (Exception e) {
			int num = Phi.getError_Num();
			num++;
			Phi.setError_Num(num);
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fail to Analysis", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// get methods according to key words
	private String Get_Value(String Key, String Filepath)
			throws FileNotFoundException {
		String Last = "";
		try {
			Scanner scan = new Scanner(new FileInputStream(Filepath));
			while (scan.hasNextLine()) {
				String next = scan.nextLine();
				if (next.length() != 0 && next.equals(Key)) {
					next = scan.nextLine();
					while (next.length() == 0) {
						next = scan.nextLine();
						continue;
					}
					Last = next;
					break;
				} else
					continue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Last;
	}

	// Insert keywords into arraylists
	private ArrayList Insert_Keywords(String Str, String Split) {
		Scanner scan = new Scanner(Str);
		scan.useDelimiter(Split);
		ArrayList list = new ArrayList();
		while (scan.hasNext()) {
			list.add(scan.next().trim());
		}
		return list;
	}

	// get "such that" and "having" contents
	private void Execuate(ArrayList list, String Filepath)
			throws FileNotFoundException {
		Scanner scan = new Scanner(new FileInputStream(Filepath));
		String Str = "";
		while (!Str.equals(list.get(list.size() - 1))) {
			Str = scan.nextLine();
			continue;
		}
		while (Str.length() != 0 && !Str.contentEquals("HAVING CLAUSE(G):")) {
			if (scan.hasNextLine()) {
				Str = scan.nextLine();
				list.add(Str);
			} else
				break;
		}
		if (list.get(list.size() - 1).toString().length() == 0
				|| list.get(list.size() - 1).toString()
						.equals("HAVING CLAUSE(G):"))
			list.remove(list.size() - 1);
	}

	// transfor the Arraylist form of grouping attributes to strings
	static String transferAttr(ArrayList outAttr) {

		String temp = "";
		for (int i = 0; i < outAttr.size(); i++) {
			temp += (outAttr.get(i) + "|");
		}

		temp = temp.substring(0, temp.lastIndexOf("|"));

		return temp;

	}

}
