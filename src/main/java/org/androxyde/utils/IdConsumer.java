package org.androxyde.utils;

import org.androxyde.oracle.home.Homes;
import org.androxyde.oracle.process.OracleProcess;
import org.buildobjects.process.StreamConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IdConsumer  implements StreamConsumer {

    UserInfos infos;

    public IdConsumer(UserInfos infos) {
        this.infos=infos;
    }

    @Override
    public void consume(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] splitted = line.split("\\s+");
            for (String item:splitted) {
                if (item.startsWith("uid")) {
                    String user = item.replaceAll("uid=","").replaceAll("[()]", ":");
                    infos.setId(Long.parseLong(user.split(":")[0]));
                    infos.setName(user.split(":")[1]);
                }
                else if (item.startsWith("gid")) {
                    String group = item.replaceAll("gid=","").replaceAll("[()]", ":");
                    GroupInfos g = GroupInfos.builder().id(Long.parseLong(group.split(":")[0])).name(group.split(":")[1]).build();
                    infos.setPrimaryGroup(g);
                }
                if (item.startsWith("groups")) {
                    String[] groups = item.replaceAll("groups=","").replaceAll("[()]", ":").split(",");
                    for (String group:groups) {
                        GroupInfos g = GroupInfos.builder().id(Long.parseLong(group.split(":")[0])).name(group.split(":")[1]).build();
                        infos.getGroups().add(g);
                    }
                }
            }
        }
    }
}