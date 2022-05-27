import java.io.File;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.*;

public class Processor {
	static Object[] memory = new Object[2048];
	static String pc = "00000000000000000000000000000000";
	static int mp = 0;
	static String[] registers = new String[32];
	static int cycles = 1;
	static Instruction[] phase = new Instruction[5];
	static boolean JEQ=false;

	public static void readFile(String filename) {
		registers[0] = "00000000000000000000000000000000";
		String returned = "";
		try {
			File f = new File(filename);
			Scanner myReader = new Scanner(f);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				parse(data);

			}

			myReader.close();

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	public static void parse(String data) {
		String[] splitted = data.split(" ");
		String instruction = "";
		switch (splitted[0]) {
		case "ADD":
			instruction = "0000";
			break;
		case "SUB":
			instruction = "0001";
			break;
		case "MUL":
			instruction = "0010";
			break;
		case "MOVI":
			instruction = "0011";
			break;
		case "JEQ":
			instruction = "0100";
			break;
		case "AND":
			instruction = "0101";
			break;
		case "XORI":
			instruction = "0110";
			break;
		case "JMP":
			instruction = "0111";
			break;
		case "LSL":
			instruction = "1000";
			break;
		case "LSR":
			instruction = "1001";
			break;
		case "MOVR":
			instruction = "1010";
			break;
		case "MOVM":
			instruction = "1011";
			break;

		}

		if (splitted[0].equals("JMP")) {
			instruction += toBinary(Integer.toBinaryString(Integer.parseInt(splitted[1])), 28);
		} else {
			instruction += toBinary(Integer.toBinaryString((Integer.parseInt(splitted[1].substring(1)))), 5);

			if (splitted[0].equals("ADD") || splitted[0].equals("SUB") || splitted[0].equals("MUL")
					|| splitted[0].equals("AND") || splitted[0].equals("LSL") || splitted[0].equals("LSR")) {
				instruction += toBinary(Integer.toBinaryString((Integer.parseInt(splitted[2].substring(1)))), 5);

				if (splitted[0].equals("LSL") || splitted[0].equals("LSR")) {
					instruction += "00000";
					instruction += toBinary(Integer.toBinaryString((Integer.parseInt(splitted[3]))), 13);

				} else {
					instruction += toBinary(Integer.toBinaryString((Integer.parseInt(splitted[3].substring(1)))), 5);
					instruction += "0000000000000";

				}

			} else {
				if (splitted[2].charAt(0) == 'R') {
					instruction += toBinary(Integer.toBinaryString((Integer.parseInt(splitted[2].substring(1)))), 5);
					instruction += toBinary(Integer.toBinaryString((Integer.parseInt(splitted[3]))), 18);// imm
				} else {
					instruction += "00000";
					instruction += toBinary(Integer.toBinaryString((Integer.parseInt(splitted[2]))), 18);// imm
				}
			}

		}
		Instruction I = new Instruction();
		I.inst = instruction;
		memory[mp] = I;
		mp++;
	}

	public static Instruction InstructionFetch() {
		
		Instruction instruction = (Instruction) memory[Integer.parseInt(pc, 2)];
		int temp = Integer.parseInt(pc, 2);

		temp++;
		pc = toBinary(Integer.toBinaryString(temp), 32);
	
		return instruction;

	}

	public static void InstructionDecode(Instruction I) {

		for (int i = 0; i < 4; i++) {
			I.opcode += I.inst.charAt(i);

		}

		for (int i = 4; i < 9; i++) {

			I.rd += I.inst.charAt(i);
		}

		for (int i = 9; i < 14; i++) {
			I.rs += I.inst.charAt(i);
		}

		for (int i = 14; i < 19; i++) {
			I.rt += I.inst.charAt(i);
		}

		for (int i = 19; i < 32; i++) {
			I.shamt += I.inst.charAt(i);
		}

		for (int i = 14; i < 32; i++) {
			I.imm += I.inst.charAt(i);
		}

		for (int i = 4; i < 32; i++) {
			I.address += I.inst.charAt(i);
		}

		if (Integer.parseInt(I.rs, 2) == 0) {
			I.rs_value = "00000000000000000000000000000000";
		}

		else {
			I.rs_value = registers[Integer.parseInt(I.rs, 2)];

		}

		if (Integer.parseInt(I.rt, 2) == 0) {
			I.rt_value = "00000000000000000000000000000000";
		} else {
			I.rt_value = registers[Integer.parseInt(I.rt, 2)];
		}

	}

	private static void Execute(Instruction I) {

	

		switch (I.opcode) {
		case "0000":
			I.temp = ADD(I.rs_value, I.rt_value);
			break;
		case "0001":
			I.temp = SUB(I.rs_value, I.rt_value);
			break;
		case "0010":
			I.temp = MUL(I.rs_value, I.rt_value);
			break;
		case "0011":
			I.temp = MOVI(I.imm);
			break;
		case "0100":
		     JEQ(registers[Integer.parseInt(I.rd, 2)], I.rs_value, I.imm);
			break;
		case "0101":
			I.temp = AND(I.rs_value, I.rt_value);
			break;
		case "0110":
			I.temp = XORI(I.rs_value, I.imm);
			break;
		case "0111":
		             JMP(I.address);
			break;
		case "1000":
			I.temp = LSL(I.rs_value, I.shamt);
			break;
		case "1001":
			I.temp = LSR(I.rs_value, I.shamt);
			break;
		case "1010":
			I.temp = MOVR(I.rs_value, I.imm);
			break;
		case "1011":
			I.temp = MOVM(I.rs_value, I.imm);
			break;
		}

	}

	private static void MEM(Instruction I) {
		if (I.rd.equals("00000")) {

			return;
		}

		if (I.opcode.equals("1010")) {
			I.temp = (String) memory[Integer.parseInt(I.temp, 2)];

		} 
		else if (I.opcode.equals("1011")) {
				if(Integer.parseInt(I.temp, 2)<2048&&Integer.parseInt(I.temp, 2)>1023) {
			memory[Integer.parseInt(I.temp, 2)] = registers[Integer.parseInt(I.rd, 2)];
			System.out.println("value in Memory address " + Integer.parseInt(I.temp, 2) + " has been updated to "+
			registers[Integer.parseInt(I.rd, 2)]);
				}
				else {
			
					System.out.println("cant write on memory for data address that does not exit ");
				}
		}

	}

	private static void WB(Instruction I) {
		
		if (I.opcode.equals("1011")||I.opcode.equals("0111")||I.opcode.equals("0100")) {
			
			return;
		}

		


			if(I.rd.equals("00000")) {
				return;
			}
			else {
				registers[Integer.parseInt(I.rd, 2)] = I.temp;
				System.out.println("R" + Integer.parseInt(I.rd, 2) + "'s value has been udpated to " + I.temp);
			}
		
	
		
		

	}

	private static String ADD(String operand1, String operand2) {
		long x;
		long y;
		if (operand1.charAt(0) == '1') {

			String h = "";
			for (int i = 0; i < operand1.length(); i++) {
				h += operand1.charAt(i) == '0' ? '1' : '0';
			}
			x = (Long.parseLong(h, 2) + 1) * -1;

		} else {
			x = Long.parseLong(operand1, 2);

		}
		if (operand2.charAt(0) == '1') {

			String z = "";
			for (int i = 0; i < operand2.length(); i++) {
				z += operand2.charAt(i) == '0' ? '1' : '0';
			}

			y = (Long.parseLong(z, 2) + 1) * -1;

		} else {
			y = Long.parseLong(operand2, 2);

		}

		long result = x + y;
	

		String res = toBinary(Long.toBinaryString(result), 32);

		return res;

	}

	private static String SUB(String operand1, String operand2) {

		long x;
		long y;
		if (operand1.charAt(0) == '1') {

			String h = "";
			for (int i = 0; i < operand1.length(); i++) {
				h += operand1.charAt(i) == '0' ? '1' : '0';
			}
			x = (Long.parseLong(h, 2) + 1) * -1;

		} else {
			x = Long.parseLong(operand1, 2);

		}
		if (operand2.charAt(0) == '1') {

			String z = "";
			for (int i = 0; i < operand2.length(); i++) {
				z += operand2.charAt(i) == '0' ? '1' : '0';
			}

			y = (Long.parseLong(z, 2) + 1) * -1;

		} else {
			y = Long.parseLong(operand2, 2);

		}

		long result = x - y;
	

		String res = toBinary(Long.toBinaryString(result), 32);

		return res;

	}

	private static String MUL(String operand1, String operand2) {

		long x;
		long y;
		if (operand1.charAt(0) == '1') {

			String h = "";
			for (int i = 0; i < operand1.length(); i++) {
				h += operand1.charAt(i) =='0'?'1':'0';
			}
			x = (Long.parseLong(h,2) + 1)*-1;

		} else {
			x = Long.parseLong(operand1, 2);

		}
		if (operand2.charAt(0) == '1') {

			String z = "";
			for (int i = 0; i < operand2.length(); i++) {
				z += operand2.charAt(i) == '0' ? '1' : '0';
			}

			y = (Long.parseLong(z, 2) + 1) * -1;

		} else {
			y = Long.parseLong(operand2, 2);

		}

		long result = x * y;
		

		String res = toBinary(Long.toBinaryString(result), 32);

		return res;

	}

	private static String MOVI(String imm) {
		String x= toBinaryImm(imm, 32);
		return x;

	}

	private static void JEQ(String rs, String rt, String imm) {
		String res = "";
		int temp = 0;

		if (rs.equals(rt)) {
			JEQ=true;
			temp = Integer.parseInt(pc, 2) + Integer.parseInt(imm, 2);
			res = toBinary(Integer.toBinaryString(temp),32);
				pc=res;
				
				
		}

		
	}

	private static String AND(String operand1, String operand2) {

		long x;
		long y;
		if (operand1.charAt(0) == '1') {

			String h = "";
			for (int i = 0; i < operand1.length(); i++) {
				h += operand1.charAt(i) =='0'?'1':'0';
			}
			x = (Long.parseLong(h,2) + 1)*-1;

		} else {
			x = Long.parseLong(operand1, 2);

		}
		if (operand2.charAt(0) == '1') {

			String z = "";
			for (int i = 0; i < operand2.length(); i++) {
				z += operand2.charAt(i) == '0' ? '1' : '0';
			}

			y = (Long.parseLong(z, 2) + 1) * -1;

		} else {
			y = Long.parseLong(operand2, 2);

		}

		long result = x & y;
		

		String res = toBinary(Long.toBinaryString(result), 32);

		return res;

	}

	private static String XORI(String operand1, String operand2) {

		long x;
		long y;
		if (operand1.charAt(0) == '1') {

			String h = "";
			for (int i = 0; i < operand1.length(); i++) {
				h += operand1.charAt(i) =='0'?'1':'0';
			}
			x = (Long.parseLong(h,2) + 1)*-1;

		} else {
			x = Long.parseLong(operand1, 2);

		}
		if (operand2.charAt(0) == '1') {

			String z = "";
			for (int i = 0; i < operand2.length(); i++) {
				z += operand2.charAt(i) == '0' ? '1' : '0';
			}

			y = (Long.parseLong(z, 2) + 1) * -1;

		} else {
			y = Long.parseLong(operand2, 2);

		}

		long result = x ^ y;
		

		String res = toBinary(Long.toBinaryString(result), 32);

		return res;

	}

	private static void JMP(String address) {
	
		String temp = "";
		for (int i = 1; i <=4; i++) {
			temp += pc.charAt(i);
			
		}
		
		temp += address;
	
		pc=temp;
	
	}

	private static String LSL(String operand1, String shamt) {
		long x;
		if (operand1.charAt(0) == '1') {

			String h = "";
			for (int i = 0; i < operand1.length(); i++) {
				h += operand1.charAt(i) =='0'?'1':'0';
			}
			x = (Long.parseLong(h,2) + 1)*-1;

		} else {
			x = Long.parseLong(operand1, 2);

		}
		String shiftedi = toBinary(Long.toBinaryString(x << Integer.parseInt(shamt, 2)), 32);

		return shiftedi;
	}

	private static String LSR(String operand1, String shamt) {
		long x;
		if (operand1.charAt(0) == '1') {

			String h = "";
			for (int i = 0; i < operand1.length(); i++) {
				h += operand1.charAt(i) =='0'?'1':'0';
			}
			x = (Long.parseLong(h,2) + 1)*-1;

		} else {
			x = Long.parseLong(operand1, 2);

		}
		String shiftedi = toBinary(Long.toBinaryString(x >>> Integer.parseInt(shamt, 2)), 32);

		return shiftedi;
	}

	private static String MOVR(String operand1, String imm) {

		String result = ADD(operand1,imm) ;
		String res = toBinary(result, 11);

		return res;
	}

	private static String MOVM(String operand1, String imm) {
		String result = ADD(operand1,imm) ;
		System.out.println(Integer.parseInt(result,2));
		String res = toBinary(result, 11);

		return res;

	
	}

	private static void pipeLine() {
		int count = 0;
		int ID_count = 0;
		int EX_count = 0;
		boolean firstEnter = true;
		boolean jumps=false;
	

		while (!isEmpty(phase) || firstEnter) {
			firstEnter = false;

			System.out.println("cycle number : " + cycles);

			if (phase[4] != null) {
				System.out.println("instruction " + phase[4].id + " finished execution");
				phase[4] = null;
			}

			if (phase[3] != null) {
				System.out.println("instruction " + phase[3].id + " is being executed in WB stage");
				WB(phase[3]);
				phase[4] = phase[3];
				phase[3] = null;
				if (phase[4].id == Instruction.count - 1) {// last instruction so i dont have to do another cycle to
															// take it out of wb stage
					System.out.println("instruction " + phase[4].id + " finished execution");
					break;
				}
			}
			if (phase[2] != null) {
				EX_count++;
				if (EX_count == 2) {
					System.out.println("instruction " + phase[2].id + " is being executed in MEM stage");
					if(phase[2].opcode.equals("0111")||JEQ){// flushing out
						phase[1]=null;
						phase[0]=null;
						cycles++;
						EX_count =0;
						ID_count=0;	
						MEM(phase[2]);
						phase[3] = phase[2];
						phase[2] = null;
				       JEQ=false;
						System.out.println("------------------------");
						continue;
						
					}
					EX_count = 0;
					MEM(phase[2]);
					phase[3] = phase[2];
					phase[2] = null;
				} else {
					
					System.out.println("instruction " + phase[2].id + " is being executed in EXECUTE stage");
					if(!phase[2].opcode.equals("0111")&&!phase[2].opcode.equals("0100")) {
						Execute(phase[2]);	
					}
					else {
						jumps=true;
					
					}
					
				}
			}

			if (phase[1] != null) {
				
				ID_count++;

				if (ID_count == 2) {
					System.out.println("instruction " + phase[1].id + " is being executed in EXECUTE stage");
					ID_count = 0;
					phase[2] = phase[1];
					phase[1] = null;
				} else
					System.out.println("instruction " + phase[1].id + " is being executed in DECODE stage");

			}

			if (phase[0] != null) {
				System.out.println("instruction " + phase[0].id + " is being executed in DECODE stage");
				InstructionDecode(phase[0]);
				phase[1] = phase[0];
				phase[0] = null;
			}

			if ( cycles % 2 != 0) {
				Instruction I = InstructionFetch();
				if(I==null) {
					cycles++;
					System.out.println("------------------------");
					continue;
			
				}
				phase[0] = I;
				System.out.println("instruction " + phase[0].id + " is being executed in FETCH stage");

			
			}
				if(jumps) {
					Execute(phase[2]);
					jumps=false;
				}
			cycles++;
			System.out.println("------------------------");

		}
		
		
		
		
		
		System.out.println("------------------------");
		for (int i = 0; i < registers.length; i++) {
			if (registers[i] != null) {
				System.out.println("R" + i + " = " + registers[i]);
				System.out.println("------------------------");

			}
		}
		System.out.println("PC = " + pc);
		System.out.println("------------------------------------------------");
		
	
		for (int i = 0; i < memory.length; i++) {
		String	x= i<1024 ? "(Instructions)":"(Data)";
			if (memory[i] != null) {
				System.out.println("memory at address " + i + x +  "= " + memory[i]);
				System.out.println("------------------------");

			}
		}

	}

	public static boolean isEmpty(Instruction[] a) {
		for (Instruction x : a) {
			if (x != null)
				return false;
		}
		return true;
	}

	public static String toBinary(String x, int num) {
		if (x.length() < num)
			while (x.length() < num) {
				x = '0' + x;
			}
		else
			return x.substring(x.length() - num);
		return x;
	}

public static String toBinaryImm(String imm,int num) {
		while(imm.length()<num) {
			imm=imm.charAt(0)+imm;
		}
		return imm;
}

	public static void main(String[] args) {

		readFile("Assembly test.txt");
		registers[2] = "00000000000000000000000000000010";
		registers[3] = "00000000000000000000000000000011";
		memory[1032] = "00000000000000000000000000000111";

		pipeLine();
	



	}
}
