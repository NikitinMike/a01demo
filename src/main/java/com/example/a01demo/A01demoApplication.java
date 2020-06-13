package com.example.a01demo;

import au.com.bytecode.opencsv.CSVReader;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class A01demoApplication {

    // root path
    private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    private static String pathCSV = TEMP_DIRECTORY.getAbsolutePath() + "/CSV"; // as "/tmp/CSV";

    // list length
    private static int listSize = 1000;
    // goods limit
    private static int limit = 20;
    // trace points every
    private static int tracePoint = 100000;

    // global ids counter
    private static Map<Integer, TreeSet<Products>> map = new HashMap<>();

    public static void main(String[] args) {

        // read sources data files
        for (String path : listFiles(pathCSV))
            try {
                readCSVDataFile(path,limit);
            } catch (IOException e) {
                System.out.println("File error" + path);
            }

        // collect results by ids
        TreeSet<Products> result = new TreeSet();
        map.forEach((key, data) -> result.addAll(data.stream().limit(limit).collect(Collectors.toList())));

        // limiting entries list
        List<Products> productsList = result.stream()
                .sorted(Comparator.comparing(Products::getPrice))
                .limit(listSize).sorted(Comparator.comparing(Products::getProductId))
                .collect(Collectors.toList());

        // ordering list
        productsList.sort(Comparator.comparing(Products::getPrice));

        // output to file
        try (PrintWriter pw = new PrintWriter(new File("data.csv"))) {
            productsList.forEach(p -> pw.println(p.csvString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("\nSaved " + productsList.size());
    }

    static void readCSVDataFile(String fileName,int limit) throws IOException {

        System.out.printf("\nRead [%s] ", fileName);
        CSVReader csvReader = new CSVReader(new FileReader(fileName));

        int n = 0;
        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            if (++n % tracePoint == 0) System.out.print(n / tracePoint % 10);
            Integer id = Integer.parseInt(nextLine[0]);
            Products product = new Products(id,
                    nextLine[1], nextLine[2], nextLine[3],
                    Float.parseFloat(nextLine[4])
            );
            if (!map.containsKey(id))
                map.put(id, new TreeSet<>());
            map.get(id).add(product);
            if (map.get(id).size() > limit)
                map.get(id).remove(map.get(id).last());
        }

    }

    public static Set<String> listFiles(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getAbsolutePath)
                .collect(Collectors.toSet());
    }

}
