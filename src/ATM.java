import java.util.Scanner;
import java.io.IOException;

public class ATM {

    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    private User newUser;


    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int TRANSFER = 4;
    public static final int LOGOUT = 5;
    public static final int FIRST_NAME_WIDTH = 20;
    public static final int LAST_NAME_WIDTH = 30;

    public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;
    public static final int OVERFLOW = 3;

    public ATM() {
        in = new Scanner(System.in);

        // activeAccount = new BankAccount(1234, 123456789, 0, new User("Ryan", "Wilson"));


        try {
			this.bank  = new Bank();
		} catch (IOException e) {
			// cleanup any resources (i.e., the Scanner) and exit
		}
    }

    public void startup() {
        long accountNo;
        String accountString = "";
        int pin;
        System.out.println("Welcome to the AIT ATM!\n");

                while (true) {
                    System.out.print("Account No.: ");
                    accountString = in.nextLine();
                    if (accountString.equals("+")) {
                        accountCreation();
                    } else {
                    System.out.print("PIN        : ");
                    pin = in.nextInt();
                    login(Long.parseLong(accountString, 10),pin);
                }

          }
    }


    public void login(long accountNo, int pin) {
    	if (isValidLogin(accountNo, pin)) {
        	activeAccount = bank.login(accountNo, pin);
            System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!");
            boolean validLogin = true;
            while (validLogin) {
                switch (getSelection()) {
                    case VIEW: showBalance(); break;
                    case DEPOSIT: deposit(); break;
                    case WITHDRAW: withdraw(); break;
                    case TRANSFER: transfer(); break;
                    case LOGOUT: validLogin = false; in.nextLine(); break;
                    default: System.out.println("\nInvalid selection.\n"); break;
                }
            }
        } else {
            if (accountNo == -1 && pin == -1) {
                shutdown();
            } else {
                System.out.println("\nInvalid account number and/or PIN.\n");
            }
        }
    }




    public boolean isValidLogin(long accountNo, int pin) {
    	boolean valid = true;
    	try {
    		valid = bank.login(accountNo, pin) != null ? true : false;
    	}catch (Exception e) {
    		valid = false;
    	}
        return valid;
    }

    public int getSelection() {
        System.out.println("\n[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Transfer money");
        System.out.println("[5] Logout");

        return in.nextInt();
    }

    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }


    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        int status = activeAccount.deposit(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.OVERFLOW) {
            System.out.println("\nDeposit rejected. Amount would cause balance to exceed $999,999,999,999.99.\n");
        } else {
        	bank.update(activeAccount);
        	bank.save();
        	System.out.println("\nDeposit accepted.");
        }
    }

    public void withdraw() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
        } else if (status == ATM.SUCCESS) {
        	bank.update(activeAccount);
        	bank.save();
            System.out.println("\nWithdrawal accepted.");
        }
    }

    public void transfer() {
    	long secondedAccountNumber;
    	boolean validAccount = true;
        System.out.print("\nEnter account: ");
        if(in.hasNextLong()) {
        	secondedAccountNumber = in.nextLong();
        }else {
        	secondedAccountNumber = 0;
        	in.nextLine();
        	in.nextLine();
        }

        System.out.print("Enter amount: ");
        double amount = in.nextDouble();
        if(bank.getAccount(secondedAccountNumber) == null) {
        	validAccount = false;
        }


        if(validAccount) {
        	BankAccount transferAccount = bank.getAccount(secondedAccountNumber);
        	int withdrawStatus = activeAccount.withdraw(amount);
        	if (withdrawStatus == ATM.INVALID) {
                System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
            } else if (withdrawStatus == ATM.INSUFFICIENT) {
                System.out.println("\nTransfer rejected. Insufficient funds.\n");
            } else if (withdrawStatus == ATM.SUCCESS) {
            	int depositStatus = transferAccount.deposit(amount);
                if (depositStatus == ATM.OVERFLOW) {
                    System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.\n");
                } else if (depositStatus == ATM.SUCCESS) {
                	System.out.println("\nTransfer accepted.");
                	bank.update(activeAccount);
                	bank.save();
                }
            }
        }else {
        	System.out.println("\nTransfer rejected. Destination account not found.\n");
        }

    }


    public void accountCreation() {
        System.out.print("\nFirst Name: ");
        String firstName = 	in.nextLine();
        
       
        if (firstName.length() <= 20 && firstName.length() > 0) {
            System.out.print("Last Name: ");
            String lastName = in.nextLine();

            if (lastName.length() <= 30 && lastName.length() > 0) {
                System.out.print("Pin: ");
                if (in.hasNextInt()) {
                    int pin = in.nextInt();
                    in.nextLine();

                    if (pin >= 1000 && pin <= 9999) {
                        newUser = new User(firstName, lastName);
                        BankAccount newAccount = bank.createAccount(pin, newUser);
                        System.out.print("\nThank you. Your account number is ");
                        System.out.println(newAccount.getAccountNo() + ".");
                        System.out.println("Please login to access your newly created account.");
                        bank.update(newAccount);
                        bank.save();
                    } else {
                        System.out.println("\nYour pin must be between 1000 and 9999.\n");
                    }
                } else {
                    in.nextLine();
                    System.out.println("\nYour pin must be numeric.\n");
                }
            } else {
                System.out.println("\nYour last name must be between 1 and 30 characters long.");
            }
        } else {
            System.out.println("\nYour first name must be between 1 and 20 characters long.");
        }
    }




    public void shutdown() {
        if (in != null) {
            in.close();
        }

        System.out.println("\nGoodbye!");
        System.exit(0);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();

        atm.startup();
    }
}
