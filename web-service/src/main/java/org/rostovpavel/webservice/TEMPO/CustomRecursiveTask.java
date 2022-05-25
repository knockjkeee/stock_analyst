package org.rostovpavel.webservice.TEMPO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.RecursiveAction;

public class CustomRecursiveTask extends RecursiveAction {

    private String name;

    public CustomRecursiveTask(String name) {
        this.name = name;
    }

    @Override
    protected void compute() {
        while (true) {
            System.out.println(processing());
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private String processing() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }


}
