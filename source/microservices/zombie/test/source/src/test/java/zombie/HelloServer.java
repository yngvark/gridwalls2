package zombie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.StringRpcServer;

public class HelloServer {
    public static void main(String[] args) {
        try {
            String hostName = (args.length > 0) ? args[0] : "rabbithost";
            int portNumber = (args.length > 1) ? Integer.parseInt(args[1]) : AMQP.PROTOCOL.PORT;

            ConnectionFactory connFactory = new ConnectionFactory();
            connFactory.setHost(hostName);
            connFactory.setPort(portNumber);
            Connection conn = connFactory.newConnection();
            final Channel ch = conn.createChannel();

            ch.queueDeclare("Hello", false, false, false, null);
            StringRpcServer server = new StringRpcServer(ch, "Hello") {
                public String handleStringCall(String request) {
                    System.out.println("Got request: " + request);
                    return "Hello, " + request + "!";
                }
            };
            server.mainloop();
        } catch (Exception ex) {
            System.err.println("Main thread caught exception: " + ex);
            ex.printStackTrace();
            System.exit(1);
        }
    }
}