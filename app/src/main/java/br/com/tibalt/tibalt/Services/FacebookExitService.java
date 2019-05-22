package br.com.tibalt.tibalt.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

public class FacebookExitService extends Service {
    public FacebookExitService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        LoginManager.getInstance().logOut();
//        super.onTaskRemoved(rootIntent);
//        this.stopSelf();
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, (graphResponse) -> {
            LoginManager.getInstance().logOut();
        }).executeAndWait();
        super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }
}
