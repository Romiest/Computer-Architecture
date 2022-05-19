import java.io.File;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.*;
public class Processor {
static String [] memory=new String [2048];
static String pc="00000000000000000000000000000000";
static int mp=0;
static String [] registers=new String[32];

public static void readFile (String filename) {
	String returned ="";
	try {
	File f=new File(filename);
	Scanner myReader = new Scanner(f);
    while (myReader.hasNextLine()) {
      String data = myReader.nextLine();
      parse(data);
    
    }
    
    myReader.close();
    
  }
	catch (FileNotFoundException e) {
    System.out.println("An error occurred.");
    e.printStackTrace();
  }
	

}
public  static  void parse(String data) {
	String [] splitted=data.split(" ");
	String instruction="";
	switch(splitted[0]) {
	 case "ADD":instruction= "0000"  ;break;
	 case "SUB":instruction= "0001"  ;break;
	 case "MUL":instruction= "0010"  ;break;
	 case "MOVI":instruction= "0011"  ;break;
	 case "JEQ":instruction= "0100"  ;break;
	 case "AND":instruction= "0101"  ;break;
	 case "XORI":instruction= "0110"  ;break;
	 case "JMP":instruction= "0111"  ;break;
	 case "LSL":instruction= "1000"  ;break;
	 case "LSR":instruction= "1001"  ;break;
	 case "MOVR":instruction= "1010"  ;break;
	 case "MOVM":instruction= "1011"  ;break;
	 

	
	}
	String opcode=instruction;
	if(opcode=="0111") {
		instruction+=toBinary(Integer.toBinaryString(Integer.parseInt(splitted[1])),28);
	}
	else {
		instruction+=toBinary(Integer.toBinaryString((Integer.parseInt(splitted[1].substring(1)))),5);

		if(opcode.equals("0000")||opcode.equals("0001")||opcode.equals("0010")||opcode.equals("0101")||opcode.equals("1000")||opcode.equals("1001")) {
			instruction+=toBinary(Integer.toBinaryString((Integer.parseInt(splitted[2].substring(1)))),5);
			
		
			if(opcode.equals("1000")||opcode.equals("1001")) {
				instruction+="00000";
				instruction+=toBinary(Integer.toBinaryString((Integer.parseInt(splitted[3]))),13);
			
				
			}
			else {
				instruction+=toBinary(Integer.toBinaryString((Integer.parseInt(splitted[3].substring(1)))),5);
				instruction+="0000000000000";
		
			}
		
		}
		else {
			if(splitted[2].charAt(0)=='R') {
				instruction+=toBinary(Integer.toBinaryString((Integer.parseInt(splitted[2].substring(1)))),5);
				instruction+=toBinary(Integer.toBinaryString((Integer.parseInt(splitted[3]))),18);
			}
			else {
				instruction+="00000";
				instruction+=toBinary(Integer.toBinaryString((Integer.parseInt(splitted[2]))),18);
			}	
		}
		
	}
	memory[mp]=instruction;
	mp++;
}
public static String toBinary(String x,int n) {
	while(x.length()<n) {
		x='0'+x;
	}
	return x;
}
public static void InstructionFetch() {
while(Integer.parseInt(pc,2)<mp) {
	String instruction = memory[Integer.parseInt(pc,2)];
	InstructionDecode(instruction);
	int temp=Integer.parseInt(pc,2)+1;
	pc=toBinary(Integer.toBinaryString(temp),32);
	}
}
public static void InstructionDecode(String instruction) {
	   String opcode=""; 
	   String rs="";      
	   String rt ="";     
	   String rd ="";      
	   String shamt="" ;   
	   String imm ="";    
	   String address =""; 
	   String rs_value="";
	   String rt_value="";
	
	   
	   for(int i=0;i<4;i++) {
		   opcode+=instruction.charAt(i);
		  
	   }
	   
	   for(int i=4;i<9;i++) {
		  
			   rd+=instruction.charAt(i);   
		   }
		
		
		  
	   
	
	   
	   for(int i=9;i<14;i++) {
		   rs+=instruction.charAt(i);
	   }
	   
	   for(int i=14;i<19;i++) {
		   rt+=instruction.charAt(i);
	   }
	   
	   
	   for(int i=19;i<32;i++) {
		   shamt+=instruction.charAt(i);
	   }
	   
	   for(int i=14;i<32;i++) {
		   imm+=instruction.charAt(i);
	   }
	
	   
	   for(int i=4;i<32;i++) {
		   address+=instruction.charAt(i);
	   }
	   if(Integer.parseInt(rs,2)==0) {
		   rs_value="00000";
	   }
	   else {
		   rs_value=registers[Integer.parseInt(rs,2)];
	   }
	   if(Integer.parseInt(rt,2)==0) {
		   rt_value="00000";
	   }
	   else {
	   rt_value=registers[Integer.parseInt(rt,2)];
	   }
	
	
	   System.out.println("Instruction "+Integer.parseInt(pc,2));
       System.out.println("opcode = "+opcode);
       System.out.println("rs = "+rs);
       System.out.println("rt = "+rt);
       System.out.println("rd = "+rd);
       System.out.println("shift amount = "+shamt);
       System.out.println("immediate = "+imm);
       System.out.println("address = "+address);
       System.out.println("value[rs] = "+rs_value);
       System.out.println("value[rt] = "+rt_value);
       System.out.println("----------");
     
       Execute(opcode,shamt,address,imm,rs_value,rt_value,rd);
  
     
      
}

private static void Execute(String opcode, String shamt, String address,String imm, String rs_value, String rt_value,String rd) {
	String temp="";
	switch(opcode) {
	 case "0000":temp=ADD(rs_value,rt_value) ;break;
	 case "0001":temp=SUB(rs_value,rt_value)  ;break;
	 case "0010":temp=MUL(rs_value,rt_value)  ;break;
	 case "0011":temp=MOVI(imm) ;break;
	 case "0100":temp=JEQ(toBinary(registers[Integer.parseInt(rd,2)],5),rs_value,imm);break;
	 case "0101":temp=AND(rs_value,rt_value);break;
	 case "0110":temp=XORI(rs_value,imm);break;
	 case "0111":temp=JMP(address)  ;break;
	 case "1000":temp=LSL(rs_value,shamt)  ;break;
	 case "1001":temp=LSR(rs_value,shamt)  ;break;
	 case "1010":temp=MOVR(rs_value,imm) ;break;
	 case "1011":temp=MOVM(rs_value,imm);break;
	}
	MEM(opcode,temp,rd);
	
			
}

public static void MEM(String opcode,String value,String rd) {
	String temp="";
	if(opcode.equals("1010")) {
	temp=memory[Integer.parseInt(temp,2)];	
	}
	else
		if(opcode.equals("1011")) {
			memory[Integer.parseInt(temp,2)]=registers[Integer.parseInt(rd,2)];
		}
	
	WB(opcode,value,rd);
}
public static void WB(String opcode,String value,String rd) {
	switch(opcode) {
	 case "0100":pc=value ;break;
	 case "0111":pc=value ;break;
	 default:registers[Integer.parseInt(rd,2)]=value;break;
	}
}


   
public  static String ADD(String operand1,String operand2) {
		
	int result=Integer.parseInt(operand1,2)+Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}



public  static String SUB(String operand1,String operand2) {
	
	int result=Integer.parseInt(operand1,2)-Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}
public  static String MUL(String operand1,String operand2) {
	
	int result=Integer.parseInt(operand1,2)*Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}
public static String MOVI (String imm) {
	return imm;
	
}
public static String JEQ (String rs,String rt,String imm) {
	String res="";
	int temp=0;
	if(rs.equals(rt)) {
	temp=Integer.parseInt(pc,2)+1+Integer.parseInt(imm,2);
	res=Integer.toBinaryString(temp);
	return res;
}
	return pc;





	
}


public  static String AND(String operand1,String operand2) {
	
	int result=Integer.parseInt(operand1,2)&Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}

public  static String XORI(String operand1,String imm) {
	
	int result=Integer.parseInt(operand1,2)^Integer.parseInt(imm,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}
public static String JMP(String address) {
	String temp="";
	for(int i=28;i<32;i++) {
		temp+=pc.charAt(i);
	}
	temp+=address;
	return temp;
}

public static String LSL(String operand1,String shamt) {
	   int i = Integer.parseInt(operand1, 2);
	   String shiftedi =toBinary(Integer.toBinaryString(i<<Integer.parseInt(shamt,2)),5);
	   
	   return shiftedi;
}

public static String LSR(String operand1,String shamt) {
	   int i = Integer.parseInt(operand1, 2);
	   String shiftedi =toBinary(Integer.toBinaryString(i>>>Integer.parseInt(shamt,2)),5);
	   
	   return shiftedi;
}
public static String MOVR(String operand1,String imm ) {
	return ADD(operand1,imm);
}

public static String MOVM(String operand1,String imm ) {
	return ADD(operand1,imm);
}






	public static void main(String[] args) {

		readFile("Assembly test.txt");
		registers[2]="00010";
		registers[3]="00010";
	   InstructionFetch();

//	    
//		System.out.println(LSL("00100","0000000000001"));
	   
		
	
	}
}
