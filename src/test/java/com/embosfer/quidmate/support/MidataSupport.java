package com.embosfer.quidmate.support;

import com.embosfer.quidmate.core.model.Transaction;

import java.io.*;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
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
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), ISO_8859_1));
                writer.write(this.header + "\n");
                writeTransactions(writer);
                writeTrailer(writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Something wrong when creating file " + this.name);
            }
        }

        private void writeTrailer(BufferedWriter writer) throws IOException {
            writer.newLine();
            writer.append("Arranged overdraft limit;").append("22/05/2017").append(";").append("+£").append("500.00").append(";");
        }

        private void writeTransactions(BufferedWriter writer) throws IOException {
            for (Transaction transaction : this.transactions) {
                writer.append(transaction.date.format(ofPattern("dd/MM/yyyy"))).append(";")
                        .append(transaction.type.toString()).append(";")
                        .append(transaction.description.value).append(";")
                        .append(transaction.debitCredit.toString()).append(";")
                        .append(transaction.balance.toString()).append(";");
                writer.newLine();
            }
        }
    }

    public static MidataFile file(String filename) {
        return new MidataFile(filename);
    }
}
