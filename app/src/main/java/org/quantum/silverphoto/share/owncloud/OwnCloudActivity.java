package org.quantum.silverphoto.share.owncloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;

import org.quantum.silverphoto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnCloudActivity extends AppCompatActivity implements Button.OnClickListener  {

    private static final int RESULT_OK = 1;

    @BindView(R.id.hostUrlInput)
    EditText hostUrlInput;

    @BindView(R.id.input_layout_hostUrl)
    TextInputLayout hostUrlLayout;

    @BindView(R.id.account_username)
    EditText accountUsername;

    @BindView(R.id.input_layout_account_username)
    TextInputLayout usernameLayout;

    @BindView(R.id.account_password)
    EditText accountPassword;

    @BindView(R.id.input_layout_account_password)
    TextInputLayout passwordLayout;

    @BindView(R.id.buttonOK)
    Button buttonOK;

    private OwnCloudClient authClient;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_cloud);
        ButterKnife.bind(this);
        username = accountUsername.getText().toString();
        password = accountUsername.getText().toString();
        buttonOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!validateHosturl()){
            return;
        } else if (!validateUsername()){
            return;
        } else if (!validatePassword()){
            return;
        } else{
            Intent loginIntent = new Intent();
            Uri serverUri = Uri.parse(hostUrlInput.getText().toString().trim());
            loginIntent.putExtra(getString(R.string.server_url)
                    , hostUrlInput.getText().toString().trim());
            loginIntent.putExtra(getString(R.string.auth_username),
                    accountUsername.getText().toString().trim());
            loginIntent.putExtra(getString(R.string.auth_password),
                    accountPassword.getText().toString().trim());
            authClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, this, true);
            authClient.setCredentials(
                    OwnCloudCredentialsFactory.newBasicCredentials(username, password)
            );

            setResult(RESULT_OK, loginIntent);
            finish();
        }
    }

    private boolean validateHosturl() {
        String email = hostUrlInput.getText().toString().trim();

        if (email.isEmpty()) {
            hostUrlLayout.setError(getString(R.string.err_msg_host));
            return false;
        } else if (!(email.contains("http://") || email.contains("https://"))){
            hostUrlLayout.setError("Please add the protocol http/ https");
            return false;
        } else {
            hostUrlLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateUsername() {
        if (accountUsername.getText().toString().trim().isEmpty()) {
            usernameLayout.setError(getString(R.string.err_msg_username));
            return false;
        } else {
            usernameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (accountPassword.getText().toString().trim().isEmpty()) {
            passwordLayout.setError(getString(R.string.err_msg_password));
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Starts and activity to open the 'new account' page in the ownCloud web site
     *
     * @param view 'Account register' button
     */
    public void onRegisterClick(View view) {
        Intent register = new Intent(
                Intent.ACTION_VIEW, Uri.parse(getString(R.string.welcome_link_url))
        );
        setResult(RESULT_CANCELED);
        startActivity(register);
    }
}
