package com.omkar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketSend {
    private Socket sk=null;
    public SocketSend(String hostIp,int port) throws IOException {
        sk=new Socket(hostIp,port);
    }
    public DataOutputStream getDataOutputStream() throws IOException {
        return new DataOutputStream(sk.getOutputStream());
    }
    public void close() throws IOException {
        sk.close();
    }
}
