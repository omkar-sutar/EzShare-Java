package com.omkar;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketRecv {
    private ServerSocket ssk=null;
    private Socket sk;
    public SocketRecv() throws IOException {
        ssk= new ServerSocket(9999);
    }

    public String[] getHostAddress() throws UnknownHostException {
        String[] res=new String[2];
        res[0]=Inet4Address.getLocalHost().toString();
        res[1]=String.valueOf(ssk.getLocalPort());
        return res;
    }
    public void accept() throws IOException {
        sk=ssk.accept();
    }
    public DataInputStream getDataInputStream() throws IOException {
        return new DataInputStream(sk.getInputStream());
    }
    public void close() throws IOException {
        sk.close();
    }
}
