package ch.unisg.omi.config;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.tcp.netty.TcpServerConnector;
import org.eclipse.californium.elements.util.NetworkInterfacesUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class CoapServerConfig extends CoapServer {

    static {
        CoapConfig.register();
        UdpConfig.register();
        TcpConfig.register();
    }

    private static final CoapServerConfig server = new CoapServerConfig(true, false, 5686);

    public CoapServerConfig(boolean udp, boolean tcp, int port) {
        addEndpoint(true, false, port);
    }

    public static CoapServerConfig getInstance() {
        return server;
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
                TcpServerConnector connector = new TcpServerConnector(bindToAddress, configuration);
                CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
                builder.setConnector(connector);
                builder.setConfiguration(configuration);
                addEndpoint(builder.build());
            }
        }
    }

}
