package com.daltao.channel;

import com.daltao.dto.Summary;
import com.daltao.dto.SummaryServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SummaryChannel implements Channel<List<Summary>> {
    @Override
    public void write(DataOutputStream dos, List<Summary> data) throws Exception {
        dos.writeInt(data.size());
        for (Summary s : data) {
            dos.writeLong(s.hashV);
            dos.write(s.md5);
        }
    }

    @Override
    public List<Summary> read(DataInputStream dis) throws Exception {
        int n = dis.readInt();
        List<Summary> ans = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            SummaryServer s = new SummaryServer();
            s.id = i;
            s.hashV = dis.readLong();
            s.md5 = dis.readNBytes(16);
            ans.add(s);
        }
        return ans;
    }
}
