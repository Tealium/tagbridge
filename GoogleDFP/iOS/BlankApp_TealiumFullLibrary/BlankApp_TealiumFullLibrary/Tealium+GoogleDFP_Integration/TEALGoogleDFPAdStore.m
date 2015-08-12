//
//  TEALGoogleDFPAdID.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/7/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "TEALGoogleDFPAdStore.h"
#import "GADBannerView+Tealium.h"
#import "GADInterstitial+Tealium.h"

@interface TEALGoogleDFPAdStore()

@property (nonatomic, strong) NSMutableSet *bannerAds;
@property (nonatomic, strong) NSMutableSet *interstitialAds;

@end

@implementation TEALGoogleDFPAdStore

- (instancetype) init {
    self = [super init];
    if (self){
        _bannerAds = [[NSMutableSet alloc] init];
        _interstitialAds = [[NSMutableSet alloc] init];
    }
    return self;
}

- (BOOL) addBannerView:(DFPBannerView*)bannerView{
    
    if ([self.bannerAds containsObject:bannerView]){
        return NO;
    }
    
    [self.bannerAds addObject:bannerView];
    
    return YES;
}

- (BOOL) removeBannerView:(DFPBannerView *)bannerView {
    
    if (![self.bannerAds containsObject:bannerView]){
        return NO;
    }
    
    [self.bannerAds removeObject:bannerView];
    return YES;
}

- (NSArray*) bannerViewsForAdID:(NSString *)adID adUnitID:(NSString *)adUnitID{
    
    if (!adID && !adUnitID){
        return [self.bannerAds allObjects];
    }
    
    NSMutableArray *bannerViews = [NSMutableArray array];
    
    [self.bannerAds enumerateObjectsUsingBlock:^(id obj, BOOL *stop) {
        DFPBannerView *bannerView = obj;
        if ([bannerView isKindOfClass:([DFPBannerView class])]){
            if ([adUnitID isEqualToString:bannerView.adUnitID ] ||
                [adID isEqualToString:[bannerView teal_adID]]){
                
                [bannerViews addObject:obj];
            }
        }
    }];

    return [NSArray arrayWithArray:bannerViews];
}

- (BOOL) addInterstitialAd:(DFPInterstitial*)interstitial {

    if ([self.interstitialAds containsObject:interstitial]){
        return NO;
    }
    
    [self.interstitialAds addObject:interstitial];
    
    return YES;
    
}

- (BOOL) removeInterstitial:(DFPInterstitial *)interstitial {
    
    if (![self.interstitialAds containsObject:interstitial]){
        return NO;
    }
    
    [self.interstitialAds removeObject:interstitial];
    return YES;
}

- (NSArray *) interstitialAdsForAdID:(NSString *)adID orAdUnitID:(NSString*)adUnitID {
    
    if (!adID && !adUnitID){
        return [self.interstitialAds allObjects];
    }
    
    NSMutableArray *interstitials = [NSMutableArray array];
    
    [self.interstitialAds enumerateObjectsUsingBlock:^(id obj, BOOL *stop) {
        DFPInterstitial *interstitial = obj;
        if ([interstitial isKindOfClass:([DFPInterstitial class])]){
            if ([adUnitID isEqualToString:interstitial.adUnitID ] ||
                [adID isEqualToString:[interstitial teal_adID]]){
                
                [interstitials addObject:obj];
            }
        }
    }];
    
    return [NSArray arrayWithArray:interstitials];
    
}

- (BOOL) removeAllAdsWithAdID:(NSString *)adID adUnitID:(NSString *)adUnitID {
    
    NSArray *banners = [self bannerViewsForAdID:adID adUnitID:adUnitID];
    NSArray *interstitials = [self interstitialAdsForAdID:adID orAdUnitID:adUnitID];
    
    __block BOOL response = NO;
    __weak TEALGoogleDFPAdStore *weakSelf = self;
    
    [banners enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        response = YES;
        DFPBannerView *bannerView = obj;
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([bannerView teal_isVisible]){
                [UIView animateWithDuration:0.5
                                 animations:^{
                                     bannerView.alpha = 0.0;
                                 } completion:^(BOOL finished) {
                                     [bannerView removeFromSuperview];
                                 }];
            }
            else {
                [bannerView removeFromSuperview];
            }
        });

        [weakSelf.bannerAds removeObject:obj];
    }];
    
    [interstitials enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        response = YES;
        
        [weakSelf.interstitialAds removeObject:obj];
    }];
    
    return response;
}

- (NSArray *) allAdsJSONReady {
    
    NSArray *allAds = [self allAds];
    NSMutableArray *mArray = [NSMutableArray array];
    
    for (id Ad in allAds) {
        if ([Ad isKindOfClass:([DFPBannerView class])] ||
            [Ad isKindOfClass:([DFPInterstitial class])]){
            
            [mArray addObject:[Ad teal_json_output]];
            
        }
    }
    
    return [NSArray arrayWithArray:mArray];
    
}

- (NSArray *) allAds {
    
    NSMutableArray *ads = [NSMutableArray array];
    
    [ads addObjectsFromArray:[self bannerViewsForAdID:nil adUnitID:nil]];
    [ads addObjectsFromArray:[self interstitialAdsForAdID:nil orAdUnitID:nil]];
    
    return ads;
    
}

@end
