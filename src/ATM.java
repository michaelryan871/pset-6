import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    
    ////////////////////////////////////////////////////////////////////////////
    //                                                                        //
    // Refer to the Simple ATM tutorial to fill in the details of this class. //
    // You'll need to implement the new features yourself.                    //
    //                                                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    public static final int VIEW = 1; 
    public static final int DEPOSIT = 2; 
    public static final int WITHDRAW = 3; 
    public static final int LOGOUT = 4; 
    
    public static final int INVALID = 0; 
    public static final int INSUFFICIENT = 1; 
    public static final int SUCCESS = 2; 
     
    /**
     * Constructs a new instance of the ATM class.
     */
    
    public ATM() {
        this.in = new Scanner(System.in);
       
        try {
			this.bank = new Bank();
		} catch (IOException e) {
			// cleanup any resources (i.e., the Scanner) and exit
		}
    }
    
    /*
     * 
     */
    public void startup() {
    	System.out.println("Welcome to the AIT ATM!");
    	
    	while(true) {
    		System.out.print("Account No.: ");
    		long accountNo = in.nextLong();
    		
    		System.out.print("IN        : ");
    		int pin = in.nextInt();
    		
    		if(isValidLogin(accountNo, pin)) {
    			System.out.println("\nHello, again " + activeAccount.getAccountHolder().getFirstName() + "!\n");
    			
    			boolean validLogin = true; 
    			while (validLogin) {
    				case VIEW: showBalance(); break;
    				case DEPOSIT: deposit(); break; 
    				case WITHDRAW: withdraw(); break;
    				case LOGOUT: validLogin = false; break;
    			}
    		}
    		
    	}
    }
    
    
    /*
     * Application execution begins here.
     */
    
    public static void main(String[] args) {
        ATM atm = new ATM();
    }
}
