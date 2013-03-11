package warmup;

import javax.sip.*;
import javax.sip.message.*;
import javax.sip.header.*;
import javax.sip.address.*;
import javax.sip.address.URI;
import javax.swing.JFrame;

import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.*;


public class SipPhone implements SipListener{
	private SipFactory mySipFactory;
	private SipStack mySipStack;
	private ListeningPoint myListeningPoint;
	private SipProvider mySipProvider;
	private MessageFactory myMessageFactory;
	private HeaderFactory myHeaderFactory;
	private AddressFactory myAddressFactory;
	private Properties myProperties;
	private String myIP; //client
    private String destination; //destination
	private int myPort;
	public String myID;
	public String myProxy; //proxy

    private Dialog myDialog;
    private ClientTransaction myClientTransaction;
    private ServerTransaction myServerTransaction; 
	
	
	public SipPhone(String myID, String myProxy){
		try {
        	this.myID = myID;
        	this.myProxy = myProxy;
        	
            myPort=5060;
            
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
            
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
	}

	
	
	public void sendRegister(String destination){
		try{
			this.destination = destination;
			
			Socket s = new Socket("java.com", 80);
	        myIP = s.getLocalAddress().getHostAddress(); // this what actually works
	        s.close();
			
			Address destinationAddress = myAddressFactory.createAddress("sip:"+this.destination+";lr");
			
			javax.sip.address.URI myRequestURI=destinationAddress.getURI();
			Address contactAddress = myAddressFactory.createAddress("sip:"+myIP+":"+myPort);
			
			ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader myViaHeader = myHeaderFactory.createViaHeader(myIP, myPort,"udp", "z9hG4bKnashds7");
			viaHeaders.add(myViaHeader);
			
			MaxForwardsHeader myMaxForwardsHeader = myHeaderFactory.createMaxForwardsHeader(70);
			CallIdHeader myCallIdHeader = mySipProvider.getNewCallId();
			CSeqHeader myCSeqHeader = myHeaderFactory.createCSeqHeader(1L, "REGISTER");
			
			SipURI fromURI = myAddressFactory.createSipURI(myID, myProxy);
			Address fromAddress = myAddressFactory.createAddress(fromURI);
			FromHeader myFromHeader = myHeaderFactory.createFromHeader(fromAddress, "456248");
			ToHeader myToHeader = myHeaderFactory.createToHeader(fromAddress, null);
			
			Request myRequest = myMessageFactory.createRequest(myRequestURI, "REGISTER", myCallIdHeader, myCSeqHeader,myFromHeader,myToHeader, viaHeaders, myMaxForwardsHeader);
	
			ContactHeader myContactHeader = myHeaderFactory.createContactHeader(contactAddress);
			myRequest.addHeader(myContactHeader);
			
			ClientTransaction myClientTransaction = mySipProvider.getNewClientTransaction(myRequest);
			myClientTransaction.sendRequest();
			
			System.out.println("[SENT] "+myRequest.toString());
		}
        catch(Exception e) {
            e.printStackTrace();
        }
	}

	
	public static void main(String[] args) throws ParseException, InvalidArgumentException, SipException, UnknownHostException, IOException {
		SipPhone phone = new SipPhone("testID31","fred.item.ntnu.no");
		phone.sendRegister("129.241.209.243");
	}
	
	
	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRequest(RequestEvent requestReceivedEvent) {
		// TODO Auto-generated method stub
		Request myRequest = requestReceivedEvent.getRequest();
		String method=myRequest.getMethod();
		try{
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
		
		
		
		System.out.println("[RECEIVED]"+myRequest.toString());
	}

	@Override
	public void processResponse(ResponseEvent responseReceivedEvent) {
		Response myResponse = responseReceivedEvent.getResponse();
		System.out.println("[RECEIVED]"+myResponse.toString());
	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
