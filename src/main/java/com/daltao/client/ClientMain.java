package com.daltao.client;

import com.daltao.channel.DeltaChannel;
import com.daltao.channel.SummaryChannel;
import com.daltao.config.Config;
import com.daltao.dto.SummaryClient;
import com.daltao.utils.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 * 1. echo hello first
 * 2. determine x and y for hash seed
 * 3. specify remote directory and send local summary
 * 4. download script and execute locally
 */
public class ClientMain {
    Config config = new Config();
    File root = new File(config.root).getCanonicalFile();

    public ClientMain() throws IOException {
        System.err.println("config file:");
        System.err.println(config);
    }

    public void run() throws Exception {
        root.mkdirs();
        if (root.getParentFile() == null) {
            throw new IllegalStateException("root can't be /");
        }
        File parent = root.getParentFile();
        String timestamp = DateUtils.yyyyMMddHHmmss(new Date());
        File tmpDir = new File(parent, "jsync_temp_" + timestamp);

        System.err.println("start jsync");
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(config.host, config.port));
        System.err.println("connection build successfully");
        DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        String hello = is.readUTF();
        if (!"hello".equals(hello)) {
            throw new IllegalStateException("Wrong password");
        }
        os.writeUTF("hi");
        os.writeUTF(Api.API_VERSION);
        os.flush();
        if (!Api.API_VERSION.equals(is.readUTF())) {
            throw new IllegalStateException("Different api version between server and client");
        }

        System.err.println("finish handshake");
        config.block = is.readInt();
        int p = is.readInt();
        int x = is.readInt();
        int y = is.readInt();

        RollingHashDeque dq = new RollingHashDeque(new RollingHash(new HashData(x, p, config.block)),
                new RollingHash(new HashData(y, p, config.block)), config.block);
        List<SummaryClient> summaryList = new CalculateLocalSummary(root, config.block, dq).call();
        os.writeUTF(config.remote);
        new SummaryChannel().write(os, (List) summaryList);
        os.flush();
        System.err.println("send summary");

        is = new DataInputStream(AESUtils.decrypt(config.pwd, new BufferedInputStream(socket.getInputStream())));
        DeltaChannel channel = DeltaChannel.of((List) summaryList, config.block, dq);
        FileCache fc = new FileCache(config.maxOpenedFile);
        channel.read(is, tmpDir, summaryList, fc);
        fc.clear();
        System.err.println("build local file successfully, total bytes " + channel.totalSize + ", actually transfer " + channel.actualTransfer);

        os.writeUTF("done");
        socket.close();

        //try to delete old file
        if(!root.renameTo(new File(parent, "jsync_delete_" + timestamp))){
            throw new IllegalStateException("Can't rename file " + root);
        }
        tmpDir.renameTo(new File(config.root));
        System.err.println("jsync done");

        //clean job
        if(config.delete) {
            System.err.println("start clean job, please wait");
            FileUtils.deepDelete(root);
            System.err.println("finish clean job");
        }
    }

    public static void main(String[] args) throws Exception {
        new ClientMain().run();
    }
}
