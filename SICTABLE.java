import java.util.*;

public class SICTABLE {
	Hashtable<String, String> hsh = new Hashtable<String, String>();

	public SICTABLE() {
		
		//OPCODE
		hsh.put("ADD", "18");
		hsh.put("ADDF", "58");
		hsh.put("ADDR", "90");
		hsh.put("AND", "40");
		hsh.put("CLEAR", "B4");
		hsh.put("COMP","28");
		hsh.put("COMPF","88");
		hsh.put("COMPR","A0");
		hsh.put("DIV","24");
		hsh.put("DIVF","64");
		hsh.put("DIVR","64");
		hsh.put("FIX","C4");
		hsh.put("FLOAT","C0");
		hsh.put("HIO","F4");
		hsh.put("J","3C");
		hsh.put("JEQ","30");
		hsh.put("JGT","34");
		hsh.put("JLT","38");
		hsh.put("JSUB","48");
		hsh.put("LDA","00");
		hsh.put("LDB","68");
		hsh.put("LDCH","50");
		hsh.put("LDF","70");
		hsh.put("LDL","08");
		hsh.put("LDS","6C");
		hsh.put("LDT","74");
		hsh.put("LDX","04");
		hsh.put("LPS","D0");
		hsh.put("MULF","60");
		hsh.put("MULR","98");
		hsh.put("NORM","C8");
		hsh.put("OR","44");
		hsh.put("RD","D8");
		hsh.put("RMO","AC");
		hsh.put("RSUB","4C");
		hsh.put("SHIFTL","A4");
		hsh.put("SHIFTR","A8");
		hsh.put("SIO","F0");
		hsh.put("SSK","EC");
		hsh.put("RSUB","4C");
		hsh.put("STA","0C");
		hsh.put("STB","78");
		hsh.put("STCH","54");
		hsh.put("STF","80");
		hsh.put("STI","D4");
		hsh.put("STL","14");
		hsh.put("STS","7C");
		hsh.put("STSW","E8");
		hsh.put("STT","84");
		hsh.put("STX","10");
		hsh.put("SUB","1C");
		hsh.put("SUBF","5C");
		hsh.put("SUBR","94");
		hsh.put("SVC","B0");
		hsh.put("TD","E0");
		hsh.put("TIO","F8");
		hsh.put("TIX","2C");
		hsh.put("TIXR","B8");
		hsh.put("WD","DC");
		
		//REGISTERTABLE
		hsh.put("A","0");
		hsh.put("X","1");
		hsh.put("L","2");
		hsh.put("B","3");
		hsh.put("S","4");
		hsh.put("T","5");
		hsh.put("F","6");
		hsh.put("PC","8");
		hsh.put("SW","9");
		
		
		
		
	}

	public String get(Object Key) {
		String getky = (String)hsh.get(Key);
		return getky;
	}

}
