package org.rostovpavel.webservice.TEMPO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;
import org.rostovpavel.base.models.Ticker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TestMind {
    public static void main(String[] args) {
        List<String> mmStart = Arrays.asList("14", "29", "45", "59");
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        MyTask testMytest = new MyTask("testMytest");

//        ForkJoinPool executor = ForkJoinPool.commonPool();
        //ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
//        test("test", executor);
        while (true) {
            String currentMM = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
            if (mmStart.contains(currentMM)) {
                System.out.println("Start -> " + new SimpleDateFormat("HH.mm.ss").format(Calendar.getInstance().getTime()));
                ScheduledFuture<?> schedule = executor.scheduleAtFixedRate(testMytest, 0, 900000, TimeUnit.MILLISECONDS);
                break;
            }
            try {
                System.out.println("Wait -> " + new SimpleDateFormat("HH.mm.ss").format(Calendar.getInstance().getTime()));
                Thread.sleep(40000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//       new SimpleDateFormat("MM").format(Calendar.getInstance().getTime());



//        return new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());


//        try {
//            Thread.sleep(5000);
//            //executor.isTerminated();
//           // List<Runnable> runnables = executor.shutdownNow();
//            schedule.cancel(true);
//
//        } catch (InterruptedException e) {
//
//        }


//        String url = "http://localhost:8888/v1/api/up";
//        String timeStamp = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
//        String fileName = "stock_" + timeStamp + "_.";
//
//
//        Gson gson = new Gson();
//        RestTemplate rest = new RestTemplate();
//        //write
//        //List<Ticker> stocks = rest.getForObject(url, TickersDTO.class).getStocks();
//        //writeToJson(fileName + "json", gson, stocks);
//        generateCSV(fileName, gson);
    }

    private static void generateCSV(String fileName, Gson gson) {
        Type listType = new TypeToken<ArrayList<Ticker>>() {}.getType();
        List<Ticker> current = null;
        try {
            current = gson.fromJson(new FileReader(fileName + "json"), listType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<String[]> dataToLines = new ArrayList<>();
        dataToLines.add(new String[]{"Name", "Price", "Move", "Val", "Trend", "Purchases", "Time"});
        current.forEach(e -> {
            dataToLines.add(new String[]{
                    e.getName(),
                    e.getPrice().toString(),
                    String.valueOf(e.getScoreMove()),
                    String.valueOf(e.getScorePowerVal()),
                    String.valueOf(e.getScorePowerTrend()),
                    String.valueOf(e.getScorePurchases()),
                    e.getTime()
            });
        });

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName + "csv"))) {
            writer.writeAll(dataToLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToJson(String fileName, Gson gson, List<Ticker> stocks) {
        Type listType = new TypeToken<ArrayList<Ticker>>() {
        }.getType();
        if (!stocks.isEmpty()) {
            try (FileReader fileReader = new FileReader(fileName)) {
                List<Ticker> current = gson.fromJson(fileReader, listType);
                fileReader.close();
                if (current != null) {
                    current.addAll(stocks);
                    try (FileWriter writer = new FileWriter(fileName)) {
                        gson.toJson(current, writer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                try (FileWriter writer = new FileWriter(fileName)) {
                    gson.toJson(stocks, writer);
                } catch (IOException x) {
                    x.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
