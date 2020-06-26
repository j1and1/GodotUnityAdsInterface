package com.jandans.unityaddsgodot;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnityAddsInterface extends GodotPlugin implements IUnityAdsListener, IUnityBannerListener {

    private final String TAG = "UnityAddsInterface";
    private SignalInfo UnityAdsReady = new SignalInfo("UnityAdsReady");
    private SignalInfo UnityAdsStart = new SignalInfo("UnityAdsStart");
    private SignalInfo UnityAdsFinish = new SignalInfo("UnityAdsFinish",  String.class);
    private SignalInfo UnityAdsError = new SignalInfo("UnityAdsError", String.class);
    private SignalInfo UnityBannerLoaded = new SignalInfo("UnityBannerLoaded");
    private SignalInfo UnityBannerUnloaded = new SignalInfo("UnityBannerUnloaded");
    private SignalInfo UnityBannerShow = new SignalInfo("UnityBannerShow");
    private SignalInfo UnityBannerClick = new SignalInfo("UnityBannerClick");
    private SignalInfo UnityBannerHide = new SignalInfo("UnityBannerHide");
    private SignalInfo UnityBannerError = new SignalInfo("UnityBannerError", String.class);

    Activity baseActivity = null;

    public UnityAddsInterface(Godot godot) {
        super(godot);
    }

    @androidx.annotation.NonNull
    @Override
    public String getPluginName() {
        return "UnityAdsGodot";
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        List<String> retVal = new ArrayList<String>();
        retVal.add("initialise");
        retVal.add("showBanner");
        retVal.add("loadAd");
        retVal.add("show");
        retVal.add("hideBanner");
        retVal.add("isReady");
        return retVal;
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> retVal = new HashSet<SignalInfo>();
        retVal.add(UnityAdsReady);
        retVal.add(UnityAdsStart);
        retVal.add(UnityAdsFinish);
        retVal.add(UnityAdsError);
        retVal.add(UnityBannerLoaded);
        retVal.add(UnityBannerUnloaded);
        retVal.add(UnityBannerShow);
        retVal.add(UnityBannerHide);
        retVal.add(UnityBannerError);
        return retVal;
    }

    @Nullable
    @Override
    public View onMainCreateView(Activity activity) {
        baseActivity = activity;
        return super.onMainCreateView(activity);
    }

    public void initialise(String appId, boolean testMode)
    {
        try
        {
            UnityAds.initialize(baseActivity, appId, testMode);
            UnityAds.addListener(this);
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    public boolean isReady(String placementId) {
        return UnityAds.isReady(placementId);
    }

    public void loadAd(String placementId)
    {
        UnityAds.load(placementId);
    }

    public boolean show(String placementId)
    {
        if (UnityAds.isReady())
        {
            try
            {
                UnityAds.show(baseActivity, placementId);
            }
            catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage());
                return false;
            }
            return true;
        }
        else
        {
            Log.i(TAG, "Adds not ready");
            return false;
        }
    }

    public void showBanner(final String placementID) {
        final IUnityBannerListener Listener = this;
        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    UnityBanners.setBannerPosition(BannerPosition.BOTTOM_CENTER);
                    UnityBanners.setBannerListener(Listener);
                    UnityBanners.loadBanner(baseActivity, placementID);
                }
                catch (Exception ex)
                {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });
    }

    public void hideBanner() {
        try
        {
            UnityBanners.destroy();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onUnityAdsReady(String s) {
        emitSignal(UnityAdsReady.getName());
    }

    @Override
    public void onUnityAdsStart(String s) {
        emitSignal(UnityAdsStart.getName());
    }

    @Override
    public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
        int state = -1;

        // Implement conditional logic for each ad completion status:
        if (finishState == UnityAds.FinishState.COMPLETED) {
            // Reward the user for watching the ad to completion.
            state = 2;
        } else if (finishState == UnityAds.FinishState.SKIPPED) {
            // user skipped the ad.
            state = 1;
        } else if (finishState == UnityAds.FinishState.ERROR) {
            // Log an error.
            Log.e(TAG, s);
            state = 0;
        }

        emitSignal(UnityAdsFinish.getName(), s.toString());
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
        emitSignal(UnityAdsError.getName(), s);
        Log.e(TAG, s);
    }
    //Banner stuff goes here
    @Override
    public void onUnityBannerLoaded(String s, View view) {
        emitSignal(UnityBannerLoaded.getName());
    }

    @Override
    public void onUnityBannerUnloaded(String s) {
        emitSignal(UnityBannerUnloaded.getName());
    }

    @Override
    public void onUnityBannerShow(String s) {
        emitSignal(UnityBannerShow.getName());
    }

    @Override
    public void onUnityBannerClick(String s) {
        emitSignal(UnityBannerClick.getName());
    }

    @Override
    public void onUnityBannerHide(String s) {
        emitSignal(UnityBannerHide.getName(), s);
    }

    @Override
    public void onUnityBannerError(String s) {
        emitSignal(UnityBannerError.getName(), s);
        Log.e(TAG, s);
    }
}
