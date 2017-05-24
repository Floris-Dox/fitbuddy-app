package flodox.fitbyte;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by doxfl on 21/05/2017.
 */

public class Mainapp extends AppCompatActivity {
    String hosturl = "http://188.226.148.45:3000/api/data";
    String dataSucces = "Data has been added to FitByte server";
    static String isoDate;

    EditText voornaamedit;
    EditText achternaamedit;
    EditText bpmedit;
    RequestQueue requestQueue;
    int[] hrm;
    int numberof = 100;

    JSONObject testObject;
    //JSONObject finalobject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voornaamedit = (EditText) findViewById(R.id.voornaamtxt);
        achternaamedit = (EditText) findViewById(R.id.achternaamtxt);
        bpmedit = (EditText) findViewById(R.id.bpmtxt);
        createRandomPulseArray();
        createTime();



    }


    private void createRandomPulseArray()
    {
        int[] hrm = new int[99];
        for(int i = 0; i < 99;)
        {
            int rand = ((int)(Math.random() * 40)) + 90;

            hrm[i] = rand;
            i++;
        }
    }


    private void createTime()
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        isoDate = df.format(new Date());
    }

    public static JSONObject jsonObject3(String voornaam, String achternaam, int bpm)
    {
        JSONObject jsonObj=new JSONObject();
        try
        {

            jsonObj.put("voornaam", voornaam);
            jsonObj.put("achternaam", achternaam);
            jsonObj.put("bpm", bpm);

        }catch(Exception e)
        {
            Log.d("Exec", e.getMessage());
        }
        Log.d("FileUtil",jsonObj.toString());
        return jsonObj;
    }


    public void postData( int bpm, Context context)
    {
        //String bpm = bpmedit.getText().toString();
        //int bpmint = Integer.parseInt(bpm.toString());
       // testObject = jsonObject3(voornaamedit.getText().toString(), achternaamedit.getText().toString(), bpm);
        //testObject = jsonObject3("joske", "vermeulen", 90);
        //"timestamp" : ISODate(), "Voornaam": "Maarten", "achternaam" : "wachters" "pulse": 67
        //testObject = jsonObject3(voornaamedit.getText().toString(), achternaamedit.getText().toString(), bpm);
        testObject = jsonObject3("Floris ", "Dox", bpm);

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, hosturl, testObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast toast = Toast.makeText(this), dataSucces, Toast.LENGTH_SHORT);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //requestQueue.add(request);
        AppController.getInstance().getRequestQueue().add(request);
    }

}


