package com.sample.MovEmt.emtApi;

public class Authentication {
    public static String email;
    public static String password;
    public static String accessToken;

    public static void setAccessTokenFromJson(String json){
        String[] lines = json.split(",");
        String token = "";

        for(String line : lines)
            if(line.contains("accessToken")){
                token = line;
                break;
            }

        if(token.equals(""))
            return;

        lines = token.split(": ");
        accessToken = lines[1].replace("\"", "");
    }
}
