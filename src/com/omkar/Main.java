package com.omkar;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("|------EzShare v1.0------|");
        String choice=askTransmissionType();

        if(choice.equals("r")){
            SocketRecv skr;
            try{
                skr=new SocketRecv();
                String[] address=skr.getHostAddress();
                System.out.println("Enter the destination directory path: (default is /Downloads/, enter -d)");
                Scanner sc= new Scanner(System.in);
                String path=sc.nextLine();
                if(!path.equals("-d")){
                    path=sc.nextLine();
                    while( !new File(path).exists() || !new File(path).isDirectory()){
                        System.out.println("Enter a valid directory!");
                        path=sc.nextLine();
                    }
                }
                else path=Paths.get(System.getProperty("user.home"),"Downloads").toString();
                System.out.println("IP: "+address[0]);
                System.out.println("Port: "+address[1]);
                skr.accept();
                System.out.println("Connection established");
                String filename=skr.getDataInputStream().readUTF();
                String fileSize=skr.getDataInputStream().readUTF();
                System.out.println("Receiving: "+filename+" | "+fileSize+" MB");
                File file=new File(path,filename);
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                int bytesReceived;
                long totalBytesReceived=0;
                byte[] bytes=new byte[1000000];
                DataInputStream dataInputStream=skr.getDataInputStream();
                System.out.print("Progress: 0%");
                int numDigits=1;    //used to track the num of digits in progress counter(required for knowing number of backspaces to be printed in console)
                while(true){
                    bytesReceived=dataInputStream.read(bytes);
                    if(bytesReceived==-1) break;
                    else if(bytesReceived!=1000000){
                        fileOutputStream.write(Arrays.copyOf(bytes,bytesReceived));
                        fileOutputStream.flush();
                    }
                    else{
                        fileOutputStream.write(bytes);
                    }
                    totalBytesReceived+=bytesReceived;
                    numDigits=printProgress((float)totalBytesReceived,Float.parseFloat(fileSize)*1048576,numDigits);
                }
                printProgress(1,1,numDigits);
                System.out.print("\n");
                fileOutputStream.close();
                skr.close();
                System.out.println("File saved at "+path);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        else{
            SocketSend sks;
            Scanner sc=new Scanner(System.in);
            System.out.println("Enter file path: ");
            String path=sc.nextLine();
            File file=new File(path);
            while(!file.isFile() || !file.exists()){
                System.out.println("Please enter a valid file path:");
                path=sc.nextLine();
                file=new File(path);
            }
            System.out.println("Enter receiver's IP:");
            String ip=sc.nextLine();
            System.out.println("Enter receiver's port:");
            int port=Integer.parseInt(sc.nextLine());
            try{
                sks=new SocketSend(ip,port);
                FileInputStream fileInputStream=new FileInputStream(file);
                sks.getDataOutputStream().writeUTF(file.getName());
                sks.getDataOutputStream().writeUTF(String.valueOf((float)file.length()/1048576));
                byte[] bytes=new byte[1000000];
                DataOutputStream dataOutputStream=sks.getDataOutputStream();
                int bytesRead;
                long bytesSent=0;
                long totalBytes=file.length();
                System.out.println("Sending file...");
                System.out.print("Progress: 0%");
                int numDigits=1;
                while(true){
                    bytesRead=fileInputStream.read(bytes);
                    if(bytesRead==-1) break;
                    else if(bytesRead!=1000000){
                        dataOutputStream.write(Arrays.copyOf(bytes,bytesRead));
                        dataOutputStream.flush();
                    }
                    else {
                        dataOutputStream.write(bytes);
                        dataOutputStream.flush();
                    }
                    bytesSent+=bytesRead;
                    numDigits=printProgress((float)bytesSent,(float) totalBytes,numDigits);
                }
                printProgress((float)totalBytes,(float) totalBytes,numDigits);
                System.out.print("\n");
                sks.close();
                System.out.println("File sent.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Press any key to exit");
        Scanner sc=new Scanner(System.in);
        sc.nextLine().isEmpty();
    }
    public static String askTransmissionType(){
        Scanner sc= new Scanner(System.in);
        System.out.println("Choose transmission type: Send/Receive (S/R)");
        String choice=sc.nextLine();
        while(!(choice.equalsIgnoreCase("s") || choice.equalsIgnoreCase("r"))){
            System.out.println("Please enter valid option: S/R");
            choice=sc.nextLine();
        }
        return choice;
    }
    public static int printProgress(float done,float total,int numDigits){
        if(numDigits==1){
            System.out.print("\b\b");   //e.g. 'Progress: 1%' becomes 'Progress: '
        }
        else if(numDigits==2){
            System.out.print("\b\b\b");     //e.g. 'Progress: 10%' becomes 'Progress: '
        }
        else{
            System.out.print("\b\b\b\b");
        }
        String percent=String.valueOf((int)((done/total)*100));
        numDigits=percent.length();
        System.out.print(percent+"%");
        return numDigits;
    }
}
