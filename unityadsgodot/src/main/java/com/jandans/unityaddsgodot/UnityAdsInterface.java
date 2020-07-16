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

public class UnityAdsInterface extends GodotPlugin implements IUnityAdsListener, IUnityBannerListener {

    private final String TAG = "UnityAdsInterface";
    private SignalInfo UnityAdsReady = new SignalInfo("UnityAdsReady");
    private SignalInfo UnityAdsStart = new SignalInfo("UnityAdsStart");
    private SignalInfo UnityAdsFinish = new SignalInfo("UnityAdsFinish", String.class, String.class);
    private SignalInfo UnityAdsError = new SignalInfo("UnityAdsError", String.class);
    private SignalInfo UnityBannerLoaded = new SignalInfo("UnityBannerLoaded");
    private SignalInfo UnityBannerUnloaded = new SignalInfo("UnityBannerUnloaded");
    private SignalInfo UnityBannerShow = new SignalInfo("UnityBannerShow");
    private SignalInfo UnityBannerClick = new SignalInfo("UnityBannerClick");
    private SignalInfo UnityBannerHide = new SignalInfo("UnityBannerHide");
    private SignalInfo UnityBannerError = new SignalInfo("UnityBannerError", String.class);

    public UnityAdsInterface(Godot godot) {
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
        return new ArrayList<String>() {
            {
                add("initialise");
                add("showBanner");
                add("loadAd");
                add("show");
                add("hideBanner");
                add("isReady");
            }
        };
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        return new HashSet<SignalInfo>() {
            {
                add(UnityAdsReady);
                add(UnityAdsStart);
                add(UnityAdsFinish);
                add(UnityAdsError);
                add(UnityBannerLoaded);
                add(UnityBannerUnloaded);
                add(UnityBannerShow);
                add(UnityBannerHide);
                add(UnityBannerError);
            }
        };
    }

    public void initialise(String appId, boolean testMode)
    {
        try
        {
            UnityAds.initialize(getActivity(), appId, testMode);
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
                UnityAds.show(getActivity(), placementId);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    UnityBanners.setBannerPosition(BannerPosition.BOTTOM_CENTER);
                    UnityBanners.setBannerListener(Listener);
                    UnityBanners.loadBanner(getActivity(), placementID);
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
    public void onUnityAdsFinish(String placement, UnityAds.FinishState finishState) {
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
            Log.e(TAG, placement);
            state = 0;
        }

        emitSignal(UnityAdsFinish.getName(), placement, String.format("%d", state));
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
        hideBanner();
    }
}
