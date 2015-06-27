package app.nubankexercise.com.nubankexercise;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends FragmentActivity {

    //private FrameLayout mainContainer;

    ViewPager pager;
    ActivityPagesAdapter adapter;
    private String requestResult;
    private JSONArray billsJSON;
    JSONArray billsJSONarray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new HttpAsyncTask().execute("http://s3-sa-east-1.amazonaws.com/mobile-challenge/bill/bill_new.json");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ActivityPagesAdapter extends FragmentStatePagerAdapter
    {
        private int NUM_OF_FRAGMENTS;
        private MonthFragment[] monthFragments;
        private JSONObject billsObjects[];

        public ActivityPagesAdapter(FragmentManager fm)
        {
            super(fm);
            NUM_OF_FRAGMENTS = billsJSON.length();
            Log.i("APP", "array size = " + NUM_OF_FRAGMENTS);
            monthFragments = new MonthFragment[NUM_OF_FRAGMENTS];
            billsObjects = new JSONObject[NUM_OF_FRAGMENTS];
            try {
                for (int i = 0; i < NUM_OF_FRAGMENTS; i++)
                {
                    billsObjects[i] = billsJSON.getJSONObject(i).getJSONObject("bill");
                }
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
                Log.i("APP", exc.getMessage());
            }
        }



        @Override
        public Fragment getItem(int i) {
            switch(i)
            {
                case 0:
                    if(monthFragments[0] == null)
                    {
                        monthFragments[0] = MonthFragment.newInstance(billsObjects[i]);
                    }
                    return monthFragments[0];
                case 1:
                    if(monthFragments[1] == null)
                    {
                        monthFragments[1] = MonthFragment.newInstance(billsObjects[i]);
                    }
                    return monthFragments[1];
                case 2:
                    if(monthFragments[2] == null)
                    {
                        monthFragments[2] = MonthFragment.newInstance(billsObjects[i]);
                    }
                    return monthFragments[2];
                case 3:
                    if(monthFragments[3] == null)
                    {
                        monthFragments[3] = MonthFragment.newInstance(billsObjects[i]);
                    }
                    return monthFragments[3];
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_OF_FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            String str = "";
            int color = 0;
            switch(position)
            {
                case 0:
                    str =  "MAR";
                    color = getResources().getColor(R.color.color_overdue);
                    break;
                case 1:
                    str = "ABR";
                    color = getResources().getColor(R.color.color_closed);
                    break;
                case 2:
                    str = "MAI";
                    color = getResources().getColor(R.color.color_open);
                    break;
                case 3:
                    str = "JUN";
                    color = getResources().getColor(R.color.color_future);
                    break;
            }

            SpannableString ss = new SpannableString(str);
            ss.setSpan(new ForegroundColorSpan(color), 0, 3, 0);
            if(pager.getCurrentItem() != position)
            {
                ss.setSpan(new RelativeSizeSpan(0.8f), 0, 3, 0);
            }
            else
            {
                ss.setSpan(new RelativeSizeSpan(1.2f), 0, 3, 0);
            }

            return ss;
        }

    }

    private static String convertInputStreamToString(InputStream inputStream)
    {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while ((line = buffer.readLine()) != null) {
                result += line;
            }
            inputStream.close();
        }
        catch(Exception exc)
        {
            exc.printStackTrace();
            Log.i("app.nubankexercise", "Exception in convertInputStreamToString");
        }
        return result;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... URLs)
        {
            InputStream inputStream = null;
            try
            {
                if(!isNetworkAvailable())
                {
                    Toast.makeText(MainActivity.this, "Parece que você está sem internet! Por favor, verifique a sua conexão e tente novamente.",
                            Toast.LENGTH_SHORT).show();
                    return null;
                }
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(new HttpGet(URLs[0]));
                int status = httpResponse.getStatusLine().getStatusCode();
                if(status >= 400 && status < 500)
                {
                    Toast.makeText(MainActivity.this, "Houve um erro com seu pedido.", Toast.LENGTH_SHORT).show();
                }
                else if(status > 500 && status < 600)
                {
                    Toast.makeText(MainActivity.this, "Desculpe, estamos enfrentando problemas técnicos. Por favo, tente novamente mais tarde.",
                            Toast.LENGTH_SHORT).show();
                }
                inputStream = httpResponse.getEntity().getContent();
                if(inputStream != null)
                {
                    requestResult = convertInputStreamToString(inputStream);
                }
                else
                {
                    requestResult = "Did not work";
                }
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
                Log.i("app.nubankexercise", "Exception in doInBackground");
            }
            if(requestResult == null)
            {
                Log.i("app.nubankexercise", "requestResult == null");
            }
            Log.i("app.nubankexercise", requestResult);
            return requestResult;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                billsJSON = new JSONArray(result);
                JSONObject obj1 = billsJSON.getJSONObject(0);
                JSONObject bill1 = obj1.getJSONObject("bill");
                JSONObject obj2 = billsJSON.getJSONObject(1);
                JSONObject obj3 = billsJSON.getJSONObject(2);
                JSONObject obj4 = billsJSON.getJSONObject(3);
                Log.i("APP", bill1.names().toString());
                Log.i("APP", obj2.names().toString());
                Log.i("APP", obj3.names().toString());
                Log.i("APP", obj4.names().toString());
                //billsJSONarray = billsJSON.getJSONArray("bill");
                adapter = new ActivityPagesAdapter(getSupportFragmentManager());
                pager = (ViewPager) findViewById(R.id.pager);
                pager.setAdapter(adapter);
                Log.i("APP" , "ViewPager added");
                // http://hmkcode.com/android-parsing-json-data/
            }
            catch(Exception exc){
                exc.printStackTrace();
            }
        }
    }

}
