package it.uniba.di.sms.barintondo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
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

import java.util.Objects;
import java.util.Set;

import it.uniba.di.sms.barintondo.utils.BTCommunicationController;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.Coupon;
import it.uniba.di.sms.barintondo.utils.MyListners;

public class CouponDetailActivity extends AppCompatActivity implements Constants {

    private String TAG_CLASS=getClass().getSimpleName();
    Toolbar myToolbar;
    ImageView myImageView;
    TextView desc;
    Coupon myCoupon;
    Button useBtn, btnDetailLuogo;

    //BT
    private Dialog dialog;
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
    private ArrayAdapter<String> discoveredDevicesAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_coupon_detail );
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );

        myCoupon = getIntent().getParcelableExtra( Constants.INTENT_COUPON);

        myToolbar = findViewById( R.id.coupon_detail_activity_toolbar );
        myToolbar.setTitle( myCoupon.getLuogo() );
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

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
                checkBTAvailability();
            }
        } );

        btnDetailLuogo = findViewById( R.id.btnDettaglioPosto );
        btnDetailLuogo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDettaglioLuogo();
            }
        } );

    }

    private void goDettaglioLuogo(){
        Intent intent = new Intent( this, LuogoDetailActivity.class );
        intent.putExtra( INTENT_LUOGO_COD, myCoupon.getCodLuogo() );
        startActivity( intent );
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
        else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            } else {
                communicationController = new BTCommunicationController(this, BTHandler);
                startCommunication();
            }
        }

    }

    private void startCommunication() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.bluetooth_devices_list);
        Toolbar title = dialog.findViewById(R.id.bluetooth_list_toolbar);
        title.setTitle(R.string.devicesDialog);
        dialog.setTitle(getResources().getString(R.string.devicesDialog));
        dialog.setCanceledOnTouchOutside(true);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        //richiedo a runtime il permesso altrimenti startDiscovery() dirà che non ci sono dispositivi disponibili
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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

                connectToDevice(address); //CONNESSIONE AL DISPOSITIVO
                //sendMessage(); //INVIO MESSAGGIO
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

                connectToDevice(address); //CONNESSIONE AL DISPOSITIVO
                //sendMessage(); //INVIO MESSAGGIO

                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        //Toolbar titleDialog = findViewById(R.id.bluetooth_list_toolbar);
        //titleDialog.setTitle(R.string.devicesDialog);
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
                            Log.i(TAG, "Connesso a: " + connectingDevice.getName() );
                            break;
                        case BTCommunicationController.STATE_CONNECTING:
                            Log.i(TAG, "Connessione in corso..." );
                            break;
                        case BTCommunicationController.STATE_LISTEN:
                            Log.i(TAG, "In attesa di connessioni in entrata" );
                            break;
                        case BTCommunicationController.STATE_NONE:
                            Log.i(TAG, "Connessione interrotta" );
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    sendMessage();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    readMessage(readMessage);
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectedToMsg)
                                    + connectingDevice.getName(),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, getResources().getString(R.string.connectionInterruptedMsg), Toast.LENGTH_SHORT).show();
        } else {
            String message;
            message = myCoupon.getCod(); //setup del messaggio da inviare
            byte[] send = message.getBytes();
            communicationController.write(send);
            Log.i(TAG,"inviato: " + message);
        }

    }

    private void readMessage(String message) {
        if (communicationController.getState() != BTCommunicationController.STATE_CONNECTED) {
            Toast.makeText(this, getResources().getString(R.string.connectionInterruptedMsg), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ricevuto: " + message, Toast.LENGTH_SHORT).show();
            if(message.contains("ok"))
                communicationController.stop();
            else {
                Toast.makeText(this, "errore", Toast.LENGTH_SHORT).show();
            }
        }
        communicationController.stop();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    communicationController = new BTCommunicationController(this, BTHandler);
                    startCommunication();
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

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
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
            }
        }
    };

}
