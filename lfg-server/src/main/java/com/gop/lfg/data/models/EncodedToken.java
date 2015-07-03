package com.gop.lfg.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EncodedToken {
   private String value;
}
