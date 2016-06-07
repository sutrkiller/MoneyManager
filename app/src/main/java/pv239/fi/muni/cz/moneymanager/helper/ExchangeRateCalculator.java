package pv239.fi.muni.cz.moneymanager.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

/**
 * Created by Tobias on 6/7/2016.
 */
public class ExchangeRateCalculator {


    private static Map<String,BigDecimal> cache = new HashMap<>();
    private static class HttpAsyncTask extends AsyncTask<Currency, Void, BigDecimal> {


        @Override
        protected BigDecimal doInBackground(Currency... urls) {

            String url  = "http://finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=";
            String charset = "UTF-8";
            String query="";
            try {
                query  = String.format("%s%s=X",
                        URLEncoder.encode(urls[0].getCurrencyCode(), charset),
                        URLEncoder.encode(urls[1].getCurrencyCode(), charset));
            } catch (UnsupportedEncodingException e) {
                Log.e("Error parsing",Log.getStackTraceString(e));
                return BigDecimal.ONE;
            }
            BigDecimal result = BigDecimal.ONE;
            try {
                URLConnection connection = new URL(url  + query).openConnection();
                connection.setRequestProperty("Accept-Charset", charset);
                InputStream response = connection.getInputStream();
                Scanner scannerr = new Scanner(response);
                String responseBody = scannerr.useDelimiter(",").next();
                result = scannerr.nextBigDecimal();
                scannerr.close();
            } catch (IOException e) {
                Log.e("Error connection",Log.getStackTraceString(e));
                return BigDecimal.ONE;
            }
            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(BigDecimal result) {

        }
    }

    public static BigDecimal TransferRate(@Nonnull Currency curFrom,@Nonnull Currency curTo, BigDecimal value) {
        if (cache.containsKey(curFrom.getCurrencyCode()+curTo.getCurrencyCode())) {
            return cache.get(curFrom.getCurrencyCode()+curTo.getCurrencyCode());
        }

        try {
            return new HttpAsyncTask().execute(curFrom,curTo).get().multiply(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

}
