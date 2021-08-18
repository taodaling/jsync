package com.daltao.server;

import com.daltao.channel.DeltaChannel;
import com.daltao.channel.SummaryChannel;
import com.daltao.config.Config;
import com.daltao.dto.Summary;
import com.daltao.utils.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ServerMain {
    Config config = new Config();
    File root = new File(config.root).getCanonicalFile();

    public ServerMain() throws IOException {
        System.err.println("config file:");
        System.err.println(config);
    }


    public void handleSocket(Socket socket) throws Exception {
        DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        //first step send hello and get hi as response
        os.writeUTF("hello");
        os.flush();
        if (!"hi".equals(is.readUTF())) {
            throw new IllegalStateException("Wrong password from " + socket.getInetAddress());
        }
        os.writeUTF(Api.API_VERSION);
        os.flush();
        if (!Api.API_VERSION.equals(is.readUTF())) {
            throw new IllegalStateException("Different api version between server and client");
        }

        int p = Constant.PRIME;
        int x = ThreadLocalRandom.current().nextInt(p - 1) + 1;
        int y = ThreadLocalRandom.current().nextInt(p - 1) + 1;
//        long x = BigInteger.probablePrime(60, ThreadLocalRandom.current()).longValue();
        os.writeInt(config.block);
//        os.writeLong(x);
        os.writeInt(p);
        os.writeInt(x);
        os.writeInt(y);
        os.flush();

        HashDeque dq = new RollingHashDeque(new RollingHash(new HashData(x, p, config.block)),
                new RollingHash(new HashData(y, p, config.block)), config.block);
//        HashDeque dq = new FastRollingHashDeque(new FastRollingHash(new FastHashData(x, config.block)), config.block);
        String remote = is.readUTF();
        File directory = new File(config.root + remote).getCanonicalFile();
        if (!directory.exists() || !directory.isDirectory() || !FileUtils.under(root, directory)) {
            throw new IllegalStateException("invalid path[" + directory.getPath() + "]");
        }

        List<Summary> summaryList = new SummaryChannel().read(is);
        os = new DataOutputStream(AESUtils.encrypt(config.pwd, new BufferedOutputStream(socket.getOutputStream())));
        DeltaChannel.of(summaryList, config.block, dq).write(os, directory);
        os.flush();
//        os.close();

        if (!"done".equals(is.readUTF())) {
            throw new IllegalStateException("jsync failed because of unknown reason");
        }
        System.err.println("jsync successfully with " + socket.getInetAddress());
    }

    public void run() throws IOException {
        ServerSocket socket = new ServerSocket();
        socket.bind(new InetSocketAddress(config.port));
        System.err.println("Server listening at port " + config.port);
        while (true) {
            Socket target = socket.accept();
            target.setKeepAlive(true);
            target.setSoTimeout(Integer.MAX_VALUE);
            new Thread(() -> {
                try {
                    handleSocket(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                } finally {
                    try {
                        target.close();
                    } catch (IOException e) {
                    }
                }
            }).start();
        }
    }

    public static void main(String[] args) throws IOException {
        new ServerMain().run();
    }
}
