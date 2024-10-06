package ch.unisg.db.config;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.util.NetworkInterfacesUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class DBServer extends CoapServer {

    public DBServer(boolean udp, boolean tcp, int port) {
        addEndpoint(true, false, port);
    }

    private void addEndpoint(boolean udp, boolean tcp, int port) {
        Configuration configuration = Configuration.getStandard();
        for (InetAddress addr : NetworkInterfacesUtil.getNetworkInterfaces()) {
            InetSocketAddress bindToAddress = new InetSocketAddress(addr, port);
            if (udp) {
                CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
                builder.setInetSocketAddress(bindToAddress);
                builder.setConfiguration(configuration);
                addEndpoint(builder.build());
            }
            if (tcp) {
                System.out.println("Not found");
            }
        }
    }
}
