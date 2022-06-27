package sgm.speechrecognition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    private InetSocketAddress address = new InetSocketAddress("1.1.0.2", 11117);
		
	private SocketChannel client;

    @Before
    public void initClient() throws IOException{
        client = SocketChannel.open(address);
    }

    @Test
    public void emptySend() throws IOException
    {
        byte[] message = new byte[]{0x01, 0x02, 0x01, 0x02};
        client.write(ByteBuffer.wrap(message));

        ByteBuffer buffer = ByteBuffer.allocate(256);
        int readByte = client.read(buffer);
        assertEquals(10, readByte);
    }

    @After
    public void closeServer() throws IOException{
        if(client != null)
            client.close();
    }
}
