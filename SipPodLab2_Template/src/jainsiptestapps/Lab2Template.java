package jainsiptestapps;

import javax.sip.*;
import javax.sip.message.*;
import javax.sip.header.*;
import javax.sip.address.*;

import java.net.*;
import java.util.*;

import javax.swing.JFrame;

public class Lab2Template extends JFrame implements SipListener {
	private static final long serialVersionUID = 666L;
	
    private SipFactory mySipFactory;
    private SipStack mySipStack;
    private ListeningPoint myListeningPoint;
    private SipProvider mySipProvider;
    private MessageFactory myMessageFactory;
    private HeaderFactory myHeaderFactory;
    private AddressFactory myAddressFactory;
    private Properties myProperties;
    private String myIP;
    private int myPort;
    public String myID;
    public String myProxy;
    
    
    //New ones:
    private Dialog myDialog;
    private ClientTransaction myClientTransaction;
    private ServerTransaction myServerTransaction;    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	//Sample ID : MySIPUA
    	//Sample Proxy: 129.241.208.187:5060
    	
    	//...
    	
    	Lab2Template app = new Lab2Template("testID12", "fred.item.ntnu.no");
        
        try {
            app.sendRegister("fred.item.ntnu.no");
            app.sendInvite("sip:mac@fred.item.ntnu.no");
        }
        catch(Exception e) {
            e.printStackTrace();
        }    	
    }
    
    public Lab2Template(String myID, String myProxy){
        try {
        	this.myID = myID;
        	this.myProxy = myProxy;
        	
            myPort=6700;
            //myIP=InetAddress.getLocalHost().getHostAddress();
            
            Socket s = new Socket("java.com", 80);
            myIP = s.getLocalAddress().getHostAddress(); // this what actually works
            s.close();
            
            
            System.out.println("Initialized at IP "+ myIP+", port "+myPort); 


            //SipFactory is used to create other factories
            mySipFactory=SipFactory.getInstance();
            mySipFactory.setPathName("gov.nist");

            //Message factory creates the SIP messages e.g. REGISTER, INVITE etc...
            myMessageFactory=mySipFactory.createMessageFactory();
            //Header factory creates the SIP headers e.g. From, To, Via etc...
            myHeaderFactory=mySipFactory.createHeaderFactory();
            //Address factory creates the SIP URI Address or simply SIP URI 
            myAddressFactory=mySipFactory.createAddressFactory();

            //SIP Stack Creation:           
            myProperties=new Properties();
            myProperties.setProperty("javax.sip.STACK_NAME", "myStack");
            mySipStack=mySipFactory.createSipStack(myProperties);

            //Create a listening point using above SipStack.
            myListeningPoint=mySipStack.createListeningPoint(myIP, myPort, "udp");
            
            //Create the SipProvider and add the above created listening point.
            mySipProvider=mySipStack.createSipProvider(myListeningPoint);
            mySipProvider.addSipListener(this);
            
            setSize(400, 300);
            setVisible(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public void sendRegister(String destination) throws Exception {
    	//Refer to lab sheet for filling this method
    }    

    public void sendInvite(String destination) throws Exception {
    	//Refer to lab sheet for filling this method
    }
    
    // Following processRequest method is called when some other UA sends 
    // a request(e.g. INVITE request) to this UA. But code within this method
    // has been provided only for sample purpose and is not required
    // for this lab. So you may ignore this method for now.
    public void processRequest(RequestEvent requestReceivedEvent) {
        Request myRequest=requestReceivedEvent.getRequest();
        String method=myRequest.getMethod();
        try {

            if (method.equals("INVITE")) {

                myServerTransaction=mySipProvider.getNewServerTransaction(myRequest);
                Response myResponse=myMessageFactory.createResponse(180,myRequest);
                ToHeader responseToHeader=(ToHeader) myResponse.getHeader("To");
                responseToHeader.setTag("454326");
                Address contactAddress = myAddressFactory.createAddress("sip:"+myIP+":"+myPort);
                Header myContactHeader = myHeaderFactory.createContactHeader(contactAddress);
                myResponse.addHeader(myContactHeader);

                myServerTransaction.sendResponse(myResponse);
                myDialog=myServerTransaction.getDialog();

                System.out.println("[RECEIVED] "+myRequest.toString());
                System.out.println("[SENT] "+myResponse.toString());
                System.out.println("Dialog status: "+myDialog.getState().toString());
            }

            else if  (method.equals("ACK")) {

                System.out.println("[RECEIVED] "+myRequest.toString());
                System.out.println("Dialog status: "+myDialog.getState().toString());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    // Following processResponse method is called when some other UA sends 
    // a response(e.g. RINGING (180) or OK (200)) to this UA. You will have to
    // at least, print the content of this response. 
    public void processResponse(ResponseEvent responseReceivedEvent) {
    	Response myResponse;
    	
    	//Get the response responseReceivedEvent in myResponse object
        // ...
    	myResponse = responseReceivedEvent.getResponse();
    	myClientTransaction=responseReceivedEvent.getClientTransaction();
        
        //Print the content of myResponse
    	// ...
    	System.out.println("[RECEIVED] "+myResponse.toString());

        try {

            if (myResponse.getStatusCode()==180) {
            	//Get the dialog from ClientTransaction and print the status
            	// ...
            }
            else if (myResponse.getStatusCode()==200) {
            	//Get the dialog from ClientTransaction and send ACK, Refer to lab sheet for
            	// hints on doing this
            	// ...
				if (myDialog != null)
				{
				// REGISTER does not create a dialog!
				
		
				
		
				}
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    //You may igonore following methods for now. But do not delete them.
    public void processTimeout(TimeoutEvent timeoutEvent) {
    }
    public void processTransactionTerminated(TransactionTerminatedEvent tevent) {
    }
    public void processDialogTerminated(DialogTerminatedEvent tevent) {
    }
    public void processIOException(IOExceptionEvent tevent) {
    }    
    
}
