package particle.travelsafetest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import static particle.travelsafetest.R.id.newDeviceListViewUI;
import static particle.travelsafetest.R.id.pairedDeviceListUI;
import static particle.travelsafetest.R.layout;

public class MainActivity extends AppCompatActivity {

    Button btOn, btOff, btScan, btDiscover;
    Switch btSwitch;
    ListView pairedDeviceListView, newDeviceListView;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> lvPairedAdapter;
    ArrayAdapter<String> lvNewDeviceAdapter;
    ArrayList<String> newDeviceList;

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
        Log.v("agam", "in onCreate");
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        btOn = (Button) findViewById(id.btOn);
        btOff = (Button) findViewById(id.btOff);
        btScan = (Button) findViewById(id.btScan);
        btDiscover = (Button) findViewById(id.btDiscovery);
        btSwitch = (Switch) findViewById(id.btSwitch);
        pairedDeviceListView = (ListView) findViewById(pairedDeviceListUI);
        newDeviceListView = (ListView) findViewById(newDeviceListViewUI);
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
        lvPairedAdapter = new ArrayAdapter<>(getApplicationContext(), layout.activity_listview, pairedDeviceList);
        pairedDeviceListView.setAdapter(lvPairedAdapter);

        newDeviceList = new ArrayList<>();
        //final ArrayList<String> newDeviceList = new ArrayList<>();
        lvNewDeviceAdapter = new ArrayAdapter<>(getApplicationContext() ,layout.activity_listview, newDeviceList);
        newDeviceListView.setAdapter(lvNewDeviceAdapter);

        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // newDeviceList.clear();
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
                lvNewDeviceAdapter.clear();
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

        newDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = newDeviceListView.getItemAtPosition(position).toString();
                Snackbar.make(view, item + " started monitoring", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();

            }
        });

    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String deviceName = device.getName();
                Log.v("agam",deviceName+ " : "+ String.valueOf(rssi));
                lvNewDeviceAdapter.add(deviceName+ " : "+ String.valueOf(rssi));
                //newDeviceList.add(deviceName+ " : "+ String.valueOf(rssi));
            }

        }
    };

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}
