package particle.travelsafetest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import static particle.travelsafetest.R.*;

public class MainActivity extends AppCompatActivity {

    Button btOn,btOff,btScan;
    Switch btSwitch;
    ListView deviceListView;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> lvAdapter;
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

        btOn            = (Button) findViewById(id.btOn);
        btOff           = (Button) findViewById(id.btOff);
        btScan          = (Button) findViewById(id.btScan);
        btSwitch        = (Switch) findViewById(id.btSwitch);
        deviceListView  = (ListView) findViewById(id.deviceList);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        btOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()){
                    Intent intentBtOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentBtOn,0);
                    Snackbar.make(v, "Bluetooth ON", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Toast.makeText(getApplicationContext(), "BT already On", Toast.LENGTH_SHORT);
                    Snackbar.make(v, "Bluetooth already ON", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        btOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isEnabled()){
                    mBluetoothAdapter.disable();
                    Snackbar.make(v, "Bluetooth turned OFF", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(v, "Bluetooth already OFF", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        final ArrayList<String> deviceList = new ArrayList<>();
        lvAdapter = new ArrayAdapter<String>(getApplicationContext(), layout.activity_listview, deviceList);
        deviceListView.setAdapter(lvAdapter);
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvAdapter.clear();
                pairedDevices = mBluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device: pairedDevices ) {
                    deviceList.add(device.getName());
                }
                lvAdapter.notifyDataSetChanged();
            }
        });

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = deviceListView.getItemAtPosition(position).toString();
                Snackbar.make(view, item + " clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
}
