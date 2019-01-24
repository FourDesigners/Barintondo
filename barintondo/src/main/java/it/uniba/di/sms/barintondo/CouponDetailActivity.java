package it.uniba.di.sms.barintondo;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import it.uniba.di.sms.barintondo.utils.BTCommunicationController;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.CouponLuogo;
import it.uniba.di.sms.barintondo.utils.InternetConnection;

public class CouponDetailActivity extends AppCompatActivity implements Constants {

    private static final String TAG = CouponDetailActivity.class.getSimpleName();

    Toolbar myToolbar;
    ImageView myImageView;
    TextView desc;
    CouponLuogo myCoupon;
    Button useBtn;

    //BT
    static String nameUUID = "it.uniba.di.sms.Barintondo";
    private final static UUID MY_UUID = UUID.nameUUIDFromBytes(nameUUID.getBytes());

    private TextView status;
    private ListView listView;
    private Dialog dialog;
    private ArrayAdapter<String> chatAdapter;
    private ArrayList<String> chatMessages;
    private BluetoothAdapter bluetoothAdapter;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";
    double average=0;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private BTCommunicationController communicationController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_coupon_detail );
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );

        myCoupon = getIntent().getParcelableExtra( Constants.INTENT_COUPON);

        myToolbar = findViewById( R.id.coupon_detail_activity_toolbar );
        myToolbar.setTitle( myCoupon.getLuogo() );
        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );

        myImageView = findViewById( R.id.luogoDetailImage );
        //thumbnail
        /*Glide.with( this )
                .load( imagesPath + myCoupon.getThumbnailLink() )
                .into( myImageView );*/

        desc = findViewById( R.id.couponDesc );
        desc.setText(myCoupon.getDescription());

        useBtn = findViewById( R.id.useBtn );
        useBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetConnection.isNetworkAvailable( CouponDetailActivity.this )) {
                    Toast.makeText( CouponDetailActivity.this , getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
                } else checkBTAvailability();
            }
        } );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected( item );

    }

    private void checkBTAvailability() {

        //check if device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.btNotAvailable), Toast.LENGTH_SHORT).show();
        }
        else showPrinterPickDialog();

    }

    private void showPrinterPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.bluetooth_devices_list);
        dialog.setTitle(getResources().getString(R.string.devicesDialog));

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

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
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                Toast.makeText(view.getContext(),info,Toast.LENGTH_LONG).show();
                String address = info.substring(info.length() - 17);

                connectToDevice(address); //INVIO MESSAGGIO
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                Toast.makeText(view.getContext(),info,Toast.LENGTH_LONG).show();
                connectToDevice(address);

                //INVIO MESSAGGIO
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        communicationController.connect(device);
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
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    //ELABORAZIONE DEL MESSAGGIO RICEVUTO DENTRO MESSAGE_READ
                    String mess = "Coupon code: " + readMessage;
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectedToMsg) + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void sendMessage(String message) {
        if (communicationController.getState() != BTCommunicationController.STATE_CONNECTED) {
            Toast.makeText(this, getResources().getString(R.string.connectionLostMsg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            message = myCoupon.getCod(); //setup del messaggio da inviare
            byte[] send = message.getBytes();
            communicationController.write(send);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    communicationController = new BTCommunicationController(this, BTHandler);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.btDisabledMsg), Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            communicationController = new BTCommunicationController(this, BTHandler);
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

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };

}
