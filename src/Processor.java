import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class Processor {
static String [] memory=new String [2048];
static int pc=0;
final int zero_reg=0;
static int [] registers=new int[31];

public static String readFile (String filename) {
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
	return returned;

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
	if(instruction=="0111") {
		instruction+=splitted[1];
	}
	else {
		instruction+=Integer.toBinaryString((Integer.parseInt(splitted[1].substring(1))));

		if(instruction.equals("0000")||instruction.equals("0001")||instruction.equals("0010")||instruction.equals("0101")||instruction.equals("1000")||instruction.equals("1001")) {
			instruction+=Integer.toBinaryString((Integer.parseInt(splitted[2].substring(1))));
			if(instruction.equals("1000")||instruction.equals("1001")) {
				instruction+="00000";
				instruction+=Integer.toBinaryString((Integer.parseInt(splitted[3])));
			}
			else {
				instruction+=Integer.toBinaryString((Integer.parseInt(splitted[3].substring(1))));
			}
		
		}
		else {
			if(splitted[2].charAt(0)=='R') {
				instruction+=Integer.toBinaryString((Integer.parseInt(splitted[2].substring(1))));
				instruction+=Integer.toBinaryString((Integer.parseInt(splitted[3])));
			}
			else {
				instruction+="00000";
				instruction+=Integer.toBinaryString((Integer.parseInt(splitted[3])));
			}	
		}
		
	}
	
}

	
}
