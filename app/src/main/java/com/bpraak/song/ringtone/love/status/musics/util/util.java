package com.bpraak.song.ringtone.love.status.musics.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bpraak.song.ringtone.love.status.musics.BuildConfig;
import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.activity.MainActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.io.File;

public class util {

    public static AdRequest AdRequest() {
        return new AdRequest.Builder().build();
    }
    public static void showBannerAd(Activity context) {
        try {
            MobileAds.initialize(context, BuildConfig.appid);
            AdView mAdView = context.findViewById(R.id.adView);
            mAdView.loadAd(AdRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void showBannerAd2(Activity context) {
        try {
            MobileAds.initialize(context, BuildConfig.appid);
            AdView mAdView = context.findViewById(R.id.adView2);
            mAdView.loadAd(AdRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInterastial(Activity activity) {
        try {
            MobileAds.initialize(activity, BuildConfig.appid);
            final InterstitialAd mInterstitialAd = new InterstitialAd(activity);
            mInterstitialAd.setAdUnitId(BuildConfig.interastial);
            mInterstitialAd.loadAd(AdRequest());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static boolean isConnected(Context mContext) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage()+"");
        }
        return connected;
    }


    public static void nativead(final View view) {

        final AdLoader adLoader = new AdLoader.Builder(view.getContext(), BuildConfig.nativeads)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.

                        // This method sets the text, images and the native ad, etc into the ad
                        // view.
                        populateNativeAdView(unifiedNativeAd,view);
//                        if (adLoader.isLoading()) {
//                            // The AdLoader is still loading ads.
//                            // Expect more adLoaded or onAdFailedToLoad callbacks.
//                        } else {
//                            // The AdLoader has finished loading ads.
//                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(AdRequest());
    }

    private static void populateNativeAdView(UnifiedNativeAd nativeAd,
                                             View view) {
        UnifiedNativeAdView adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    public static String saveImagePath(Activity activity) {
        return activity.getExternalFilesDir(null)
                + File.separator + activity.getString(R.string.app_name)+ File.separator + activity.getString(R.string.image);
    }

    public static void showNetworkDialog(final Context mContext) {
        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
            builder1.setMessage("No internet connection, please check your mobile internet connection.");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            // Redirect to root class
                            Intent i = new Intent(mContext, MainActivity.class);
                            // set the new task and clear flags
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(i);
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
