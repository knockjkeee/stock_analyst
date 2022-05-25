package org.rostovpavel.webservice.TEMPO;

import lombok.Data;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Ticker;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Data
public class MyTask implements Runnable{
    private String name;
    RestTemplate rest = new RestTemplate();
    String url = "http://localhost:8888/v1/api/up";

    public MyTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        List<Ticker> stocks = rest.getForObject(url, TickersDTO.class).getStocks();
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + " Stocks size: "+ stocks.size());
    }
}
