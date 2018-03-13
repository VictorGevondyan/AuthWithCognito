package am.victor.authwithcognito;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    String token;

    CognitoCachingCredentialsProvider credentialsProvider;

    View.OnClickListener submitButtonClickListener;

    Runnable runnable;

    EditText datasetEditText;
    EditText dataKeyEditText;
    EditText dataValueEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initOnClickListener();
        initRunnable();

        Intent profileActivityIntent = getIntent();
        token = profileActivityIntent.getStringExtra( Constants.TOKEN );

        Button submitButton = findViewById( R.id.submit_button );
        submitButton.setOnClickListener( submitButtonClickListener );

    }

    private void initOnClickListener(){

        submitButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                datasetEditText = findViewById(R.id.dataset);
                dataKeyEditText = findViewById(R.id.data_key);
                dataValueEditText = findViewById(R.id.data_value);

                // Initialize the Amazon Cognito credentials provider
                credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:81c4342a-f483-4405-b9e4-61f50cde0ac9", // Identity pool ID
                        Regions.US_EAST_1 // Region
                );

                Map<String, String> logins = new HashMap<String, String>();
                logins.put("captadorwifi.auth0.com", token);
                credentialsProvider.setLogins(logins);

                CognitoSyncManager syncClient = new CognitoSyncManager(
                        getApplicationContext(),
                        Regions.US_EAST_1,
                        credentialsProvider);

                // Create a record in a dataset and synchronize with the server
                com.amazonaws.mobileconnectors.cognito.Dataset dataset = syncClient
                        .openOrCreateDataset(datasetEditText.getText().toString());


                dataset.put(dataKeyEditText.getText().toString(), dataValueEditText.getText().toString());
                dataset.synchronize(new DefaultSyncCallback() {

                    @Override
                    public void onSuccess(com.amazonaws.mobileconnectors.cognito.Dataset dataset, List newRecords) {
                        // We cannot display Toast on non-ui thread. So we do such a trick.
                        runOnUiThread(runnable);
                    }

                });

            }
        };

    }

    private void initRunnable() {

        runnable = new Runnable() {

            public void run() {

                datasetEditText.setText("");
                dataKeyEditText.setText("");
                dataValueEditText.setText("");

                Toast.makeText(getApplicationContext(), "Your data uploaded to AWS", Toast.LENGTH_LONG).show();

            }

        };


    }


}
