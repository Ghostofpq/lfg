package com.gop.lfg.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {
    private String login;
    private String password;
    private String nickname;
}
