package com.example.demoandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.apiguard3.APIGuard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private final String TAG = "DemoAndroidApp";
    private final String REQUEST_URL = "https://mobile-ref.fastcache.net/app/endpoint/v42";
    private Button mMakeRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMakeRequestButton = findViewById(R.id.button);

        mMakeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button Clicked!!!");
                new MakeHTTPRequest().execute();
            }
        });
    }

    class MakeHTTPRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids){
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            //Sample request body
            String requestBodyContent = "username=shpdbgtstM&password=password123";
            byte[] requestBodyBytes = requestBodyContent.getBytes();
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), requestBodyBytes);

            Request request = new Request.Builder()
                    .url(REQUEST_URL)
                    .method("POST", requestBody)
                    .build();

            //map
            final Map<String, String> requestHeaders = APIGuard.getSharedInstance().getRequestHeaders(REQUEST_URL, requestBodyBytes);
            for (String key : requestHeaders.keySet()) {
                Log.i(TAG, key + ": " + requestHeaders.get(key));
            }
            /*
            * Add the headers from header map requestHeaders to the main Request
             */
            request = request.newBuilder()
                    .headers(Headers.of(requestHeaders))
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                public  void onResponse(Call call, Response response) throws IOException{
                    Log.d(TAG, response.toString());
                    //multi map [same key with different values in that] to flat map or simple map
                    Map<String, String> responseHeaders = new HashMap<>();
                    for (String header : response.headers().names()){
                        //parse response headers requires simple map
                        responseHeaders.put(header, response.headers().get(header));
                    }
                }

                public void onFailure(Call call, IOException e){
                    Log.d(TAG, "Call Failed: "+ e.getLocalizedMessage());
                }
            });
            return null;
        }
    }
}