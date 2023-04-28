package com.taxieasyua.job.driver_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.taxieasyua.job.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

import javax.net.ssl.HttpsURLConnection;


public class ServicesFragment extends Fragment {
    private ListView listView;
    private String[] array = services().toArray(new String[0]);
    Activity activity;
    public static final String TAG = "ServicesFragment";

    public ServicesFragment() throws MalformedURLException, InterruptedException, JSONException {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_app_servises, container, false);
        listView = view.findViewById(R.id.list);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.services_adapter_layout, array);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemChecked(0,true);

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            activity = (Activity) context;
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        List<String> servicesList = new ArrayList<>();
        SparseBooleanArray booleanArray = listView.getCheckedItemPositions();
        for (int i = 0; i < booleanArray.size(); i++) {
            if(booleanArray.get(booleanArray.keyAt(i))) {
                servicesList.add(array[booleanArray.keyAt(i)]);
                Log.d(TAG, "click: " + array[booleanArray.keyAt(i)]);
            }
        }

        try {
            ((Postman) activity).fragmentMailService(servicesList);
        } catch (ClassCastException ignored) {}
    }
    private ArrayList<String> services () throws MalformedURLException, InterruptedException, JSONException {

//        String[] servicesArr;
//        servicesArr = new String[]{
//                "Терминал",
//                "Таксі Лайт Юа",
//                "UBER",
//                "UKLON",
//                "BOLT",
//                "OnTaxi",
//                "838",
//                "Lubimoe Taxi",
//                "3040",
//                "Максім"
//        };

        String url = "https://m.easy-order-taxi.site/api/servicesAll/Android";

        return  servicesAll(url);

//        return new String[]{
//                "Терминал",
//                "Таксі Лайт Юа",
//                "UBER",
//                "UKLON",
//                "BOLT",
//                "OnTaxi",
//                "838",
//                "Lubimoe Taxi",
//                "3040",
//                "Максім"
//        };
    }





    public ArrayList<String> servicesAll (String urlString) throws MalformedURLException, InterruptedException, JSONException {
        URL url = new URL(urlString);
        final String TAG = "TAG";

        Exchanger<String> exchanger = new Exchanger<>();

        AsyncTask.execute(() -> {
            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                if (urlConnection.getResponseCode() == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    exchanger.exchange(convertStreamToString(in));
                } else {

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            urlConnection.disconnect();
        });

        ServicesFragment.ResultFromThread first = new ResultFromThread(exchanger);

        JSONArray jsonarray = new JSONArray(first.message);
        Log.d(TAG, "servicesAll contacts: " + jsonarray );
        ArrayList<String> servicesAll = new ArrayList<>();
        for(int i=0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            servicesAll.add(jsonobject.getString("name"));
        } Log.d(TAG, "servicesAll: " + servicesAll);

        return servicesAll;
    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static class ResultFromThread {
        public String message;

        public ResultFromThread(Exchanger<String> exchanger) throws InterruptedException {
            this.message = exchanger.exchange(message);
        }

    }
}
