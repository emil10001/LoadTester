package com.feigdev.loadtester;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Subscribe;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private TextView cpuStatus, ramStatus;
        private Button startStop, low, medium, high;
        private Handler handler;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            handler = new Handler();

            cpuStatus = (TextView) rootView.findViewById(R.id.cpu_status);
            ramStatus = (TextView) rootView.findViewById(R.id.ram_status);

            startStop = (Button) rootView.findViewById(R.id.start_stop);
            low = (Button) rootView.findViewById(R.id.low);
            medium = (Button) rootView.findViewById(R.id.medium);
            high = (Button) rootView.findViewById(R.id.high);

            startStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ThreadSpawn.isStarted()) {
                        getActivity().startService(new Intent(getActivity().getApplicationContext(), ThreadSpawn.class));
                    } else if (ThreadSpawn.isRunning()) {
                        ThreadSpawn.stopSpawner();
                    } else {
                        ThreadSpawn.startSpawner();
                    }
                }
            });

            low.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThreadSpawn.switchModes(Constants.LOW);
                }
            });
            medium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThreadSpawn.switchModes(Constants.MEDIUM);
                }
            });
            high.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThreadSpawn.switchModes(Constants.HIGH);
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            BusProvider.INSTANCE.bus().register(this);
        }

        @Override
        public void onPause() {
            BusProvider.INSTANCE.bus().unregister(this);
            super.onPause();
        }

        @Override
        public void onDestroy(){
            if (!ThreadSpawn.isRunning())
                ThreadSpawn.killSpawner();

            super.onDestroy();
        }

        @Subscribe
        public void runningStatus(final MessageTypes.RunningStatus status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (ThreadSpawn.isRunning())
                        startStop.setText(R.string.stop);
                    else
                        startStop.setText(R.string.start);
                }
            });
        }

        @Subscribe
        public void updateCpuStatus(final MessageTypes.CpuStatus status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    cpuStatus.setText(status.getStatus());
                }
            });
        }
        @Subscribe
        public void updateRamStatus(final MessageTypes.RamStatus status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ramStatus.setText(status.getStatus());
                }
            });
        }
    }

}
