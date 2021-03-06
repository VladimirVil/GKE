package nodesV2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.sharksystem.asap.ASAPChunkReceivedListener;
import net.sharksystem.asap.ASAPEngine;
import net.sharksystem.asap.ASAPEngineFS;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPStorage;
import net.sharksystem.asap.MultiASAPEngineFS;
import net.sharksystem.asap.MultiASAPEngineFS_Impl;
import net.sharksystem.asap.apps.ASAPJavaApplication;
import net.sharksystem.asap.apps.ASAPJavaApplicationFS;
import net.sharksystem.asap.apps.ASAPMessageReceivedListener;
import net.sharksystem.asap.apps.ASAPMessages;

public class GKENode implements ASAPJavaApplication{
	// the final values re from RFC3526, id 16. Modular Exponential DH Group 4096
	// bit
	final static BigInteger generator = BigInteger.valueOf(2);
//	final static String hex = "FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1\n" + 
//			"      29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD\n" + 
//			"      EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245\n" + 
//			"      E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED\n" + 
//			"      EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D\n" + 
//			"      C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F\n" + 
//			"      83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D\n" + 
//			"      670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B\n" + 
//			"      E39E772C 180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9\n" + 
//			"      DE2BCBF6 95581718 3995497C EA956AE5 15D22618 98FA0510\n" + 
//			"      15728E5A 8AAAC42D AD33170D 04507A33 A85521AB DF1CBA64\n" + 
//			"      ECFB8504 58DBEF0A 8AEA7157 5D060C7D B3970F85 A6E1E4C7\n" + 
//			"      ABF5AE8C DB0933D7 1E8C94E0 4A25619D CEE3D226 1AD2EE6B\n" + 
//			"      F12FFA06 D98A0864 D8760273 3EC86A64 521F2B18 177B200C\n" + 
//			"      BBE11757 7A615D6C 770988C0 BAD946E2 08E24FA0 74E5AB31\n" + 
//			"      43DB5BFC E0FD108E 4B82D120 A9210801 1A723C12 A787E6D7\n" + 
//			"      88719A10 BDBA5B26 99C32718 6AF4E23C 1A946834 B6150BDA\n" + 
//			"      2583E9CA 2AD44CE8 DBBBC2DB 04DE8EF9 2E8EFC14 1FBECAA6\n" + 
//			"      287C5947 4E6BC05D 99B2964F A090C3A2 233BA186 515BE7ED\n" + 
//			"      1F612970 CEE2D7AF B81BDD76 2170481C D0069127 D5B05AA9\n" + 
//			"      93B4EA98 8D8FDDC1 86FFB7DC 90A6C08F 4DF435C9 34063199\n" + 
//			"      FFFFFFFF FFFFFFFF";
//	
	final static String hex = "AF33";
	final static BigInteger base = BigInteger.valueOf(Integer.parseInt(hex, 16));
	public static final CharSequence APP = "GKE";
	private MultiASAPEngineFS multiEngine;
	CharSequence name;
	CharSequence folder;
	Collection<CharSequence> formats;
	Collection<CharSequence> recipents;
    private GKEMessage_Impl[] messages = null;

	BigInteger pubKey; // this one will be also used as a order identificator

	private Map<CharSequence, ASAPMessageReceivedListener> messageReceivedListener = new HashMap<>();

	GKENode(BigInteger pubKey) throws IOException, ASAPException {
		this.pubKey = pubKey;
	}
	
	public GKENode(BigInteger pubkey, String name, String folder, Collection<CharSequence> formats, Collection<CharSequence> recipents)
			throws IOException, ASAPException {
		this.pubKey = pubKey;
		//this.asap = ASAPJavaApplicationFS.createASAPJavaApplication("TestAliceNoUSe", "test/ALiceNoUse", formats);
		this.name = name;
		this.folder = folder;
		this.formats = formats;
		this.multiEngine = this.getMulitEngine();
		this.recipents = recipents;

		if (formats != null && !formats.isEmpty()) {
			// ensure that supported format engine are up and running
			for (CharSequence i : formats) {
				this.multiEngine.createEngineByFormat(i);
			}
		}
	}
//
//	public GKENode(BigInteger pubkey, String name, String folder, Collection<CharSequence> formats)
//			throws IOException, ASAPException {
//		this.pubKey = pubKey;
//		this.asap = ASAPJavaApplicationFS.createASAPJavaApplication("TestAliceNoUSe", "test/ALiceNoUse", formats);
//		this.name = name;
//		this.folder = folder;
//		this.formats = formats;
//		this.multiEngine = this.getMulitEngine();
//
//		if (formats != null && !formats.isEmpty()) {
//			// ensure that supported format engine are up and running
//			for (CharSequence i : formats) {
//				this.multiEngine.createEngineByFormat(i);
//			}
//		}
//	}
//
//	public void sendMessage(CharSequence format, CharSequence uri, Collection<CharSequence> recipents, byte[] message)
//			throws ASAPException, IOException {
//		this.asap.sendASAPMessage(format, uri, recipents, message);
//	}
//
//	private Map<CharSequence, ASAPMessageReceivedListener> messageReceivedListener = new HashMap<>();
//
//	// @Override
//	public void setASAPMessageReceivedListener(CharSequence format, ASAPMessageReceivedListener listener)
//			throws ASAPException, IOException {
//
//		// wrap receiver and add listener to multiengine
//		// ASAPStorage appStorage = ASAPEngineFS.getASAPStorage(APP, folder);
//		System.out.println("**********************************************************************");
//		System.out.println("Name is " + this.name);
//		System.out.println(this.folder.chars());
//		System.out.println("format: ");
//		format.chars().forEachOrdered(System.out::println);
//		this.getMulitEngine().setASAPChunkReceivedListener(format, new MessageChunkReceivedListenerWrapper(listener));
//
//		// set with multi engine
//		this.messageReceivedListener.put(format, listener);
//		System.out.println("**********************************************************************");
//		System.out.println("Finished listener");
//		System.out.println(this.folder.chars());
//	}
//
//	private MultiASAPEngineFS getMulitEngine() throws IOException, ASAPException {
//		// TODO: re-create any time - keep track of potential changes in external
//		// storage (file system)?
//		MultiASAPEngineFS multiEngine = MultiASAPEngineFS_Impl.createMultiEngine(name, folder,
//				MultiASAPEngineFS.DEFAULT_MAX_PROCESSING_TIME, null);
//
//		for (CharSequence format : this.messageReceivedListener.keySet()) {
//			ASAPMessageReceivedListener listener = this.messageReceivedListener.get(format);
//			multiEngine.setASAPChunkReceivedListener(format, new MessageChunkReceivedListenerWrapper(listener));
//		}
//
//		return multiEngine;
//	}
//
//	public ASAPJavaApplication getAsap() {
//		return asap;
//	}
//
//	public void setAsap(ASAPJavaApplication asap) {
//		this.asap = asap;
//	}
//
//	public BigInteger getPubKey() {
//		return pubKey;
//	}
//
//	public void setPubKey(BigInteger pubKey) {
//		this.pubKey = pubKey;
//	}
//
//	public static CharSequence getApp() {
//		return APP;
//	}
//
//	public CharSequence getName() {
//		return name;
//	}
//
//	public void setName(CharSequence name) {
//		this.name = name;
//	}
//
//	public CharSequence getFolder() {
//		return folder;
//	}
//
//	public void setFolder(CharSequence folder) {
//		this.folder = folder;
//	}
//
//	public Collection<CharSequence> getFormats() {
//		return formats;
//	}
//
//	public void setFormats(Collection<CharSequence> formats) {
//		this.formats = formats;
//	}
//
//	private String getLogStart() {
//		return this.getClass().getSimpleName() + ": ";
//	}
//
//	private class MessageChunkReceivedListenerWrapper implements ASAPChunkReceivedListener {
//		private final ASAPMessageReceivedListener listener;
//
//		public MessageChunkReceivedListenerWrapper(ASAPMessageReceivedListener listener) throws ASAPException {
//			if (listener == null)
//				throw new ASAPException("listener must not be null");
//			this.listener = listener;
//		}
//
//		@Override
//		public void chunkReceived(String format, String sender, String uri, int era) {
//			System.out.println(getLogStart() + "chunk received - convert to asap message received");
//			try {
//				ASAPEngine engine = this.getMultiEngine().getEngineByFormat(format);
//				ASAPMessages messages = engine.getIncomingChunkStorage(sender).getASAPChunkCache(uri, era);
//				this.listener.asapMessagesReceived(messages);
//				System.out.println("************Inside listener*****");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//			} catch (ASAPException | IOException e) {
//				System.out.println(getLogStart() + e.getLocalizedMessage());
//			}
//		}
//
//		public MultiASAPEngineFS getMultiEngine() {
//			return multiEngine;
//		}
//
//	}
//
//	public MultiASAPEngineFS getMultiEngine() {
//		return multiEngine;
//	}
//
//	public void setMultiEngine(MultiASAPEngineFS multiEngine) {
//		this.multiEngine = multiEngine;
//	}

	@Override
	public void handleConnection(InputStream arg0, OutputStream arg1) throws IOException, ASAPException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendASAPMessage(CharSequence format, CharSequence uri, Collection<CharSequence> recipents, byte[] message)
			throws ASAPException, IOException {
		
		  ASAPEngine engine = this.getMulitEngine().getEngineByFormat(format);

	        engine.createChannel(uri, recipents);
	        engine.add(uri, message);
		
	}

	@Override
	public void setASAPMessageReceivedListener(CharSequence format, ASAPMessageReceivedListener listener)
			throws ASAPException, IOException {
	
        // wrap receiver and add listener to multiengine
        this.getMulitEngine().setASAPChunkReceivedListener(format, new MessageListenerWrapper(listener));

        // set with multi engine
        this.messageReceivedListener.put(format, listener);
		
	}
	
	private MultiASAPEngineFS getMulitEngine() throws IOException, ASAPException {
		// TODO: re-create any time - keep track of potential changes in external
		// storage (file system)?
		MultiASAPEngineFS multiEngine = MultiASAPEngineFS_Impl.createMultiEngine(name, folder,
				MultiASAPEngineFS.DEFAULT_MAX_PROCESSING_TIME, null);

		for (CharSequence format : this.messageReceivedListener.keySet()) {
			ASAPMessageReceivedListener listener = this.messageReceivedListener.get(format);

			multiEngine.setASAPChunkReceivedListener(format, new MessageListenerWrapper(listener));
		}

		return multiEngine;
	}
	
//	private class MessageChunkReceivedListenerWrapper implements ASAPChunkReceivedListener {
//		private final ASAPMessageReceivedListener listener;
//
//		public MessageChunkReceivedListenerWrapper(ASAPMessageReceivedListener listener) throws ASAPException {
//			if (listener == null)
//				throw new ASAPException("listener must not be null");
//			this.listener = listener;
//		}
//
//		@Override
//		public void chunkReceived(String format, String sender, String uri, int era) {
//			System.out.println(getLogStart() + "chunk received - convert to asap message received");
//			try {
//				ASAPEngine engine = this.getMultiEngine().getEngineByFormat(format);
//				ASAPMessages messages = engine.getIncomingChunkStorage(sender).getASAPChunkCache(uri, era);
//				this.listener.asapMessagesReceived(messages);
//				System.out.println("************Inside listener*****");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//				System.out.println("**");
//			} catch (ASAPException | IOException e) {
//				System.out.println(getLogStart() + e.getLocalizedMessage());
//			}
//		}
//
//		public MultiASAPEngineFS getMultiEngine() {
//			return multiEngine;
//		}
//
//	}
	
	private String getLogStart() {
		return this.getClass().getSimpleName() + ": ";
	}	

	public MultiASAPEngineFS getMultiEngine() {
		return multiEngine;
	}

	public void setMultiEngine(MultiASAPEngineFS multiEngine) {
		this.multiEngine = multiEngine;
	}

	public CharSequence getName() {
		return name;
	}

	public void setName(CharSequence name) {
		this.name = name;
	}

	public CharSequence getFolder() {
		return folder;
	}

	public void setFolder(CharSequence folder) {
		this.folder = folder;
	}

	public Collection<CharSequence> getFormats() {
		return formats;
	}

	public void setFormats(Collection<CharSequence> formats) {
		this.formats = formats;
	}

	public BigInteger getPubKey() {
		return pubKey;
	}

	public void setPubKey(BigInteger pubKey) {
		this.pubKey = pubKey;
	}

	public static BigInteger getGenerator() {
		return generator;
	}

	public static String getHex() {
		return hex;
	}

	public static BigInteger getBase() {
		return base;
	}

	public static CharSequence getApp() {
		return APP;
	}

	public Collection<CharSequence> getRecipents() {
		return recipents;
	}

	public void setRecipents(Collection<CharSequence> recipents) {
		this.recipents = recipents;
	}

	public Map<CharSequence, ASAPMessageReceivedListener> getMessageReceivedListener() {
		return messageReceivedListener;
	}

	public void setMessageReceivedListener(Map<CharSequence, ASAPMessageReceivedListener> messageReceivedListener) {
		this.messageReceivedListener = messageReceivedListener;
	}

	public GKEMessage_Impl[] getMessages() {
		return messages;
	}

	public void setMessages(GKEMessage_Impl[] messages) {
		this.messages = messages;
	}
	
	private class MessageListenerWrapper implements ASAPChunkReceivedListener {
	    private final ASAPMessageReceivedListener listener;


	    public MessageListenerWrapper(ASAPMessageReceivedListener listener) throws ASAPException {
	        if(listener == null) throw new ASAPException("listener must not be null");
	        this.listener = listener;
	    }
		@Override
		public void chunkReceived(String format, String sender, String uri, int era) {
			
			 System.out.println(getLogStart() + "Chunk received - convert to gke message listener");
	         try {
	             ASAPEngine engine = multiEngine.getEngineByFormat(APP);
	             //will probably need to adjust to GKEMessage
	             ASAPMessages messages = engine.getIncomingChunkStorage(sender).getASAPChunkCache(uri, era);
	             this.listener.asapMessagesReceived(messages);
	         } catch (ASAPException | IOException e) {
	             System.out.println(getLogStart() + e.getLocalizedMessage());
	         }
	     }
	}
}


