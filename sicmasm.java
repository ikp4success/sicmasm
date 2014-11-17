/*
Name: Immanuel I George
Class: Language Processors (CIS 335)
School: Cleveland State University
Objective: Develop Sic Assembler

*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sicmasm {
	static ArrayList<String> sc = new ArrayList<String>();
	static SICTABLE stc = new SICTABLE();
	static ArrayList<String> objectCode = new ArrayList<String>();

	public static void main(String[] args) {
		// readText(args[0]);
		readText("src//test.txt");

	}

	public static ArrayList<String> CalcLocCode() {
		ArrayList<String> lc = new ArrayList<String>();

		lc.add("LOCCODE		MNEMONICS			  OBJECTCODE");
		int binaryCounter = 0;
		for (int i = 0; sc.size() > i; i++) {
			String line = sc.get(i);
			if (i == 0 || i == 1) {
				lc.add("0000" + "		" + line);
			} else if (i == sc.size() - 1 || line.contains("BASE    LENGTH")) {
				lc.add("		" + line);
				//if (binaryCounter != 0)
					//binaryCounter = binaryCounter - 1;
			} else {

				if (line.contains("+")) {
					int curBinaryValue4 = binaryCounter
							+ Integer.parseInt("4", 16);
					;
					String nLine = String.format("%04x", curBinaryValue4)
							+ "		" + line;
					lc.add(nLine);
					binaryCounter = curBinaryValue4;
				} else if (line.contains("CLEAR") || line.contains("COMPR")
						|| line.contains("DIVR") || line.contains("MULR ")
						|| line.contains("SHIFTR") || line.contains("SUBR")
						|| line.contains("TIXR")) {
					int curBinaryValue3 = binaryCounter
							+ Integer.parseInt("2", 16);
					String nLine = String.format("%04x", curBinaryValue3)
							+ "		" + line;
					lc.add(nLine);
					binaryCounter = curBinaryValue3;

				} else if (line.contains("4096")) {
					int curBinaryValue4 = binaryCounter
							+ Integer.parseInt("1000", 16);
					String nLine = String.format("%04x",
							(binaryCounter + 1000), 16) + "		" + line;
					// String nLine = curBinaryValue4
					// + "		" + line;
					lc.add(nLine);
					binaryCounter = curBinaryValue4;
				} else {

					int curBinaryValue3 = binaryCounter
							+ Integer.parseInt("3", 16);
					;
					String nLine = String.format("%04x", curBinaryValue3)
							+ "		" + line;
					lc.add(nLine);
					binaryCounter = curBinaryValue3;

				}
			}

		}

		return lc;

	}

	public static void print(ArrayList<String> list) {

		if (list != null)
			for (int i = 0; list.size() > i; i++) {
				System.out.println(list.get(i));
			}

	}

	public static void printObjectCode() {

		PrintWriter output;
		try {
			output = new PrintWriter("main.obj");

			if (objectCode != null)
				for (int i = 0; objectCode.size() > i; i++) {
					if (i == 0) {
						output.println("H^COPY" + "^" + objectCode.get(i) + "^");

					} else if (i == 1) {
						// System.out.print(objectCode.get(i));
						output.print(objectCode.get(i));
						output.println();

					} else if (i == objectCode.size() - 1) {

						output.println("E^" + objectCode.get(i));

					} else {

						output.print("T^" + objectCode.get(i) + "^");

					}

				}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void outputToTextFile(ArrayList<String> list, File fileUrl) {
		PrintWriter ouput;
		try {
			ouput = new PrintWriter(fileUrl);

			if (list != null)
				for (int i = 0; list.size() > i; i++) {
					ouput.println((list.get(i)));
				}

			ouput.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean isValidSyntax(String line) {
		Boolean isValid = false;
		String[] lineSplit = line.split("\\s+");
		int count = 0;

		for (int i = 0; lineSplit.length > i; i++) {
			count++;

		}
		if (count == 2 || count == 3) {
			isValid = true;
		}

		return isValid;
	}

	public static int CountLine(String line) {

		String[] lineSplit = line.split("\\s+");
		int count = 0;

		for (int i = 0; lineSplit.length > i; i++) {
			count++;

		}

		return count;
	}

	public static String getPC(String line) {

		// ArrayList<String> oc = new ArrayList<String>();
		String retPC = null;
		for (int i = 0; CalcLocCode().size() > i; i++) {
			String line2 = CalcLocCode().get(i);
			if (line2.contains(line)) {
				if (i + 1 < CalcLocCode().size() - 1) {
					if (!CalcLocCode().get(i + 1).contains("BASE    LENGTH")) {

						String pattern = "(.*?)(\\w+)";
						retPC = regexOperation(pattern, CalcLocCode()
								.get(i + 1));
						retPC = String.format("%03x",
								Integer.parseInt(retPC, 16));
					} else {
						String pattern = "(.*?)(\\w+)";
						retPC = regexOperation(pattern, CalcLocCode()
								.get(i + 2));
						retPC = String.format("%03x",
								Integer.parseInt(retPC, 16));
					}
				} else {
					String pattern = "(.*?)(\\w+)";
					retPC = regexOperation(pattern,
							CalcLocCode().get(CalcLocCode().size() - 1));
					retPC = String.format("%03x", Integer.parseInt(retPC, 16));
				}
			}

		}
		return retPC;
	}

	public static ArrayList<String> calcObjectCode(
			ArrayList<String> CalcdLocCode) {
		ArrayList<String> oc = new ArrayList<String>();

		for (int i = 0; CalcdLocCode.size() > i; i++) {
			String line = CalcdLocCode.get(i);
			if (i == 0 || i == CalcdLocCode.size() - 1
					|| line.contains("BASE    LENGTH")) {
				oc.add(line);
			} else {
				if (CountLine(line) == 3) {
					if (line.contains("START")) {
						oc.add(line + "		  " + "00000");
					}

					else if (line.contains("MOV")) {
						String CalcDisp = calcDisp(line, ParsePassOneCode(line));
						if (line.contains("%RA")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDA")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDA")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);
							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
						if (line.contains("%RB")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDB")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDB")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							}

							else if (line.contains("#") && !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							}

						}
						if (line.contains("%RL")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDL")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDL")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);
							}

						}
						if (line.contains("%RT")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										(calcNI + 1));

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										(calcNI + 2));

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
						if (line.contains("%RX")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDX")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDX")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);
							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							}
						}

					} else {
						String gpv = "000";
						String getOPCode = "000";
						String CalcDisp = null;

						if (stc.get(Lex(line.trim())) != null) {
							getOPCode = Lex(line.trim());
							// System.out.println("testing-" +
							// Lex(line.trim()));
							getOPCode = stc.get(getOPCode);
							CalcDisp = calcDisp(line, gpv);

						} else {
							CalcDisp = "000";
						}
						if (calcPassOne(CalcLocCode()).get(Lex(line.trim())) != null) {
							gpv = Lex(line.trim());
							gpv = calcPassOne(CalcLocCode()).get(gpv);
							CalcDisp = calcDisp(line, gpv);
						} else {
							CalcDisp = getPC(line);
						}

						if (line.contains("+") && line.contains("#")) {
							String xbpeVal = "5";
							int calcNI = Integer.parseInt(getOPCode);

							Object opcode = String.format("%02x", calcNI + 1);

							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("+") && line.contains("@")) {
							String xbpeVal = "5";
							int calcNI = Integer.parseInt(getOPCode);

							Object opcode = String.format("%02x", calcNI + 2);

							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("#") && !line.contains("+")) {

							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode);
							Object opcode = String.format("%02x", calcNI + 1);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("@") && !line.contains("+")) {
							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode);
							Object opcode = String.format("%02x", calcNI + 2);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "	  " + ObjCode);
							objectCode.add(ObjCode);

						} else {
							// System.out.println("opcode-"+getOPCode);
							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode, 16);
							Object opcode = String.format("%02x", calcNI + 3);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						}
						// oc.add(line + "				");

					}
				} else if (CountLine(line) == 4) {

					if (line.contains("START")) {
						oc.add(line + "		  " + "000000");
					}

					else if (line.contains("MOV")) {

						String CalcDisp = calcDisp(line, ParsePassOneCode(line));
						// System.out.println("Check pass"+ParsePassOneCode(line));

						if (line.contains("%RA")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDA")));

								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDA")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
						if (line.contains("%RB")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDB")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDB")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							}

							else if (line.contains("#") && !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							}

						}
						if (line.contains("%RL")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDL")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDL")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "	  " + ObjCode);
								objectCode.add(ObjCode);

							}

						}
						if (line.contains("%RT")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										(calcNI + 1));

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										(calcNI + 2));

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
						if (line.contains("%RX")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDX")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDX")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		  " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
					} else {

						String gpv = "000";
						String getOPCode = "000";
						String CalcDisp = null;

						if (stc.get(Lex(line.trim())) != null) {
							getOPCode = Lex(line.trim());
							// System.out.println("testing-" +
							// Lex(line.trim()));
							getOPCode = stc.get(getOPCode);
							CalcDisp = calcDisp(line, gpv);

						} else {
							CalcDisp = "000";
						}
						if (calcPassOne(CalcLocCode()).get(Lex(line.trim())) != null) {
							gpv = Lex(line.trim());
							gpv = calcPassOne(CalcLocCode()).get(gpv);
							CalcDisp = calcDisp(line, gpv);
						} else {
							CalcDisp = "000";
						}

						if (line.contains("+") && line.contains("#")) {
							String xbpeVal = "5";
							int calcNI = Integer.parseInt(getOPCode);

							Object opcode = String.format("%02x", calcNI + 1);

							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("+") && line.contains("@")) {
							String xbpeVal = "5";
							int calcNI = Integer.parseInt(getOPCode);

							Object opcode = String.format("%02x", calcNI + 2);

							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("#") && !line.contains("+")) {

							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode);
							Object opcode = String.format("%02x", calcNI + 1);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("@") && !line.contains("+")) {
							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode);
							Object opcode = String.format("%02x", calcNI + 2);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						} else {
							// System.out.println("opcode-"+getOPCode);
							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode, 16);
							Object opcode = String.format("%02x", calcNI + 3);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		  " + ObjCode);
							objectCode.add(ObjCode);

						}
						// oc.add(line + "				");
					}

				} else if (CountLine(line) == 2) {
					if (line.contains("START")) {
						oc.add(line + "		  " + "000000");
						objectCode.add("000000");
					} else if (line.contains("MOV")) {

						String CalcDisp = calcDisp(line, ParsePassOneCode(line));
						// System.out.println("Check pass"+ParsePassOneCode(line));

						if (line.contains("%RA")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDA")));

								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDA")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDA")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
						if (line.contains("%RB")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDB")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDB")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							}

							else if (line.contains("#") && !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDB")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							}

						}
						if (line.contains("%RL")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDL")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDL")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDL")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							}

						}
						if (line.contains("%RT")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										(calcNI + 1));

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										(calcNI + 2));

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDT")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
						if (line.contains("%RX")) {
							if (line.contains("+") && line.contains("#")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDX")));

								Object opcode = String.format("%02x",
										calcNI + 1);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("+") && line.contains("@")) {
								String xbpeVal = "5";
								int calcNI = Integer.parseInt((stc.get("LDX")));

								Object opcode = String.format("%02x",
										calcNI + 2);

								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("#")
									&& !line.contains("+")) {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 1);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else if (line.contains("@")
									&& !line.contains("+")) {
								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 2);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							} else {

								String xbpeVal = "2";
								int calcNI = Integer.parseInt((stc.get("LDX")));
								Object opcode = String.format("%02x",
										calcNI + 3);
								String ObjCode = opcode + xbpeVal + CalcDisp;
								oc.add(line + "		          " + ObjCode);
								objectCode.add(ObjCode);

							}
						}
					} else {

						String gpv = "0000";
						String getOPCode = "0000";
						String CalcDisp = null;

						if (stc.get(Lex(line.trim())) != null) {
							getOPCode = Lex(line.trim());
							// System.out.println("testing-" +
							// Lex(line.trim()));
							getOPCode = stc.get(getOPCode);
							CalcDisp = calcDisp(line, gpv);

						} else {
							CalcDisp = "0000";
						}
						if (calcPassOne(CalcLocCode()).get(Lex(line.trim())) != null) {
							gpv = Lex(line.trim());
							gpv = calcPassOne(CalcLocCode()).get(gpv);
							CalcDisp = calcDisp(line, gpv);
						} else {
							CalcDisp = "0000";
						}

						if (line.contains("+") && line.contains("#")) {
							String xbpeVal = "5";
							int calcNI = Integer.parseInt(getOPCode);

							Object opcode = String.format("%02x", calcNI + 1);

							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		          " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("+") && line.contains("@")) {
							String xbpeVal = "5";
							int calcNI = Integer.parseInt(getOPCode);

							Object opcode = String.format("%02x", calcNI + 2);

							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		          " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("#") && !line.contains("+")) {

							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode);
							Object opcode = String.format("%02x", calcNI + 1);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		          " + ObjCode);
							objectCode.add(ObjCode);

						} else if (line.contains("@") && !line.contains("+")) {
							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode);
							Object opcode = String.format("%02x", calcNI + 2);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		          " + ObjCode);
							objectCode.add(ObjCode);

						} else {
							String xbpeVal = "2";
							int calcNI = Integer.parseInt(getOPCode, 16);
							Object opcode = String.format("%02x", calcNI + 3);
							String ObjCode = opcode + xbpeVal + CalcDisp;
							oc.add(line + "		          " + ObjCode);
							objectCode.add(ObjCode);

						}

					}

				}
			}

		}
		return oc;

	}

	public static String Lex(String line) {
		String OPValue = null;
		// String pattern = "\\d+(.*?)\\w+";
		// line.trim();
		String[] egx = line.split("\\s+");
		for (int i = 0; egx.length > i; i++) {
			// System.out.println("oberhere"+egx[i]);
			OPValue = egx[i];

		}
		//

		return OPValue;
	}

	// public static String grabPassVal(String line) {
	// String passVal = null;
	// // String pattern = "\\d+(.*?)\\w+";
	// line.trim();
	// String[] egx = line.split("\\s");
	// for (int i = 0; egx.length > i; i++) {
	// String lc = calcPassOne(CalcLocCode()).get(egx[i]);
	// if (lc != null) {
	// passVal = lc;
	// } else {
	// passVal = "";
	// }
	//
	// }
	//
	// return passVal;
	// }

	public static String ParsePassOneCode(String line) {
		String psOneValue = null;
		if (line.contains(",")) {
			String pattern = "[^ ]*,";
			String egx = regexOperation(pattern, line.trim());

			if (egx.contains("%")) {
				if (egx.contains(",")) {
					String egxc2 = line.replaceAll(egx, " ");
					// System.out.println("HERE-1 "+egx2);
					return ParsePassOneCode(egxc2);

				}
			} else if (egx != null) {
				String egxC2 = egx.replace(",", "");
				psOneValue = egxC2;
			}

		} else {
			String pattern = "(\\w+)[.!?]?\\s*$";
			String egx2 = regexOperation(pattern, line);
			if (egx2.contains("%")) {
				String egx3 = egx2.replace(egx2, "");
				// System.out.println("HERE-3"+egx3);
				return ParsePassOneCode(egx3);
			} else if (egx2 != null) {
				// System.out.println("HERE-3" + egx2);
				psOneValue = egx2;
			}

		}

		return psOneValue;
	}

	public static String calcDisp(String line, String locCode) {
		String calcdDisp = null;

		if (locCode != null) {
			int getLoc;
			if (calcPassOne(CalcLocCode()).get(locCode) == null) {
				getLoc = Integer.parseInt(locCode, 16);
			} else {
				getLoc = Integer.parseInt(
						calcPassOne(CalcLocCode()).get(locCode), 16);
			}

			int getPC = Integer.parseInt(getPC(line), 16);
			// String strLoc = String.format("%04x", getLoc);
			// String strPC = String.format("%04x", getPC);

			if (getLoc > getPC) {
				calcdDisp = String.format("%03x", (getLoc - getPC));

			} else {
				int base = Integer.parseInt(
						calcPassOne(CalcLocCode()).get("LENGTH"), 16);
				if (getLoc > base) {
					calcdDisp = String.format("%03x", (getLoc - base));
				} else {
					calcdDisp = getPC(line);
				}

			}

		} else {
			calcdDisp = "";
		}
		return calcdDisp;
	}

	public static Hashtable<String, String> calcPassOne(
			ArrayList<String> calcdLocCode) {
		Hashtable<String, String> pOne = new Hashtable<String, String>();
		for (int i = 0; calcdLocCode.size() > i; i++) {
			String line = calcdLocCode.get(i);
			if (!line.contains("MNEMONICS")) {

				String getLocValue = "(.*?)(\\w+)";
				String getLoc = "([A-Z])\\w+";

				pOne.put(regexOperation(getLoc, line),
						regexOperation(getLocValue, line));
			}

		}

		return pOne;

	}

	public static String regexOperation(String pattern, String line) {
		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);
		String rop = null;
		// Now create matcher object.
		Matcher m = r.matcher(line);
		if (m.find()) {
			rop = m.group();

		}
		return rop;
	}

	public static void readText(String txtloc) {

		BufferedReader br = null;

		try {

			String sCurrentLine;
			Boolean passedSyntaxCheck = true;
			br = new BufferedReader(new FileReader(txtloc));
			int lineCount = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println(sCurrentLine);
				lineCount++;
				if (isValidSyntax(sCurrentLine)) {
					sc.add(sCurrentLine);
				} else {
					passedSyntaxCheck = false;
					System.out.println("Syntax Error found in Line "
							+ lineCount + "  SICCODE: ---" + sCurrentLine);

					break;
				}

			}
			if (passedSyntaxCheck == true) {
				System.out.println("SYNTAX CHECK  =  PASSED\n");
				CalcLocCode();
				// System.out.println("passone\n" + calcPassOne(CalcLocCode()));

				print(calcObjectCode(CalcLocCode()));
				outputToTextFile(calcObjectCode(CalcLocCode()), new File(
						"main.lst"));
				printObjectCode();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
