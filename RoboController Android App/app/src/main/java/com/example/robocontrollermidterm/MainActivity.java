package com.example.robocontrollermidterm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button showPairedDevicesButton;
    private ListView pairedDevicesListView;

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevicesSet;

    public static String EXTRA_ADDRESS ="device_address";

    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent intent = new Intent(MainActivity.this, LedControllerActivity.class);
            intent.putExtra(EXTRA_ADDRESS, address);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showPairedDevicesButton = findViewById(R.id.pairedDevicesButton);
        pairedDevicesListView = findViewById(R.id.listView);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Toast.makeText(this, "No bluetooth adapter found in the device", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            if(bluetoothAdapter.isEnabled()){

            }
            else{
                // ask the user to turn on the bluetooth on
                Intent turnBTOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTOnIntent, 1);
            }
        }

        showPairedDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPairedDevicesList();
            }
        });
    }

    private void showPairedDevicesList(){
        pairedDevicesSet = bluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevicesSet.size() > 0){
            for(BluetoothDevice bt : pairedDevicesSet){
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else{
            Toast.makeText(this, "No Paired BLuetooth Devices Found", Toast.LENGTH_SHORT).show();
        }
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        pairedDevicesListView.setAdapter(arrayAdapter);
        pairedDevicesListView.setOnItemClickListener(listItemClickListener);
    }
}
