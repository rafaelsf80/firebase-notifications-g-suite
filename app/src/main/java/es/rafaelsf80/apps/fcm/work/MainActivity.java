/**
 * Copyright 2016 Rafael Sanchez Fuentes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Rafael Sanchez Fuentes rafaelsf80 at gmail dot com
 */


package es.rafaelsf80.apps.fcm.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private static final boolean SHOW_UI = true; // Debug boolean to show UI or not

    public static final ArrayList<String> TOPICS = new ArrayList<>();
    public  String [] groups;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar pbRegistration;

    MyGroupAdapter groupAdapter = null;
    public LinearLayout mMainLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (SHOW_UI) {
            setContentView(R.layout.activity_main);
            mMainLL = (LinearLayout) findViewById(R.id.llMain);

            pbRegistration = (ProgressBar) findViewById(R.id.pbRegistration);
            pbRegistration.setVisibility(ProgressBar.GONE);

            // Initialize groups and TOPICS strings
            groups = getResources().getStringArray(R.array.groups);
            TOPICS.clear();
            for(int i=0;i<groups.length;i++) {
                TOPICS.add("");  // All groups unchecked
            }

            // **** LISTVIEW and ADAPTER **** //
            groupAdapter = new MyGroupAdapter(this);
            ListView listView = (ListView) findViewById(R.id.lvGroups);
            listView.setAdapter(groupAdapter);

            // **** BUTTON and LISTENER **** //
            final Button btSelectedGroups = (Button) findViewById(R.id.btSelectGroup);
            btSelectedGroups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d(TAG, "TOPICS selected: " + TOPICS.toString());

                    // At least one topic must be selected
                    boolean no_topics_selected = true;
                    for (String topic : TOPICS)
                        if (!topic.equals(""))
                            no_topics_selected = false;

                    if (no_topics_selected)
                        Toast.makeText(MainActivity.this, getString(R.string.atleast_one_topic_selected),
                                Toast.LENGTH_LONG).show();
                    else if (checkPlayServices()) {
                        pbRegistration.setVisibility(ProgressBar.VISIBLE);
                        // Start IntentService to register this application with GCMxs
                        Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                        startService(intent);
                    }
                }

                private boolean checkPlayServices() {
                    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                    int resultCode = apiAvailability.isGooglePlayServicesAvailable(MainActivity.this);
                    if (resultCode != ConnectionResult.SUCCESS) {
                        if (apiAvailability.isUserResolvableError(resultCode)) {
                            apiAvailability.getErrorDialog(MainActivity.this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                                    .show();
                        } else {
                            Log.i(TAG, "This device is not supported.");
                            finish();
                        }
                        return false;
                    }
                    return true;
                }
            });
        }

        // **** BROADCAST_RECEIVER **** //

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SHOW_UI)
                    pbRegistration.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

                if (sentToken) {
                    if (SHOW_UI)
                        Toast.makeText(context, getString(R.string.gcm_message_sent), Toast.LENGTH_LONG).show();
                    Log.d(TAG, getString(R.string.gcm_message_sent));
                } else {
                    if (SHOW_UI)
                        Toast.makeText(context, getString(R.string.token_error_message), Toast.LENGTH_LONG).show();
                    Log.d(TAG, getString(R.string.token_error_message));
                }
                finish();
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private class MyGroupAdapter extends BaseAdapter {

        private Context mContext = null;
        private ArrayList mSelectedGroups;
        private String [] groups = getResources().getStringArray(R.array.groups);

        public MyGroupAdapter(Context context) {
            mContext = context;
        }

        private class ViewHolder {
            TextView tv;
            CheckBox cb;
        }

        @Override
        public Object getItem(int i) {
            return groups[i];
        }

        @Override
        public int getCount() {
            return groups.length;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            final int position1 = position;
            final String checkedGroup = groups[position];

            Log.d(TAG, "getView position: " + String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.group, null);

                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.tvGroupItem);
                holder.cb = (CheckBox) convertView.findViewById(R.id.cbGroupItem);
                convertView.setTag(holder);

                holder.cb.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;

                        //Add or remove topics from MainActivity.TOPICS array
                        if (cb.isChecked())
                            if (!MainActivity.TOPICS.contains( checkedGroup ))
                                MainActivity.TOPICS.set(position1, checkedGroup );
                        if (!cb.isChecked())
                            MainActivity.TOPICS.set(position1, "");

                        Log.d(TAG, "Clicked on Checkbox: " + cb.getText() +
                                " is " + cb.isChecked());
                        Log.d(TAG, TOPICS.toString());
                    }
                });

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.cb.setText(groups[position]);
            if (holder.cb.isChecked())
            // MainActivity.TOPICS includes selected groups. So, chec or uncheck "CheckBox" accordingly
            if (MainActivity.TOPICS.contains( holder.cb.getText() ))
                holder.cb.setChecked(true);
            else
                holder.cb.setChecked(false);

            // TODO: ADD EXTRA TextView TO SHOW MORE INFO ON TOPICS
            if (position == 0) {
                holder.tv.setTextColor(Color.RED);
                holder.tv.setText(" Preferé");
            }
            return convertView;

        }
    }
}
