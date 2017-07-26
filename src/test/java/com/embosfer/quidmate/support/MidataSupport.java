package com.embosfer.quidmate.support;

import com.embosfer.quidmate.core.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Created by embosfer on 28/05/2017.
 */
public class MidataSupport {

    public static class MidataFile {

        // TODO review this global access later (handy for less verbose tests)
        public String header;
        public final String name;
        public Transaction[] transactions;
        public File outputFile;

        MidataFile(String name) {
            this.name = name;
            outputFile = new File(System.getProperty("java.io.tmpdir") + this.name);
            try {
                if (!outputFile.createNewFile()) {
                    outputFile.delete();
                    outputFile.createNewFile();
                }
                System.out.println("Filename stored in " + outputFile);
            } catch (IOException e) {
                throw new RuntimeException("Oh dear... couldn't create " + name);
            }
        }

        public MidataFile withHeader(String header) {
            this.header = header;
            return this;
        }

        public MidataFile withTransactions(Transaction... transactions) {
            this.transactions = transactions;
            createActualFileInSystem();
            return this;
        }

        private void createActualFileInSystem() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.write(this.header + "\n");
                for (Transaction transaction : this.transactions) {
                    writer.printf("%s;", transaction.date.format(ofPattern("dd/MM/yyyy")));
                    writer.printf("%s;", transaction.type);
                    writer.printf("%s;", transaction.description.value);
                    writer.printf("%s;", transaction.debitCredit);
                    writer.printf("%s;", transaction.balance);
                    writer.write("\n");
                }
            } catch (IOException ex) {
                throw new RuntimeException("Something wrong when creating file " + this.name);
            }
        }
    }

    public static MidataFile file(String filename) {
        return new MidataFile(filename);
    }
}
