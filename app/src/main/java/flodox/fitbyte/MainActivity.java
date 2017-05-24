package flodox.fitbyte;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.opengl.EGLDisplay;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by doxfl on 21/05/2017.
 */

public class MainActivity extends AppCompatActivity {
    ArrayList<String> deviceAddresses;
    ArrayList<Sensor> sensors;
    ListView listSensors;
    Intent addSensor;
    Intent detailSensor;
    static final int ADD_DEVICE = 123;
    SimpleArrayAdapter arrAdapter;
    TextView tv;

    private BluetoothAdapter mBluetoothAdapter = null;

    String testSenName="";
    String testSenAddress="";

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public class  SimpleArrayAdapter extends ArrayAdapter<Sensor> {
        public SimpleArrayAdapter(Context context, ArrayList<Sensor> values) {
            super(context, -1 , values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = null;
            if(convertView != null){
                v = convertView;
            }
            else {


                //v = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            Sensor sensor = getItem(position);
            TextView tv = (TextView) findViewById(R.id.nameSensor);
            //tv.setText(sensor.getName());
            TextView tvHeart = (TextView) findViewById(R.id.heartBeat);
           // tvHeart.setText(String.valueOf(sensor.heartB));
            return v;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toestemming om locatie voor BLE te mogen gebruiken
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceAddresses = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        addSensor = new Intent(MainActivity.this, AddSensorActivity.class);
        detailSensor = new Intent(MainActivity.this, DetailActivity.class);
        sensors = new ArrayList<>();
        listSensors = (ListView) findViewById(R.id.sensorList);
        arrAdapter = new SimpleArrayAdapter(this, sensors);
        listSensors.setAdapter(arrAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(addSensor, ADD_DEVICE);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_DEVICE) {




                // Make sure the request was successful
                if (resultCode == RESULT_OK) {

                    String newSenName = data.getStringExtra("name");
                    String newSenAddress = data.getStringExtra("address");
                    BluetoothDevice btDev = data.getParcelableExtra("btDev");
                    deviceAddresses.add(newSenAddress);

                    testSenAddress = newSenAddress;
                    testSenName = newSenName;

                    Toast.makeText(getApplicationContext(), newSenName + newSenAddress, Toast.LENGTH_SHORT).show();

                    BluetoothDevice newSen = mBluetoothAdapter.getRemoteDevice(newSenAddress);
                    Sensor newSensor = new Sensor(newSen, this);
                    sensors.add(newSensor);

                    arrAdapter.notifyDataSetChanged();

                }
            }
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
