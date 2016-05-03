/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.surl;

import org.json.JSONObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;


@SuppressWarnings("serial")
public class ShortUrlRedirectServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ShortUrlRedirectServlet.class.getName());
    public static final String BASE_URL = "https://surl-1297.appspot.com";
    public static final String EXPAND_BASE_URL = "https://v1-dot-sampleendpoint-1280.appspot.com/_ah/api/urlshortner/v1/expandUrl/";


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = null;
        String shortUrl;
        URL url;
        HttpURLConnection conn;
        String line;
        StringBuffer response;
        BufferedReader reader;
        JSONObject jo;
        String longUrl;
        int code;
        try {
            out = resp.getWriter();
            shortUrl = BASE_URL + req.getRequestURI();
            shortUrl = URLEncoder.encode(shortUrl, "UTF-8");
            log.info("short url:" + shortUrl);
            url = new URL(EXPAND_BASE_URL + shortUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            response = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            jo = new JSONObject(response.toString());
            if (jo.getBoolean("success") == false) {
                out.print("URL can not be expanded for the short url");
            }
            longUrl = jo.getString("longUrl");
            log.info("LongURL :" + longUrl);
            if (!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
                longUrl = "http://" + longUrl;
            }
            url = new URL(longUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            code = conn.getResponseCode();
            conn.disconnect();
            log.info("Code :" + code);
            if (code == HttpURLConnection.HTTP_OK) {
                resp.sendRedirect(longUrl);
            } else {
                resp.sendRedirect(longUrl);
            }
        } catch (Exception e) {
            out.println("Exception :" + e.getMessage());
            //resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            //resp.setHeader("Location", longurl);
        }
    }
}
