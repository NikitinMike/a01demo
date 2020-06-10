package com.example.a01demo;

import au.com.bytecode.opencsv.CSVReader;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;

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
    private static int tracePoint = 10000;

    // global ids counter
    private static Map<Integer, TreeSet<Products>> map = new HashMap<>();

    public static void main(String[] args) {

        // creating CSV data sources (may skip)
        File tmpDir = new File(TEMP_DIRECTORY, "CSV");
        try {
            FileUtils.forceDelete(tmpDir);
        } catch (IOException e) {
            System.out.println("First Run");
        }
        tmpDir.mkdir();
        makeCSVFilesData(pathCSV, 9, 99999, 100, 99.99F);

        // read sources data files
        for (String path : listFiles(pathCSV))
            try {
                readCSVDataFile(path);
            } catch (IOException e) {
                System.out.println("File error"+path);
            }

        // collect results by ids
        TreeSet<Products> result = new TreeSet();
        map.forEach((key, data) -> result.addAll(data));

        // limiting entries list
        List<Products> productsList = result.stream()
                .sorted(Comparator.comparing(Products::getPrice))
                .limit(listSize).sorted(Comparator.comparing(Products::getProductId))
                .collect(Collectors.toList());

        // output to file
        try (PrintWriter pw = new PrintWriter(new File("data.csv"))) {
            productsList.forEach(p -> pw.println(p.csvString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("\nSaved " + productsList.size());
    }

    static void readCSVDataFile(String fileName) throws IOException {

        System.out.printf("\nRead [%s] ", fileName);
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    @SneakyThrows
    static void makeCSVFilesData(String path, int files, int rows, int maxId, float maxPrice) {

        Random random = new Random();

        String[] state = {"BLUE", "GREEN", "MAGENTA", "RED", "CYAN"};
        String[] cond = {"dog", "horse", "cat", "bird", "men"};

        System.out.println("Create DataFiles ");
        for (int j = 1; j <= files; j++) {
            File csvOutputFile = new File(String.format(path + "/data%04d.csv", j));
            System.out.print(j);
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                int n=0;
                for (int i = 1; i <= rows; i++) {
                    if (++n % tracePoint == 0) System.out.print(".");
                    pw.println(
                            new Products(random.nextInt(maxId) + maxId
                                    , String.format("%d", i)
                                    , state[random.nextInt(state.length)]
                                    , cond[random.nextInt(cond.length)]
                                    , random.nextFloat() * maxPrice
                            ).csvString()
                    );
                }
            }
        }
    }

    public static Set<String> listFiles(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getAbsolutePath)
                .collect(Collectors.toSet());
    }

}