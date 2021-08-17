package com.daltao.channel;

import com.daltao.dto.Directory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStructureChannel {
    public static void server(DataOutputStream dos, List<Directory> dirs) throws IOException {
        dos.writeInt(dirs.size());
        for (Directory d : dirs) {
            dos.writeInt(d.parentId);
            dos.writeUTF(d.name);
        }
    }

    public static List<Directory> client(DataInputStream dis) throws IOException {
        int n = dis.readInt();
        List<Directory> ans = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Directory d = new Directory();
            d.parentId = dis.readInt();
            d.name = dis.readUTF();
            ans.add(d);
        }
        return ans;
    }
}
