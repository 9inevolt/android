package gg.destiny.app.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public final class KrakenApi
{
    public static final int CONNECT_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 5000;

    public static final JSONObject getChannel(String channel) throws IOException, JSONException
    {
        String url = String.format("https://api.twitch.tv/kraken/channels/%s",
                Uri.encode(channel));

        String obj = getString(url);

        if (obj == null)
            return null;

        return new JSONObject(obj);
    }

    public static final JSONObject getStream(String channel) throws IOException, JSONException
    {
        String url = String.format("https://api.twitch.tv/kraken/streams/%s",
                Uri.encode(channel));

        String obj = getString(url);

        if (obj == null)
            return null;

        return new JSONObject(obj);
    }

    public static final JSONObject searchChannels(String query, int limit) throws IOException, JSONException
    {
        String url = String.format("https://api.twitch.tv/kraken/search/channels?q=%s&limit=%d",
                Uri.encode(query), limit);

        String obj = getString(url);

        if (obj == null)
            return null;

        return new JSONObject(obj);
    }

    public static final JSONObject searchStreams(String query) throws IOException, JSONException
    {
        String url = String.format("https://api.twitch.tv/kraken/search/streams?q=%s",
                Uri.encode(query));

        String obj = getString(url);

        if (obj == null)
            return null;

        return new JSONObject(obj);
    }

    public static final ChannelAccessToken getChannelAccessToken(String channel) throws IOException, JSONException
    {
        String url = String.format("http://api.twitch.tv/api/channels/%s/access_token",
                Uri.encode(channel));

        String token = getString(url);

        if (token == null)
            return null;

        JSONObject jObj = new JSONObject(token);
        return new ChannelAccessToken(jObj.getString("sig"), jObj.getString("token"));
    }

    public static final String getPlaylist(String channel) throws IOException, JSONException
    {
        ChannelAccessToken token = getChannelAccessToken(channel);

        if (token ==  null)
            return null;

        String url = String.format(
                "http://usher.twitch.tv/select/%s.json?nauthsig=%s&nauth=%s&allow_source=true&allow_audio_only=true",
                Uri.encode(channel).toLowerCase(Locale.ENGLISH),
                Uri.encode(token.sig), Uri.encode(token.token));

        Log.d("KrakenApi", "Usher url: " + url);

        return getString(url);
    }

    public static final String getPlaylist2(String channel) throws IOException, JSONException
    {
        ChannelAccessToken token = getChannelAccessToken(channel);

        if (token ==  null)
            return null;

        String url = String.format(
                "http://usher.twitch.tv/api/channel/hls/%s.m3u8?sig=%s&token=%s&allow_source=true&allow_audio_only=true",
                Uri.encode(channel).toLowerCase(Locale.ENGLISH),
                Uri.encode(token.sig), Uri.encode(token.token));

        return getString(url);
    }

    private static final String getString(String url) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        try {
            if (conn.getResponseCode() == 200) {
                return readFullyString(conn.getInputStream());
            }

            if (conn.getResponseCode() < 500) {
                return null;
            }

            throw new IOException(conn.getResponseMessage());
        } finally {
            conn.disconnect();
        }
    }

    static final class ChannelAccessToken {
        public String sig;
        public String token;

        ChannelAccessToken(String sig, String token) {
            this.sig = sig;
            this.token = token;
        }
    }

    private static final String readFullyString(InputStream inputStream) throws IOException {
        return new String(readFully(inputStream), "UTF-8");
    }

    private static final byte[] readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }
}
