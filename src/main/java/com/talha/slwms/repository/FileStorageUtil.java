package com.talha.slwms.repository;

import com.talha.slwms.model.Customer;

import java.io.*;
import java.nio.file.*;
import java.util.*;
public class FileStorageUtil {

    private FileStorageUtil() {}

    // --- Plain text CSV write, using NIO ---
    public static void writeCustomersToCsv(List<Customer> customers, String filePath) {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,email,phone");
        for (Customer c : customers) {
            lines.add(c.getCustomerId() + "," + c.getName() + "," + c.getEmail() + "," + c.getPhone());
        }
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, lines);
        } catch (IOException e) {
            throw new RuntimeException("Failed writing CSV: " + e.getMessage(), e);
        }
    }

    // --- Reading back with BufferedReader ---
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed reading file: " + e.getMessage(), e);
        }
        return lines;
    }

    // --- Serialization: save/load a whole object graph in one shot ---
    public static void saveObject(Object obj, String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed: " + e.getMessage(), e);
        }
    }

    public static Object loadObject(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization failed: " + e.getMessage(), e);
        }
    }
}