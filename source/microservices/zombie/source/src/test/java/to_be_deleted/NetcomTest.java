package to_be_deleted;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.ConnectStatus;
import com.yngvark.gridwalls.netcom.Connected;
import com.yngvark.gridwalls.netcom.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.netcom.RetryConnecter;
import com.yngvark.gridwalls.netcom.RpcCaller;
import com.yngvark.gridwalls.netcom.RpcResult;
import com.yngvark.gridwalls.netcom.RpcSucceeded;
import com.yngvark.gridwalls.netcom.rabbitmq.BrokerConnecter;
import org.junit.jupiter.api.Test;

public class NetcomTest {
    @Test
    public void checktype() {
        RetryConnecter retryConnecter = new RetryConnecter(
                Config.builder().build(),
                new MyBrokerConnecter()
        );

        Netcom netcom = new Netcom(
                retryConnecter,
                new MyRpcCaller()
        );


        RpcResult arne = netcom.rpcCall("test", "arne");
    }

    class MyCon {
        public void disconnect() { }
    }

    class MyBrokerConnecter implements BrokerConnecter {

        @Override
        public ConnectStatus connect(String host, int timeoutMilliseconds) {
            return new Connected(
                new MyConnectionWrapper()
            );
        }
    }

    class MyConnectionWrapper implements ConnectionWrapper {
        private MyCon con = new MyCon();

        public MyCon getConnection() {
            return con;
        }

        @Override
        public void disconnectIfConnected() {
            con.disconnect();
        }
    }

    class MyRpcCaller implements RpcCaller<MyConnectionWrapper> {
        @Override
        public RpcResult rpcCall(MyConnectionWrapper connectionWrapper, String rpcQueueName, String message) {
            MyCon connection = connectionWrapper.getConnection();
            return new RpcSucceeded("MY RPC RESPONSE");
        }
    }

}