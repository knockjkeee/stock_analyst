package org.rostovpavel.webservice.TEMPO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.models.Ticker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class GenerateFile {
//    String url = "http://localhost:8888/v1/api/up";
//    RestTemplate rest = new RestTemplate();

    public static void generateCSV(String name) {
        Gson gson = new Gson();
        String timeStamp = getTimeStamp();
        String fileName = name + "_" + timeStamp + "_.";
        Type listType = getType();
        List<Ticker> current = null;
        try {
            current = gson.fromJson(new FileReader(fileName + "json"), listType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<String[]> dataToLines = new ArrayList<>();
        dataToLines.add(new String[]{"Name", "Price", "Move", "Val", "Trend", "Purchases", "Trend", "Time", "MA", "MACD", "AO", "BB"});
//        dataToLines.add(new String[]{"Name", "Price", "Move", "Val", "Trend", "Purchases", "Trend", "Time", "MA", "MACD", "AO", "BB", "SO"});
        if (current != null) {
            current.forEach(e -> {
                dataToLines.add(new String[]{
                        e.getName(),
                        e.getPrice().toString(),
                        String.valueOf(e.getScoreMove()),
                        String.valueOf(e.getScorePowerVal()),
                        String.valueOf(e.getScorePowerTrend()),
                        String.valueOf(e.getScorePurchases()),
                        e.getSuperTrend().get_keyMain() + "/" + e.getSuperTrend().get_keySecond(),
                        e.getTime(),
                        String.valueOf(e.getMovingAverage().getInnerScore()),
                        e.getMacd().get_key() + "/" + e.getMacd().getProcent() + "/" + e.getMacd().getScoreToKeys() + "/" + e.getMacd().getScoreToLine() + "/" + e.getMacd().getScoreToSignal(),
                        e.getAwesomeOscillator().get_key() + "/" + e.getAwesomeOscillator().getDirection() + "/" + e.getAwesomeOscillator().getScoreKey() + "/" + e.getAwesomeOscillator().getScoreLine() + "/" + e.getAwesomeOscillator().getScoreSignal(),
                        e.getBollingerBands().get_key() + "/" + e.getBollingerBands().getWbProcent() + "/" + e.getBollingerBands().getScoreToKeys() + "/" + e.getBollingerBands().getScoreToLine() + "/" + e.getBollingerBands().getScoreToSignal()
//                        e.getStochasticOscillator().get_key() + "/" + e.getStochasticOscillator().getProcent() + "/" + e.getStochasticOscillator().getScoreToKeys() + "/" + e.getStochasticOscillator().getScoreToLine() + "/" + e.getStochasticOscillator().getScoreToSignal(),
                });
            });
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName + "csv"))) {
            writer.writeAll(dataToLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToJson(@NotNull List<Ticker> stocks, String name) {
        Gson gson = new Gson();
        String timeStamp = getTimeStamp();
        String fileName = name + "_" + timeStamp + "_.json";
        Type listType = getType();
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

    @NotNull
    private static String getTimeStamp() {
        return new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
//        return "27.05.2022";
    }

    private static Type getType() {
        return new TypeToken<ArrayList<Ticker>>() {
        }.getType();
    }
}

