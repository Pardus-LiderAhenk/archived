package test.liderahenk.xmpp;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class ShutdownServer implements IoHandler{
	private IoConnector connector;
	private IoSession session;
	private boolean received = false;
	
	public ShutdownServer() {
		try {
			connector = new NioSocketConnector();
			connector.getSessionConfig().setReadBufferSize(2048);

			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
			connector.setHandler(this);
			SocketSessionConfig dcfg = (SocketSessionConfig) connector.getSessionConfig();
			ConnectFuture connFuture = connector.connect(new InetSocketAddress("localhost", ServerMain.SHUTDOWN_PORT));
			connFuture.awaitUninterruptibly();
			session = connFuture.getSession();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ShutdownServer s =  new ShutdownServer();
		
		System.out.println(s.session);
		try {
			if (s.session != null){
			WriteFuture future = s.session.write("quit");
			s.session.close(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(0);
		
	}
	
	public static void sendShutdownSignal(){
		
	}
	
//	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("ShutdownServer.messageSent()");
	}
	
//	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println("ShutdownServer.messageReceived()");
	}

//	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("ShutdownServer.sessionCreated()");
		
	}

//	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("ShutdownServer.sessionOpened()");
	}

//	@Override
	public void sessionClosed(IoSession session) throws Exception {
	   System.out.println("ShutdownServer.sessionClosed()");
		
	}

//	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		System.out.println("ShutdownServer.sessionIdle()");
		
	}

//	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		System.out.println("ShutdownServer.exceptionCaught()");
		
	}

}
