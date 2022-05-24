import java.io.File;


import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.*;



public class Processor {
static Object [] memory=new Object [2048];
static String pc="00000000000000000000000000000000";
static int mp=0;
static String [] registers=new String[32];
static int cycles=1;
static Instruction []phase=new Instruction[5];


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
	Instruction I = new Instruction();
	I.inst=instruction;
	memory[mp]=I;
	mp++;
}
public static String toBinary(String x,int n) {
	while(x.length()<n) {
		x='0'+x;
	}
	return x;
}
public static Instruction InstructionFetch() {
	Instruction instruction =(Instruction)memory[Integer.parseInt(pc,2)];
	int temp = Integer.parseInt(pc,2);
	temp++;  	
	pc = toBinary(Integer.toBinaryString(temp),32);
	return instruction;
	
}
public static void InstructionDecode(Instruction I) {

	   
	   for(int i=0;i<4;i++) {
		 I.opcode+=I.inst.charAt(i);
		  
	   }
	   
	   for(int i=4;i<9;i++) {
		  
			 I.rd+=I.inst.charAt(i);  
		   }
		
		
		  
	   
	
	   
	   for(int i=9;i<14;i++) {
		   I.rs+=I.inst.charAt(i);
	   }
	   
	   for(int i=14;i<19;i++) {
			 I.rt+=I.inst.charAt(i);
	   }
	   
	   
	   for(int i=19;i<32;i++) {
			 I.shamt+=I.inst.charAt(i);
	   }
	   
	   for(int i=14;i<32;i++) {
			 I.imm+=I.inst.charAt(i);
	   }
	
	   
	   for(int i=4;i<32;i++) {
			 I.address+=I.inst.charAt(i);
	   }
	   if(Integer.parseInt(I.rs,2)==0) {
		  I. rs_value="00000";
	   }
	   else {
		 I.rs_value=registers[Integer.parseInt(I.rs,2)];
	   }
	   if(Integer.parseInt(I.rt,2)==0) {
		  I.rt_value="00000";
	   }
	   else {
	  I.rt_value=registers[Integer.parseInt(I.rt,2)];
	   }
	
	
//   System.out.println("Instruction "+Integer.parseInt(pc,2));
//       System.out.println("opcode = "+I.opcode);
//       System.out.println("rs = "+I.rs);
//       System.out.println("rt = "+I.rt);
//       System.out.println("rd = "+I.rd);
//       System.out.println("shift amount = "+I.shamt);
//       System.out.println("immediate = "+I.imm);
//       System.out.println("address = "+I.address);
//       System.out.println("value[rs] = "+I.rs_value);
//       System.out.println("value[rt] = "+I.rt_value);
//       System.out.println("----------");
//     
  
  
     
      
}

private static void Execute(Instruction I) {
	
	switch(I.opcode) {
	 case "0000":I.temp=ADD(I.rs_value,I.rt_value) ;break;
	 case "0001":I.temp=SUB(I.rs_value,I.rt_value)  ;break;
	 case "0010":I.temp=MUL(I.rs_value,I.rt_value)  ;break;
	 case "0011":I.temp=MOVI(I.imm) ;break;
	 case "0100":I.temp=JEQ(toBinary(registers[Integer.parseInt(I.rd,2)],5),I.rs_value,I.imm);break;
	 case "0101":I.temp=AND(I.rs_value,I.rt_value);break;
	 case "0110":I.temp=XORI(I.rs_value,I.imm);break;
	 case "0111":I.temp=JMP(I.address)  ;break;
	 case "1000":I.temp=LSL(I.rs_value,I.shamt)  ;break;
	 case "1001":I.temp=LSR(I.rs_value,I.shamt)  ;break;
	 case "1010":I.temp=MOVR(I.rs_value,I.imm) ;break;
	 case "1011":I.temp=MOVM(I.rs_value,I.imm);break;
	}

	
	
			
}

private static void MEM(Instruction I) {

	if(I.opcode.equals("1010")) {
	I.temp=(String)memory[Integer.parseInt(I.temp,2)];	
	}
	else
		if(I.opcode.equals("1011")) {
			memory[Integer.parseInt(I.temp,2)]=registers[Integer.parseInt(I.rd,2)];
		}
	
	
}
private static void WB(Instruction I) {
	switch(I.opcode) {
	 case "0100":pc=I.temp ;break;
	 case "0111":pc=I.temp ;break;
	 default:registers[Integer.parseInt(I.rd,2)]=I.temp;break;
	}
}


   
private  static String ADD(String operand1,String operand2) {
		
	int result=Integer.parseInt(operand1,2)+Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}



private  static String SUB(String operand1,String operand2) {
	
	int result=Integer.parseInt(operand1,2)-Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}
private  static String MUL(String operand1,String operand2) {
	
	int result=Integer.parseInt(operand1,2)*Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}
private static String MOVI (String imm) {
	return imm;
	
}
private static String JEQ (String rs,String rt,String imm) {
	String res="";
	int temp=0;
	if(rs.equals(rt)) {
	temp=Integer.parseInt(pc,2)+1+Integer.parseInt(imm,2);
	res=Integer.toBinaryString(temp);
	return res;
}
	return pc;





	
}


private  static String AND(String operand1,String operand2) {
	
	int result=Integer.parseInt(operand1,2)&Integer.parseInt(operand2,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}

private  static String XORI(String operand1,String imm) {
	
	int result=Integer.parseInt(operand1,2)^Integer.parseInt(imm,2);
	String res=toBinary(Integer.toBinaryString(result),5);

	
	return res;
	
}
private static String JMP(String address) {
	String temp="";
	for(int i=28;i<32;i++) {
		temp+=pc.charAt(i);
	}
	temp+=address;
	return temp;
}

private static String LSL(String operand1,String shamt) {
	   int i = Integer.parseInt(operand1, 2);
	   String shiftedi =toBinary(Integer.toBinaryString(i<<Integer.parseInt(shamt,2)),5);
	   
	   return shiftedi;
}

private static String LSR(String operand1,String shamt) {
	   int i = Integer.parseInt(operand1, 2);
	   String shiftedi =toBinary(Integer.toBinaryString(i>>>Integer.parseInt(shamt,2)),5);
	   
	   return shiftedi;
}
private static String MOVR(String operand1,String imm ) {
	return ADD(operand1,imm);
}

private static String MOVM(String operand1,String imm ) {
	return ADD(operand1,imm);
}



private static void pipeLine() {
	int maxCycles=mp+((mp-1)*2);
	int count=0;
	int ID_count=0;
	int EX_count=0;

		
	while(cycles<=maxCycles) {
		System.out.println("cycle number : "+cycles);	
		
	if(phase[4]!=null){
		System.out.println("instruction "+phase[4].id + " finished execution");
		phase[4]=null;
	}
	
	if(phase[3]!=null) {
		System.out.println("instruction "+phase[3].id + " is being executed in WB stage");
		WB(phase[3]);
		phase[4]=phase[3];
		phase[3]=null;
	}	
	if(phase[2]!=null) {
		EX_count++;
		if(EX_count==2) {
		System.out.println("instruction "+phase[2].id + " is being executed in MEM stage");
		EX_count=0;
		MEM(phase[2]);
		phase[3]=phase[2];
		phase[2]=null;
		}
		else
			System.out.println("instruction "+phase[2].id + " is being executed in EXECUTE stage");
	}
	
	if(phase[1]!=null) {
		
		ID_count++;
	
		if(ID_count==2) {
		System.out.println("instruction "+phase[1].id + " is being executed in EXECUTE stage");
		ID_count=0;
		Execute(phase[1]);
		phase[2]=phase[1];
		phase[1]=null;	
	}
		else
			System.out.println("instruction "+phase[1].id + " is being executed in DECODE stage");

	}
	
	if(phase[0]!=null) {
		System.out.println("instruction "+phase[0].id + " is being executed in DECODE stage");
		InstructionDecode(phase[0]);
		phase[1]=phase[0];
		phase[0]=null;
	}	
	
	if(count!=mp&&cycles%2!=0) {
		Instruction I = InstructionFetch();
		phase[0]=I;
		System.out.println("instruction "+phase[0].id + " is being executed in FETCH stage");

	
		count++;
	}
	
	

	cycles++;
	System.out.println("------------------------");
		
	}
	
		
		
		
		
		
		
}



	public static void main(String[] args) {

		readFile("Assembly test.txt");
		registers[2]="00100";
		registers[3]="00010";
	
        pipeLine();
    	System.out.println(registers[1]);
    	System.out.println(registers[4]);
    	System.out.println(registers[5]);
    	System.out.println(registers[6]);
    	System.out.println(registers[7]);
	

	   
		
	
	}
}
