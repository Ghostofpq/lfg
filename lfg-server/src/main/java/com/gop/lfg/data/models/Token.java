package com.gop.lfg.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "tokens")
public class Token {
    @Id
    private String id;
    @Indexed
    public String access_token;
    @Indexed
    public String token_refresh;
    public long issued_at;
    public long expires_at;
}
