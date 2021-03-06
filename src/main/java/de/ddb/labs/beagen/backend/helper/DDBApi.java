/* 
 * Copyright 2019, 2020 Michael Büchner, Deutsche Digitale Bibliothek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ddb.labs.beagen.backend.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author Michael Büchner
 */
public class DDBApi {

    public static int httpGet(final String urlStr, final String format, final File fileName) throws ConnectException, IOException {
        // HTTP Request Header
        final HashMap<String, String> properties = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put("Authorization", "OAuth oauth_consumer_key=\"" + Configuration.get().getValue("beagen.ddbapikey") + "\"");
                put("Accept", format);
            }
        };

        // open HTTP connection with URL
        final URL url = new URL(urlStr);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // set properties if any do exist
        properties.keySet().forEach((k) -> {
            conn.setRequestProperty(k, properties.get(k));
        });

        conn.connect();

        // test if request was successful (status 200)
        if (conn.getResponseCode() != 200) {
            throw new ConnectException("HTTP status code is " + conn.getResponseCode() + ". " + conn.getResponseMessage());
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    out.write(inputLine);
                    out.newLine();
                }
            }
        }

        return 200;
    }

    public static InputStream httpGet(final String urlStr, final String format) throws ConnectException, IOException {
        // HTTP Request Header
        final HashMap<String, String> properties = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put("Authorization", "OAuth oauth_consumer_key=\"" + Configuration.get().getValue("beagen.ddbapikey") + "\"");
                put("Accept", format);
            }
        };

        // open HTTP connection with URL
        final URL url = new URL(urlStr);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // set properties if any do exist
        properties.keySet().forEach((k) -> {
            conn.setRequestProperty(k, properties.get(k));
        });

        conn.connect();

        // test if request was successful (status 200)
        if (conn.getResponseCode() != 200) {
            throw new ConnectException("HTTP status code is " + conn.getResponseCode() + " while getting data from DDB-API.");
        }
        return conn.getInputStream();
    }
}
