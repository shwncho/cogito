package com.server.cogito.domain.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.domain.user.domain.KaKaoUser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.server.cogito.global.common.exception.auth.AuthErrorCode.KAKAO_LOGIN;


public class CreateKaKaoUser {

    public static KaKaoUser createKaKaoUserInfo(String token){

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result.toString());
            JsonObject kakao_account = (JsonObject) obj.get("kakao_account");
            JsonObject properties = (JsonObject) obj.get("properties");

            String email = kakao_account.get("email").getAsString();
            String nickname = properties.get("nickname").getAsString();


            System.out.println("email : " + email);
            System.out.println("nickname = " + nickname);

            br.close();

            return KaKaoUser.of(email,nickname);

        } catch(Exception e){
            throw new ApplicationException(KAKAO_LOGIN);
        }


    }

}
