package com.gop.lfg.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.time.DateTime;

/**
 * Created by VMPX4526 on 26/03/2015.
 */
@Data
public class ErrorMessage {
    private String message;
    private long date;

    public ErrorMessage(String message){
        this.message=message;
        this.date = DateTime.now().getMillis();
    }
}
