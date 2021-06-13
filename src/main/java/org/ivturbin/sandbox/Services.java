package org.ivturbin.sandbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class Services {
    ArrayList<Integer> ports = new ArrayList<>();

    final Logger logger = LogManager.getLogger(Services.class.getName());
    Services() {
        logger.info("Getting services");
        try {
            File file = new File("C://Windows//System32//drivers//etc//services");

            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                if (line.isEmpty() || !Character.isLetterOrDigit(line.charAt(0))){
                    line = reader.readLine();
                    continue;
                }

                int i = 15;
                while (!Character.isDigit(line.charAt(i))) {
                    i++;
                }
                ports.add(Integer.parseInt(line.substring(i).split("/")[0]));

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Getting services error");
        }
    }
}
