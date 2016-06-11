package pv239.fi.muni.cz.moneymanager.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import pv239.fi.muni.cz.moneymanager.crypto.ALockingClass;

/**
 * Class that downloads current exchange rate using Yahoo Finance Api
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 6/7/2016.
 */
public class ExchangeRateCalculator {
    SharedPreferences prefs;
    public ExchangeRateCalculator(Context context) {
        prefs = context.getSharedPreferences(ALockingClass.PREFS,Context.MODE_PRIVATE);
    }

    private Map<String,BigDecimal> cache = new HashMap<>();

    public BigDecimal transferRate(@Nonnull Currency curFrom, @Nonnull Currency curTo, BigDecimal value) {
        if (curFrom.getCurrencyCode().equals(curTo.getCurrencyCode())) return BigDecimal.ONE.multiply(value);
        String key = curFrom.getCurrencyCode() + curTo.getCurrencyCode();
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            try {
                BigDecimal bd = new HttpAsyncTask().execute(curFrom, curTo).get();
                if (bd == null) return null;
                cache.put(key, bd);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                prefs.edit().putString(key, String.valueOf(bd)).putString(key+"|date", format.format(Calendar.getInstance().getTime())).apply();
                return bd.multiply(value);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public boolean checkOlderRateDownloaded(@Nonnull Currency curFrom, @Nonnull Currency curTo) {
        return prefs.contains(curFrom.getCurrencyCode() + curTo.getCurrencyCode());
    }

    public BigDecimal getOlderRateDownloaded(@Nonnull Currency curFrom, @Nonnull Currency curTo) {
        String rate = prefs.getString(curFrom.getCurrencyCode() + curTo.getCurrencyCode(),null);
        if (rate == null) return null;
        return BigDecimal.valueOf(Double.parseDouble(rate));
    }

    public Date getOlderDateDownloaded(@Nonnull Currency curFrom, @Nonnull Currency curTo) {
        String date = prefs.getString(curFrom.getCurrencyCode() + curTo.getCurrencyCode() + "|date",null);
        if (date==null) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private static class HttpAsyncTask extends AsyncTask<Currency, Void, BigDecimal> {


        @Override
        protected BigDecimal doInBackground(Currency... urls) {

            String url  = "http://finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=";
            String charset = "UTF-8";
            String query;
            try {
                query  = String.format("%s%s=X",
                        URLEncoder.encode(urls[0].getCurrencyCode(), charset),
                        URLEncoder.encode(urls[1].getCurrencyCode(), charset));
            } catch (UnsupportedEncodingException e) {
                Log.e("Error parsing",Log.getStackTraceString(e));
                return null;
            }
            BigDecimal result;
            try {
                URLConnection connection = new URL(url  + query).openConnection();
                connection.setRequestProperty("Accept-Charset", charset);
                InputStream response = connection.getInputStream();
                Scanner scanner = new Scanner(response);
                scanner.useDelimiter(",").next();
                result = scanner.nextBigDecimal();
                scanner.close();
            } catch (IOException e) {
                Log.e("Error connection",Log.getStackTraceString(e));
                return null;
            }
            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(BigDecimal result) {

        }
    }

}
