package sgm.speechrecognition.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SpeechRecognitionServer extends Thread{

    private InetAddress address;
    private int port = 11117;
    private boolean BLOCKING = false;

    public SpeechRecognitionServer(int port) throws IOException{
        this.port = port;
        this.address = InetAddress.getLocalHost();
    }
    public SpeechRecognitionServer() throws IOException{
        this.address = InetAddress.getLocalHost();
    }

    @Override
    public synchronized void start() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        super.start();
    }

    public void init() throws IOException{
        System.setProperty("java.net.preferIPv6Addresses","true");

        Selector selector = Selector.open();

        ServerSocketChannel socketChannel = ServerSocketChannel.open();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);

        socketChannel.bind(inetSocketAddress);
        socketChannel.configureBlocking(BLOCKING);

        int ops = socketChannel.validOps();

        SelectionKey selectionKey = socketChannel.register(selector, ops, null);

        while(true){
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = selectionKeys.iterator();

            while(keysIterator.hasNext()){
                SelectionKey key = keysIterator.next();
                keysIterator.remove();

                if (!key.isValid()){
                    continue;
                }
                if (key.isAcceptable()) {
					SocketChannel client = socketChannel.accept();
 
					client.configureBlocking(false);
 
					client.register(selector, SelectionKey.OP_READ);
					System.out.println("Connection Accepted: " + client.getRemoteAddress());
 
				}
                if (key.isReadable()) {
					
					SocketChannel client = (SocketChannel) key.channel();
					
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int receivedByte = client.read(buffer);

                    if(receivedByte == -1){
                        closingConnection(client);
                        continue;
                    }
                    
					System.out.println("Message received byte: " + receivedByte);
                    
                    if(isConnectionClosingFlag(buffer.array())){
                        closingConnection(client);
                        continue;
                    }

                    if(client.isConnected())
                        client.register(selector, SelectionKey.OP_WRITE);
 
				}
                if (key.isWritable()) {
					
					SocketChannel client = (SocketChannel) key.channel();

                    ByteBuffer buffer = ByteBuffer.wrap(new byte[10]);
                    client.write(buffer);
                    
                    if(client.isConnected())
                        client.register(selector, SelectionKey.OP_READ);
				}
            }
        }
    }

    private void closingConnection(SocketChannel client) throws IOException{
        SocketAddress addr = client.getRemoteAddress();
        client.close();
        System.out.println("Connection Closed: "+addr.toString());
    }

    //If 1-2-3-4 bytes 0x00. Connections closed.
    private boolean isConnectionClosingFlag(byte[] array){

        if(array.length < 16)
            return true;

        if(array[0] == 0x00 && array[1] == 0x00 && array[2] == 0x00 && array[3] == 0x00)
            return true;

        return false;
    }
}
