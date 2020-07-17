package com.jandans.unityaddsgodot;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBannerSize;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnityAdsInterface extends GodotPlugin implements IUnityAdsListener, BannerView.IListener {

    private final String TAG = "UnityAdsInterface";
    private SignalInfo rnityAdsReady = new SignalInfo("UnityAdsReady");
    private SignalInfo unityAdsStart = new SignalInfo("UnityAdsStart");
    private SignalInfo unityAdsFinish = new SignalInfo("UnityAdsFinish", String.class, String.class);
    private SignalInfo unityAdsError = new SignalInfo("UnityAdsError", String.class);
    private SignalInfo unityBannerLoaded = new SignalInfo("UnityBannerLoaded");
    private SignalInfo unityBannerClick = new SignalInfo("UnityBannerClick");
    private SignalInfo unityBannerLeftApp = new SignalInfo("UnityBannerLeftApp");
    private SignalInfo unityBannerError = new SignalInfo("UnityBannerError", String.class);
    private BannerView bannerView = null;

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
                add(rnityAdsReady);
                add(unityAdsStart);
                add(unityAdsFinish);
                add(unityAdsError);
                add(unityBannerLoaded);
                add(unityBannerClick);
                add(unityBannerError);
                add(unityBannerLeftApp);
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
        final UnityBannerSize bannerSize = new UnityBannerSize(320, 50);
        hideBanner();
        bannerView = new BannerView(getActivity(), placementID, bannerSize);
        bannerView.setListener(this);
        bannerView.load();
    }

    public void hideBanner() {
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
    }

    @Override
    public void onUnityAdsReady(String s) {
        emitSignal(rnityAdsReady.getName());
    }

    @Override
    public void onUnityAdsStart(String s) {
        emitSignal(unityAdsStart.getName());
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

        emitSignal(unityAdsFinish.getName(), placement, String.format("%d", state));
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
        emitSignal(this.unityAdsError.getName(), s);
        Log.e(TAG, s);
    }
    //Banner stuff goes here
    @Override
    public void onBannerLoaded(BannerView bannerView) {
        emitSignal(unityBannerLoaded.getName());
    }

    @Override
    public void onBannerClick(BannerView bannerView) {
        emitSignal(unityBannerClick.getName());
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
        emitSignal(unityBannerError.getName(), bannerErrorInfo.errorMessage);
        Log.e(TAG, bannerErrorInfo.errorMessage);
    }

    @Override
    public void onBannerLeftApplication(BannerView bannerView) {
        emitSignal(unityBannerLeftApp.getName());
    }
}
