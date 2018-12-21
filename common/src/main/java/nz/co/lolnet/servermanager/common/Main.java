/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.servermanager.common;

import redis.clients.jedis.Jedis;

import java.io.StringReader;
import java.util.Properties;

public class Main {
    
    // TODO REMOVE ME
    public static void main(String[] args) {
        try {
            String data = "id=3 addr=127.0.0.1:47878 fd=8 name= age=14 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client";
            
            Properties properties = new Properties();
            properties.load(new StringReader(String.join("\n", data.split(" "))));
            System.out.println(properties.getProperty("id"));
            System.out.println(properties.getProperty("addr"));
            System.out.println(properties.getProperty("name"));
            System.out.println(properties.getProperty("cmd"));
            Jedis t;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}