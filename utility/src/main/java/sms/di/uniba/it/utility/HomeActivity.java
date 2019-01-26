package sms.di.uniba.it.utility;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {

    Button couponBtn;

    //BT
    String STRING_TOAST_MSG="toast";

    private BluetoothAdapter bluetoothAdapter;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private BTCommunicationController communicationController;
    private BluetoothDevice connectingDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        couponBtn = findViewById(R.id.couponBtn);
        couponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBTAvailability();
            }
        } );

    }

    private void checkBTAvailability() {

        //check if device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.btNotAvailable), Toast.LENGTH_SHORT).show();
        }
        else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            } else {
                communicationController = new BTCommunicationController(this, BTHandler);
                startReceivingMode();
            }
        }

    }

    private void startReceivingMode() {

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        //richiedo a runtime il permesso altrimenti startDiscovery() dirà che non ci sono dispositivi disponibili
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        bluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        if (communicationController != null) {
            if (communicationController.getState() == BTCommunicationController.STATE_NONE) {
                communicationController.start();
            }
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    private Handler BTHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BTCommunicationController.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.connectedToMsg) + connectingDevice.getName(),Toast.LENGTH_SHORT).show();
                            break;
                        case BTCommunicationController.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.connectingMsg),Toast.LENGTH_SHORT).show();
                            break;
                        case BTCommunicationController.STATE_LISTEN:
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.listeningModeMsg),Toast.LENGTH_SHORT).show();
                            break;
                        case BTCommunicationController.STATE_NONE:
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.notConnectedMsg),Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    sendMessage();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //aggiungo stringa esplicativa
                    String mess = "Codice coupon: " + readMessage;
                    readMessage(mess);
                    //invio il messaggio di risposta
                    sendMessage();
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectedToMsg) + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(STRING_TOAST_MSG),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void sendMessage() {
        if (communicationController.getState() != BTCommunicationController.STATE_CONNECTED) {
            Toast.makeText(this, getResources().getString(R.string.connectionLostMsg), Toast.LENGTH_SHORT).show();
        } else {
            String message;
            message = "ok"; //setup del messaggio da inviare
            byte[] send = message.getBytes();
            communicationController.write(send);
            Toast.makeText(this, "Messaggio inviato: " + message, Toast.LENGTH_SHORT).show();
        }

    }

    private void readMessage(String message) {
        if (communicationController.getState() != BTCommunicationController.STATE_CONNECTED) {
            Toast.makeText(this, getResources().getString(R.string.connectionLostMsg), Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "ricevuto: " + message, Toast.LENGTH_SHORT).show();
        }

        sendMessage();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    communicationController = new BTCommunicationController(this, BTHandler);
                    startReceivingMode();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.btDisabledMsg), Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (communicationController != null)
            communicationController.stop();
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            /*if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String deviceName;
                    if(device.getName() == null) //controllo che sia fornito un nome al dispositivo
                        deviceName = getResources().getString(R.string.nameHidden);
                    else deviceName = device.getName();
                    discoveredDevicesAdapter.add(deviceName + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }*/
        }
    };
}
