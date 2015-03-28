package au.wsit.ifconfig.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;


public class Settings extends ActionBarActivity {

    EditText WAN_IP_GET_METHOD;
    TextView LOG_OUTPUT;
    int logger_count = 0;

    MainActivity getMain = new MainActivity();
    //WriteFile getIP = new WriteFile();

    CheckBox mNotificationCheckBox;

    boolean checkBoxState;
    boolean checkBoxSavedState;
    SharedPreferences mSharedPreferences;

    public static final String TAG = Settings.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        WAN_IP_GET_METHOD = (EditText) findViewById(R.id.WAN_IP_EditText_ID);
        mNotificationCheckBox = (CheckBox) findViewById(R.id.notification_checkBox);
        LOG_OUTPUT = (TextView) findViewById(R.id.ID_LOG);

        WAN_IP_GET_METHOD.setText(mSharedPreferences.getString(ifconfigConstants.KEY_DYN_URL, null));



        // Check if we have already saved the checkbox state
        checkBoxSavedState = mSharedPreferences.getBoolean(ifconfigConstants.KEY_NOTIFICATIONS_ENABLED, true);

        mNotificationCheckBox.setChecked(checkBoxSavedState);

        mNotificationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mNotificationCheckBox.isChecked())
                {
                    Toast.makeText(Settings.this, "Notifications enabled", Toast.LENGTH_LONG).show();

                    // Turn on notifications
                    mSharedPreferences.edit().putBoolean(ifconfigConstants.KEY_NOTIFICATIONS_ENABLED, true).apply();

                }
                else
                {
                    Toast.makeText(Settings.this, "Notifications disabled", Toast.LENGTH_LONG).show();

                    // Turn off notifications
                    mSharedPreferences.edit().putBoolean(ifconfigConstants.KEY_NOTIFICATIONS_ENABLED, false).apply();
                }

            }
        });


    }



    public void SaveButton(View view)
    {

        mSharedPreferences.edit().putString(ifconfigConstants.KEY_DYN_URL, WAN_IP_GET_METHOD.getText().toString()).apply();
        ToastMsg("Saved");

    }

    public void TestButton(View view)
    {
        //WriteFile READ_DATA = new WriteFile();
        //String val = READ_DATA.readData("URL.txt");

        new DownloadWebpageTask().execute(WAN_IP_GET_METHOD.getText().toString());

    }

    public void logger(String msg)
    {
        if (logger_count > 18)
        {
            LOG_OUTPUT.setText(msg + "\n");
        }
        msg = msg.toString();
        LOG_OUTPUT.append(msg + "\n");
        logger_count++;
    }

    public void ToastMsg(String msg)
    {
        int duration = Toast.LENGTH_LONG;
        Context context = getApplicationContext();
        CharSequence text = msg;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return getMain.downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Error getting IP";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            ToastMsg("WAN IP: " + result);
        }
    }


}
