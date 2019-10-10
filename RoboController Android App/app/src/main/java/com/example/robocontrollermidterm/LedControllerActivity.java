package com.example.robocontrollermidterm;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.UUID;

public class LedControllerActivity extends AppCompatActivity {

    private SeekBar leftArmSeekbar, rightArmSeekbar, neckSeekbar;
    // private SwitchCompat switchCompat;
    private ToggleButton toggleButton;
    private ImageView disconnectImageViewButton;

    private String address = null;

    private ProgressDialog progressDialog;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private boolean isBluetoothConnected = false;

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_led_controller);

        leftArmSeekbar = findViewById(R.id.leftArmSeekbar);
        rightArmSeekbar = findViewById(R.id.rightArmSeekbar);
        neckSeekbar = findViewById(R.id.neckSeekbar);

        toggleButton = findViewById(R.id.eyesToggleButton);

        disconnectImageViewButton = findViewById(R.id.disconnectImageViewButton);

        leftArmSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    try{
                        String leftArmSeekbarMessage = "L" + String.valueOf(progress);
                        bluetoothSocket.getOutputStream().write(leftArmSeekbarMessage.getBytes());
                    }
                    catch (IOException e){
                        msg("Error");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rightArmSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    try{
                        String rightArmSeekbarMessage = "R" + String.valueOf(progress);
                        bluetoothSocket.getOutputStream().write(rightArmSeekbarMessage.getBytes());
                    }
                    catch (IOException e){
                        msg("Error");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        neckSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    try{
                        String neckSeekbarMessage = "N" + String.valueOf(progress);
                        bluetoothSocket.getOutputStream().write(neckSeekbarMessage.getBytes());
                    }
                    catch (IOException e){
                        msg("Error");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        disconnectImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Toast.makeText(LedControllerActivity.this, "Is Checked", Toast.LENGTH_SHORT).show();
                    turnOnLed();
                }
                else{
                    // Toast.makeText(LedControllerActivity.this, "Is Not Checked", Toast.LENGTH_SHORT).show();
                    turnOffLed();
                }
            }
        });

        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        new ConnectBT().execute();
    }

    private void disconnect() {
        if (bluetoothSocket != null) //If the btSocket is busy
        {
            try {
                bluetoothSocket.close(); //close connection
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout
    }

    private void turnOffLed() {
        if (bluetoothSocket != null)
        {
            try {
                bluetoothSocket.getOutputStream().write("TF".toString().getBytes());
                Toast.makeText(this, "Turned Off", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnLed() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write("TO".toString().getBytes());
                Toast.makeText(this, "Turned On", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>{
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LedControllerActivity.this, "Connecting...", "Please Wait.");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                if(bluetoothSocket == null || !isBluetoothConnected){
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothSocket.connect();
                }
            }
            catch (IOException e) {
                connectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(!connectSuccess){
                msg("Connection Failed.");
            }
            else{
                msg("Connected");
                isBluetoothConnected = true;
            }
            progressDialog.dismiss();
        }
    }
}
