public class Instruction {
    static int count =1;
    int id;
       String inst="";
       String opcode=""; 
       String rs="";      
       String rt ="";     
       String rd ="";      
       String shamt="";   
       String imm ="";    
       String address =""; 
       String rs_value="";
       String rt_value="";
       String temp="";
       public Instruction() {
           id=count;
           count++;
                   
       }
       
       public  String toString() {
    	   return this.inst;
       }
}
