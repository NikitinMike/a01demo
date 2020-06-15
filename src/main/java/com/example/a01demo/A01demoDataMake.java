package com.example.a01demo;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//@SpringBootApplication
public class A01demoDataMake {

    // root path
    private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    private static String pathCSV = TEMP_DIRECTORY.getAbsolutePath() + "/CSV"; // as "/tmp/CSV";

    // trace points every
    private static int tracePoint = 100000;

    public static void main(String[] args) {

        // creating CSV data sources (may skip)
        System.out.println("\nStarted " + new Date());
        File tmpDir = new File(TEMP_DIRECTORY, "CSV");
        try {
            FileUtils.forceDelete(tmpDir);
        } catch (IOException e) {
            System.out.println("First Run");
        }
        tmpDir.mkdir();


        makeCSVFilesData(pathCSV, 99, 999999, 100, 99.99F);


        System.out.println("\nFinished " + new Date());
    }

    @SneakyThrows
    static void makeCSVFilesData(String path, int files, int rows, int maxId, float maxPrice) {

        Random random = new Random();

        String[] state = {"BLUE", "GREEN", "MAGENTA", "RED", "CYAN"};
        String[] cond = {"dog", "horse", "cat", "bird", "men"};

        System.out.println("Create DataFiles ");
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int j = 1; j <= files; j++) {
            int finalJ = j;
            service.execute(() -> {
                try  {
                    File csvOutputFile = new File(String.format(path + "/data%04d.csv", finalJ));
                    System.out.print(finalJ+" ");
                    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                        int n = 0;
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        service.shutdown();
        // Ждем завершения выполнения потоков не более 10 минут.
        try {
            service.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
