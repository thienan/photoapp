package org.quantum.silverphoto.share.tumblr;

import android.os.AsyncTask;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.User;

import org.quantum.silverphoto.data.local.AccountDatabase;
import org.quantum.silverphoto.utilities.BasicCallBack;
import org.quantum.silverphoto.utilities.Constants;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.quantum.silverphoto.data.local.AccountDatabase.AccountName.TUMBLR;

/**
 * Created by SilverLive Team on 22/7/17.
 */

public class TumblrClient {
    private User user;
    private JumblrClient client;
    BasicCallBack nameCallBack;
    public TumblrClient() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
        query.equalTo("name", TUMBLR.toString());
        final RealmResults<AccountDatabase> result = query.findAll();
        if (result.size() != 0) {
            client = new JumblrClient(Constants.TUMBLR_CONSUMER_KEY, Constants.TUMBLR_CONSUMER_SECRET);
            client.setToken(result.get(0).getToken(), result.get(0).getSecret());
        }
    }

    public JumblrClient getClient() {
        return client;
    }

    public void getName(BasicCallBack basicCallBack) {
        this.nameCallBack = basicCallBack;
        new getUser().execute();
    }

    private class getUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            user = client.user();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            nameCallBack.callBack(Constants.SUCCESS,user.getName());
        }
    }

}

