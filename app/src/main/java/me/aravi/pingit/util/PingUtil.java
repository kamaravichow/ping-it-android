/*
 * Copyright (c) 2021. Aravind Chowdary (@kamaravichow)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  you may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package me.aravi.pingit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

public class PingUtil {

    private static final String TAG = PingUtil.class.getSimpleName();
    private static final String PING_FAIL_RESULT = "-1";
    private static ArrayList<String> ipPingResults = new ArrayList<>();


    //    Success result
    //    PING 150.xxx.xxx.27 (150.xxx.xxx.27) 56(84) bytes of data.
    //    64 bytes from 150.xxx.xxx.27: icmp_seq=1 ttl=47 time=66.5 ms
    //    --- 150.xxx.xxx.27 asyncPing statistics ---
    //    1 packets transmitted, 1 received, 0% packet loss, time 0ms
    //    rtt min/avg/max/mdev = 66.509/66.509/66.509/0.000 ms

    //    Fail
    //    PING 61.xxx.xxx.106 (61.xxx.xxx.106) 56(84) bytes of data.
    //    --- 61.xxx.xxx.106 asyncPing statistics ---
    //    100 packets transmitted, 0 received, 100% packet loss, time 99005ms

    //get ping result then process later

    public static void asyncGetPingResult(String url, final OnPingResultListener onPingResultListener) {
        String domain = getDomain(url);
        if (null == domain) {
            return;
        }
        asyncPing(createSimplePingCommand(1, 100, domain), new OnPingResultListener() {
            @Override
            public String onPingSuccess(String pingResult) {
                onPingResultListener.onPingSuccess(pingResult);
                return null;
            }

            @Override
            public String onPingFailure() {
                onPingResultListener.onPingFailure();
                return null;
            }
        });
    }

    public static int getMaxTTL(String pingResult) {
        ArrayList<Integer> ttlList = getPingDataListWithKey(pingResult, "ttl");
        if (ttlList.isEmpty()) {
            return Integer.valueOf(PING_FAIL_RESULT);
        } else {
            return Collections.max(ttlList);
        }
    }

    public static int getMinTTL(String pingResult) {
        ArrayList<Integer> ttlList = getPingDataListWithKey(pingResult, "ttl");
        if (ttlList.isEmpty()) {
            return Integer.valueOf(PING_FAIL_RESULT);
        } else {
            return Collections.min(ttlList);
        }
    }

    public static int getAveTTL(String pingResult) {
        ArrayList<Integer> ttlList = getPingDataListWithKey(pingResult, "ttl");
        if (ttlList.isEmpty()) {
            return Integer.valueOf(PING_FAIL_RESULT);
        } else {
            int totalTTL = 0;
            for (int ttl : ttlList) {
                totalTTL += Integer.valueOf(ttl);
            }
            return Integer.valueOf(totalTTL / ttlList.size());
        }
    }

    public static ArrayList<Integer> getPingDataListWithKey(String pingResult, String key) {
        String[] split = pingResult.split("\n");
        ArrayList<Integer> dataList = new ArrayList<>();
        for (String s : split) {
            if (s.contains(key)) {
                String keyString = s.substring(s.indexOf(key) + key.length() + 1, s.length());//key.length()+"="
                keyString = keyString.substring(0, keyString.indexOf(" "));
                dataList.add(Integer.valueOf(keyString));
            }
        }
        return dataList;
    }

    public static String getMaxElapseTime(String resultString) {
        if (null != resultString && resultString.length() != 0) {
            //Failed result does not contain this charsequence
            if (resultString.contains("min/avg/max/mdev")) {
                String tempInfo = resultString.substring(resultString.indexOf("min/avg/max/mdev") + 19);
                String[] temps = tempInfo.split("/");
                return String.valueOf(Float.valueOf(temps[2]));
            } else {
                return PING_FAIL_RESULT;
            }
        }
        return PING_FAIL_RESULT;
    }

    public static String getMinElapseTime(String resultString) {
        if (null != resultString && resultString.length() != 0) {
            if (resultString.contains("min/avg/max/mdev")) {
                String tempInfo = resultString.substring(resultString.indexOf("min/avg/max/mdev") + 19);
                String[] temps = tempInfo.split("/");
                return String.valueOf(Float.valueOf(temps[0]));
            } else {
                return PING_FAIL_RESULT;
            }
        }
        return PING_FAIL_RESULT;
    }

    public static String getAveElapseTime(String resultString) {
        if (null != resultString && resultString.length() != 0) {
            if (resultString.contains("min/avg/max/mdev")) {
                String tempInfo = resultString.substring(resultString.indexOf("min/avg/max/mdev") + 19);
                String[] temps = tempInfo.split("/");
                return String.valueOf(Float.valueOf(temps[1]));
            } else {
                return PING_FAIL_RESULT;
            }
        }
        return PING_FAIL_RESULT;
    }

    public static String getServerIP(String pingResult) {
        if (null != pingResult && pingResult.length() != 0) {
            String tempInfo = pingResult.substring(pingResult.indexOf("PING") + 5);
            String ip = tempInfo.substring(0, tempInfo.indexOf(" "));
            return ip;
        } else {
            return PING_FAIL_RESULT;
        }
    }

    public static String getPingTimes(String pingResult) {
        if (null != pingResult && pingResult.length() != 0) {
            String[] split = pingResult.split("\n");
            String info = "";
            for (String s : split) {
                if (s.contains("packets transmitted")) {
                    info = s;
                }
            }
            if (info.length() != 0) {
                info = info.substring(0, info.indexOf(" "));
                return info;
            } else {
                return PING_FAIL_RESULT;
            }

        } else {
            return PING_FAIL_RESULT;
        }
    }

    public static String getReceivedPackage(String pingResult) {
        if (null != pingResult && pingResult.length() != 0) {
            String[] split = pingResult.split("\n");
            String info = "";
            for (String s : split) {
                if (s.contains("packets transmitted")) {
                    info = s;
                }
            }
            if (info.length() != 0) {
                info = info.substring(info.indexOf(",") + 2, info.indexOf("received") - 1);
                return info;
            } else {
                return PING_FAIL_RESULT;
            }
        } else {
            return PING_FAIL_RESULT;
        }
    }

    public static String getPacketLoss(String pingResult) {
        if (null != pingResult) {
            try {
                String tempInfo = pingResult.substring(pingResult.indexOf("received,"));
                return tempInfo.substring(9, tempInfo.indexOf("packet"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return PING_FAIL_RESULT;
    }

    public static String getPacketLoss(String ip, int count, int timeout) {
        asyncPing(createSimplePingCommand(count, timeout, ip), new OnPingResultListener() {
            @Override
            public String onPingSuccess(String pingResult) {
                if (null != pingResult) {
                    try {
                        String tempInfo = pingResult.substring(pingResult.indexOf("received,"));
                        return tempInfo.substring(9, tempInfo.indexOf("packet"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return PING_FAIL_RESULT;
            }

            @Override
            public String onPingFailure() {
                return PING_FAIL_RESULT;
            }
        });
        return PING_FAIL_RESULT;
    }

    private static String getDomain(String url) {
        String domain = null;
        try {
            domain = URI.create(url).getHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return domain;
    }

    // No regex skills.
//    private static boolean isMatch(String regex, String string) {
//        return Pattern.matches(regex, string);
//    }

    private static void asyncPing(final String command, final OnPingResultListener onPingResultListenerListener) {
        //TODO Possible Memory Leakage
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process process = null;
                try {
                    process = Runtime.getRuntime().exec(command);
                    InputStream is = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while (null != (line = reader.readLine())) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    reader.close();
                    is.close();
                    onPingResultListenerListener.onPingSuccess(sb.toString());
                } catch (IOException e) {
                    onPingResultListenerListener.onPingFailure();
                    e.printStackTrace();
                } finally {
                    if (null != process) {
                        process.destroy();
                    }
                }
            }
        });
        thread.run();
    }

    private static String createSimplePingCommand(int count, int timeout, String domain) {
        return "/system/bin/ping -c " + count + " -w " + timeout + " " + domain;
    }

    public interface OnPingResultListener {

        String onPingSuccess(String pingResult);

        String onPingFailure();
    }
}

