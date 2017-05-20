package particle.travelsafetest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import static particle.travelsafetest.R.id;
import static particle.travelsafetest.R.id.pairedDeviceList;
import static particle.travelsafetest.R.layout;

public class MainActivity extends AppCompatActivity {

    Button btOn, btOff, btScan, btDiscover;
    Switch btSwitch;
    ListView pairedDeviceListView, newDeviceListView;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> lvPairedAdapter;
    ArrayAdapter<String> lvNewDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
       int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        btOn = (Button) findViewById(id.btOn);
        btOff = (Button) findViewById(id.btOff);
        btScan = (Button) findViewById(id.btScan);
        btDiscover = (Button) findViewById(id.btDiscovery);
        btSwitch = (Switch) findViewById(id.btSwitch);
        pairedDeviceListView = (ListView) findViewById(pairedDeviceList);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        btOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent intentBtOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentBtOn, 0);
                    Snackbar.make(v, "Bluetooth ON", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Toast.makeText(getApplicationContext(), "BT already On", Toast.LENGTH_SHORT);
                    Snackbar.make(v, "Bluetooth already ON", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        btOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                    Snackbar.make(v, "Bluetooth turned OFF", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(v, "Bluetooth already OFF", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        final ArrayList<String> pairedDeviceList = new ArrayList<>();
        lvPairedAdapter = new ArrayAdapter<String>(getApplicationContext(), layout.activity_listview, pairedDeviceList);
        pairedDeviceListView.setAdapter(lvPairedAdapter);
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvPairedAdapter.clear();
                pairedDevices = mBluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : pairedDevices) {
                    pairedDeviceList.add(device.getName());
                }
                lvPairedAdapter.notifyDataSetChanged();
            }
        });

        btDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(mReceiver, filter);
            }
        });
        pairedDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = pairedDeviceListView.getItemAtPosition(position).toString();
                Snackbar.make(view, item + " clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ArrayList<String> newDeviceList = new ArrayList<>();
            lvNewDeviceAdapter = new ArrayAdapter<String>(getApplicationContext(), layout.activity_listview, newDeviceList);
            newDeviceListView = (ListView) findViewById(id.newDeviceList);
            newDeviceListView.setAdapter(lvNewDeviceAdapter);
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String deviceName = device.getName();
                newDeviceList.add(deviceName+ " : "+ String.valueOf(rssi));
                //lvNewDeviceAdapter.notifyDataSetChanged();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }

        }
    };

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}
