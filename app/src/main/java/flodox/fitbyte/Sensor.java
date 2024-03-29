package flodox.fitbyte;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by doxfl on 21/05/2017.
 */


public class Sensor {  //implements Parcelable {
    BluetoothDevice BTSensor;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String mDeviceName;
    private String mDeviceAddress;
    BluetoothGattCharacteristic characteristic;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    HashMap<String, String> HeartRateCharaData = new HashMap<String, String>();
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    String heartB;
    private ServiceConnection mServiceConnection;
    private BroadcastReceiver mGattUpdateReceiver;
    Mainapp mainapp = new Mainapp();

    public Sensor(String name){
        this.name = name;


    }

    public Sensor(BluetoothDevice btDev, Context context){
        BTSensor = btDev;
        name = btDev.getName();
        mDeviceAddress = btDev.getAddress();

        mDeviceName = btDev.getName();


        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                if (!mBluetoothLeService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    //finish();
                }
                // Automatically connects to the device upon successful start-up initialization.
                mBluetoothLeService.connect(mDeviceAddress);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

                mBluetoothLeService.connect(mDeviceAddress);

            }
        };

        mGattUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                    mConnected = true;
                    Log.d("Gatt Connected", String.valueOf(mConnected));
                    Log.d("adress: ", mDeviceAddress);
                } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
                   // mBluetoothLeService.connect(mDeviceAddress);
                   // mConnected = true;
                } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    // Show all the supported services and characteristics on the user interface.
                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    Log.d("In mGattUpdateReceiver", "Services Discoverd");
                } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                    //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA)); voorbeeld code display data
                    //TODO: code om heart rate data te displayen
                    Log.d("In ActionDataAvailable", "");
                    heartB = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                }
            }
        };

        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        Log.d("ServiceConnection", mServiceConnection.toString());
        Log.d("BTsensor", btDev.toString());
        context.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    private String name = null;

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    private int heartBeat = 0;
    public int getHeartBeat(){
        return heartBeat;
    }
    /*public void setHeartBeat(int heartBeat){
        this.heartBeat = heartBeat;
    }*/

    private int weight = 0;
    public int getWeight(){
        return weight;
    }
    public void setWeight(int weight){
        this.weight = weight;
    }

    private int length = 0;
    public int getLength(){
        return length;
    }
    public void setLength(int length){
        this.length = length;
    }

    private int idealHeartBeat = 0;
    public int getIdealHeartBeat(){
        return idealHeartBeat;
    }

    private String extra = null;
    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra){
        this.extra = extra;
    }


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        Log.d("In displayGattServices", "");
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "unknown_service";
        String unknownCharaString = "unknown_characteristic";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            Log.d("Gatt Service", String.valueOf( gattService.getType()) + gattService.getUuid().toString());
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
                Log.d("Gatt Characteristic", gattCharacteristic.getUuid().toString());
                if (gattCharacteristic.getUuid().toString() == "00002a37-0000-1000-8000-00805f9b34fb" || String.valueOf(gattCharacteristic.getUuid()) == "00002a37-0000-1000-8000-00805f9b34fb"){
                    characteristic = gattCharacteristic;
                    Log.d("Yes yes yes", "");
                    Log.d("Characteristic", String.valueOf(characteristic.getProperties()));

                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }


        /*for (ArrayList<BluetoothGattCharacteristic> charas : mGattCharacteristics){
            for (BluetoothGattCharacteristic charac: charas){
                if (charac.getUuid().toString() == "00002a37-0000-1000-8000-00805f9b34fb"){
                    Log.d("Yes yes","");
                }
            }
        }*/




        subscribeHeartRate();
    }

    private void subscribeHeartRate(){
        for (int i = 0; i<mGattCharacteristics.size(); i++){
            for (int j = 0; j<mGattCharacteristics.get(i).size(); j++){
                if (mGattCharacteristics.get(i).get(j).getUuid().toString() == "00002a37-0000-1000-8000-00805f9b34fb"){
                    Log.d("Yes yes","");
                }
            }
        }

        for (ArrayList<BluetoothGattCharacteristic> charas : mGattCharacteristics){
            for (BluetoothGattCharacteristic charac: charas){
                if (charac.getUuid().toString().equals("00002a37-0000-1000-8000-00805f9b34fb")){
                    Log.e("yesyesyes", "");
                    characteristic = charac;
                }
            }
        }
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                //If there is an active notification on a characteristic, clear
                //it first so it doesn't update the data field on the user interface.
               if (mNotifyCharacteristic != null) {
                   mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                   mNotifyCharacteristic = null;
               }

                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                Log.d("NotifyChar", String.valueOf(mNotifyCharacteristic.getDescriptor(UUID_HEART_RATE_MEASUREMENT)));
                String bpms = String.valueOf(mNotifyCharacteristic.getDescriptor(UUID_HEART_RATE_MEASUREMENT));
                //int bpm = Integer.parseInt(bpms.toString());
                 //int bpm = Integer.parseInt(bpms);
                //Integer bpm = Integer.valueOf(heartB);
                //mainapp.postData(bpm);


            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}

