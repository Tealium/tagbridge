//
//  TEALGoogleDFPAdID.h
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/7/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GoogleMobileAds/GoogleMobileAds.h>

@interface TEALGoogleDFPAdStore : NSObject

- (BOOL) addBannerView:(DFPBannerView*)bannerView;

- (BOOL) removeBannerView:(DFPBannerView *)bannerView;

- (NSArray*) bannerViewsForAdID:(NSString *)adID adUnitID:(NSString *)adUnitID;

- (BOOL) addInterstitialAd:(DFPInterstitial*)interstitial;

- (BOOL) removeInterstitial:(DFPInterstitial *)interstitial;

- (NSArray *) interstitialAdsForAdID:(NSString *)adID orAdUnitID:(NSString*)adUnitID;

- (BOOL) removeAllAdsWithAdID:(NSString *)adID adUnitID:(NSString *)adUnitID;

- (NSArray *) allAdsJSONReady;

@end
