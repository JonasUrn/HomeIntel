package com.UserAgent;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class UserAgentGenerator {
    private int quantity;
    private int[] randomNumbers;

    public UserAgentGenerator(int quantityOfAgents) {
        this.quantity = quantityOfAgents;

        randomNumbers = new int[quantityOfAgents];
        for (int i = 0; i < quantityOfAgents; i++) {
            Random rand = new Random();
            randomNumbers[i] = rand.nextInt(0, 99); // 100 rows in a file
        }
    }

    public String getUserAgent() {
        String returnedLine = "";
        try {
            int iterator = 0;
            File file = new File(
                    "C:\\Users\\jonas\\Desktop\\4 kursas\\Programavimo Inzinerija\\HomeIntel\\backend\\src\\main\\java\\com\\UserAgent\\userAgentData.csv");

            Scanner scanner = new Scanner(file);
            while (iterator < randomNumbers[0]) {
                scanner.nextLine();
                iterator++;
            }
            returnedLine = scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnedLine;
    }

    public String[] getUserAgents() {
        String[] finalAgents = new String[randomNumbers.length];
        for (int i = 0; i < randomNumbers.length; i++) {
            finalAgents[i] = getUserAgent();
        }
        return finalAgents;
    }
}
