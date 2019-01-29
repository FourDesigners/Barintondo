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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    //COSTANTI
    final String REQUEST_ADD_COUPON="addCoupon";
    final String REQUEST_REMOVE_COUPON="removeCoupon";
    final String REQUEST_RESULT_OK="ok";
    Context context;

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

    //view
    Button useCouponBtn, addCouponBtn;
    EditText codCoupon, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;

        codCoupon = findViewById(R.id.codCoupon);
        email = findViewById(R.id.email);

        addCouponBtn = findViewById(R.id.btnAdd);
        addCouponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCoupon();
            }
        } );

        useCouponBtn = findViewById(R.id.couponBtn);
        useCouponBtn.setOnClickListener(new View.OnClickListener() {
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
                    //sendMessage();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(context, "Codice coupon:" + readMessage + "-", Toast.LENGTH_SHORT).show();
                    readMessage(readMessage);
                    //invio il messaggio di risposta
                    //sendMessage();
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

    private void sendMessage(String message) {
        if (communicationController.getState() != BTCommunicationController.STATE_CONNECTED) {
            Toast.makeText(this, getResources().getString(R.string.connectionLostMsg), Toast.LENGTH_SHORT).show();
        } else {
            //String message;
            //message = "ok"; //setup del messaggio da inviare
            byte[] send = message.getBytes();
            communicationController.write(send);
            Toast.makeText(this, "Messaggio inviato: " + message, Toast.LENGTH_SHORT).show();
        }

    }

    private void readMessage(String message) {
        if (communicationController.getState() != BTCommunicationController.STATE_CONNECTED) {
            Toast.makeText(this, getResources().getString(R.string.connectionLostMsg), Toast.LENGTH_SHORT).show();
            return;
        } else removeCoupon(message);

        //sendMessage();
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

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(context, "Individuati dispositivi nelle vicinanze", Toast.LENGTH_SHORT).show();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "Ricerca dispositivi terminata", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void addCoupon() {
        String newCod = codCoupon.getText().toString();
        String emailAddress = email.getText().toString();
        manageCoupons( REQUEST_ADD_COUPON , newCod , emailAddress );
    }

    public void removeCoupon(String couponCod) {
        manageCoupons( REQUEST_REMOVE_COUPON, couponCod, null );
    }

    private void manageCoupons(final String requestOp, final String couponCod, final String user ) {
        // Log.i( TAG , getClass().getSimpleName() + ":entered manageInterests( )");
        String Url = "http://barintondo.altervista.org/gestore_coupon.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                String[] result = response.split( "," );
                Log.i( "utilityApp" , "entered onResponse(), request: " + result[0] + ", result: " + result[1] );

                switch (result[0]) {
                    case REQUEST_ADD_COUPON:
                        boolean added = result[1].equals( REQUEST_RESULT_OK );
                        Log.i("utilityApp", "risultato server:" + result[1] + "-");
                        couponAdded( added );
                        break;
                    case REQUEST_REMOVE_COUPON:
                        boolean removed = result[1].equals( REQUEST_RESULT_OK );
                        Log.i("utilityApp", "risultato server:" + result[1] + "-");
                        couponRemoved( removed );
                        break;
                }
            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText( context , "Errore nella richiesta al server" , Toast.LENGTH_SHORT ).show();
            }
        } ) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , requestOp );
                MyData.put( "couponCod" , couponCod );
                MyData.put( "email" , user );
                return MyData;
            }
        };
        MyRequestQueue.add( MyStringRequest );
    }

    public void couponRemoved(boolean result) {
        if (result) {
            Toast.makeText( getApplicationContext() , "Coupon rimosso" , Toast.LENGTH_SHORT ).show();
            sendMessage("ok");
        } else {
            Toast.makeText(getApplicationContext(), "Errore nella rimozione", Toast.LENGTH_SHORT).show();
            sendMessage("err");
        }
    }

    public void couponAdded(boolean result) {
        if (result) {
            Toast.makeText( getApplicationContext() , "Coupon aggiunto" , Toast.LENGTH_SHORT ).show();
        } else
            Toast.makeText( getApplicationContext() , "Errore nella aggiunta" , Toast.LENGTH_SHORT ).show();
    }

}
