package org.rostovpavel.webservice.TEMPO;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Ticker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Data
public class MyTask implements Runnable {
    private String name;
    RestTemplate rest = new RestTemplate();
    String url = "http://localhost:8888/v1/api/up";

    public MyTask(String name) {
        this.name = name;
    }

    public RestTemplate customRestTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        return new RestTemplate(factory);
    }

    @Override
    public void run() {
        List<Ticker> stocks = null;
        //RestTemplate rest = customRestTemplate();
        //List<Ticker> stocks = rest.getForObject(url, TickersDTO.class).getStocks();
        //System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + " Stocks size: "+ stocks.size());
        while (true) {

            String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            try {
                HttpStatus statusCode = getHttpStatus();
                if (statusCode == null) break;
                System.out.println(time + " status code: " + statusCode + "another attempt after wait 4 sec [try]");
                sleep(4000);
            } catch (RestClientException e) {
                System.out.println(time + " - RestClientException: " + e.getLocalizedMessage());
                sleep(4000);
                HttpStatus statusCode = null;
                try {
                    statusCode = getHttpStatus();
                    if (statusCode == null) break;
                    System.out.println(time + " status code: " + statusCode + "another attempt after wait 4 sec [catch]");
                    sleep(4000);
                } catch (Exception ex) {
                    break;
                }
            }
        }
    }

    @Nullable
    private HttpStatus getHttpStatus() {
        List<Ticker> stocks;
        ResponseEntity<TickersDTO> entity = rest.getForEntity(url, TickersDTO.class);
        HttpStatus statusCode = entity.getStatusCode();
        if (statusCode.value() == 200) {
            stocks = entity.getBody().getStocks();
            String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            System.out.println(time + " Stocks size: " + stocks.size());
            return null;
        }
        return statusCode;
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
