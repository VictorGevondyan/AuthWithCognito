package am.victor.authwithcognito;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 3000;

    Runnable splashScreenRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initSplashScreenRunnable();

        Handler splashScreenHandler = new Handler();
        splashScreenHandler.postDelayed( splashScreenRunnable, SPLASH_TIME_OUT );

    }

    private void initSplashScreenRunnable(){

        splashScreenRunnable = new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                // Auth0 instance which will be used to hold your account details.
                Auth0 account = new Auth0("KJILAoYYfPvYBWBbU6S0ZGyqMDl0RaGv", "captadorwifi.auth0.com");
                account.setLoggingEnabled(true);
                //Configure the account in OIDC conformant mode.
                account.setOIDCConformant(true);

                // Start Browser-based Auth0 login interface
                WebAuthProvider.init(account)
                        .withAudience("https://captadorwifi.auth0.com/userinfo")
                        .start(SplashScreenActivity.this, authCallback );

            }

        };

    }

    AuthCallback authCallback = new AuthCallback() {

        @Override
        public void onFailure(@NonNull Dialog dialog) {
            Log.d("Auth0 authent exc dial", "DIALOG");
        }

        @Override
        public void onFailure(AuthenticationException exception) {
            Log.d("Auth0 authent exception", exception.getDescription());
        }

        @Override
        public void onSuccess(@NonNull Credentials credentials) {

            finish();

            //Authenticated
            String token = credentials.getIdToken();

            Intent profileActivityIntent = new Intent( getApplicationContext(), ProfileActivity.class );
            profileActivityIntent.putExtra( Constants.TOKEN, token);
            startActivity(profileActivityIntent);

        }

    };

}
