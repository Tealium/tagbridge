//
//  TEALGoogleDFPRemoteCommands.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/6/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "TEALGoogleDFPRemoteCommands.h"
#import <TealiumLibrary/Tealium.h>
#import "TEALGoogleDFPAdStore.h"
#import "GADBannerView+Tealium.h"
#import "GADInterstitial+Tealium.h"
#import "TEALGoogleDFPConstants.h"

@interface TEALGoogleDFPRemoteCommands()

@property (nonatomic, strong) TEALGoogleDFPAdStore *store;

@end
@implementation TEALGoogleDFPRemoteCommands

#pragma mark - PUBLIC METHODS

+ (instancetype) sharedInstance {
    static dispatch_once_t onceToken = 0;
    __strong static TEALGoogleDFPRemoteCommands *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[TEALGoogleDFPRemoteCommands alloc] init];
    });
    
    return _sharedObject;
}

- (void) enable {
    __weak TEALGoogleDFPRemoteCommands *weakSelf = self;
    
    [Tealium addRemoteCommandId:@"google_dfp"
                    description:@"google_dfp"
                    targetQueue:self.dispatchQueue
                          block:^(TealiumRemoteCommandResponse *response) {
                             
                              NSDictionary *payload = response.requestPayload;
                              NSString *dfpCommand = payload[@"command"];
                              
                              if ([dfpCommand isEqualToString:COMMAND_CREATE_BANNER_AD]){
                                  [weakSelf createBannerAdWithResponse:response];
                              }
                              else if ([dfpCommand isEqualToString:COMMAND_CREATE_INTERSTITIAL_AD]){
                                  [weakSelf createInterstitialAdWithResponse:response];
                              }
                              else if ([dfpCommand isEqualToString:COMMAND_GET_ADS]){
                                  [weakSelf getAdsWithResponse:response];
                              }
                              else if ([dfpCommand isEqualToString:COMMAND_SHOW_INTERSTITIAL_AD]){
                                  [weakSelf showInterstitialAdWithResponse:response];
                              }
                              else if ([dfpCommand isEqualToString:COMMAND_REMOVE_AD]) {
                                  [weakSelf removeAdWithResponse:response];
                              }
                              else {
                                  response.body = [NSString stringWithFormat:@"Method %@ not found.", dfpCommand];
                                  response.status = TealiumRC_Failure;
                              }
                              
                          }];
}

#pragma mark - PRIVATE INSTANCE METHODS

- (instancetype) init {
    self = [super init];
    if (self){
        _dispatchQueue = dispatch_queue_create("com.tealium.google.dfp", NULL);
        _store = [[TEALGoogleDFPAdStore alloc] init];
    }
    
    return self;
}

- (void) createBannerAdWithResponse:(TealiumRemoteCommandResponse*)response {
        NSLog(@"%s ", __FUNCTION__);
    
    NSDictionary *payload = response.requestPayload;
    __block NSString *adUnitID = payload[KEY_AD_UNIT_ID];
    
    if (!adUnitID){
        //TODO: Error reporting
        response.body = @"No Ad Unit Id passed in from request payload.";
        response.status = TealiumRC_Malformed;
        [response send];
        return;
    }
    if (!self.activeViewController) {
        // TODO: add error details
        response.status = STATUS_NO_VIEW;
        [response send];
        return;
    }
    
    __block NSString *adID = payload[KEY_AD_ID];
    __block NSArray *testDevices = payload[KEY_TEST_DEVICES];
    __block NSArray *bannerAdSizesStrings = payload[KEY_BANNER_AD_SIZES];
    
    __weak TEALGoogleDFPRemoteCommands *weakSelf = self;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        NSArray *bannerSizes = [weakSelf bannerAdSizesFromArrayOfStrings:bannerAdSizesStrings];
        
        if ([bannerSizes count] == 0) {
            response.body = @"No valid banner sizes received from request payload";
            response.status = TealiumRC_Malformed;
            [response send];
            return;
        }
        
        NSValue *value = bannerSizes[0];
        
        GADAdSize size = GADAdSizeFromNSValue(value);
        DFPBannerView *bannerView = [[DFPBannerView alloc] initWithAdSize:size];
        
        [bannerView teal_setAdID:adID];
        bannerView.validAdSizes = bannerSizes;
        bannerView.adUnitID = adUnitID;
        bannerView.adSizeDelegate = weakSelf;
        bannerView.appEventDelegate = weakSelf;
        bannerView.delegate = weakSelf;
        bannerView.rootViewController = weakSelf.activeViewController;
        
        DFPRequest *request = [DFPRequest request];
        request.testDevices = testDevices;

        [bannerView loadRequest:request];
        
        [weakSelf.store addBannerView:bannerView];
        
        response.status = TealiumRC_Success;
        [response send];
    });

    
}

- (void) createInterstitialAdWithResponse:(TealiumRemoteCommandResponse*)response {
        NSLog(@"%s ", __FUNCTION__);
    
    NSDictionary *payload = response.requestPayload;
    __block NSString *adUnitID = payload[KEY_AD_UNIT_ID];
    
    if (!adUnitID){
        //TODO: Error reporting
        response.body = @"No Ad Unit Id passed in from request payload.";
        response.status = TealiumRC_Malformed;
        [response send];
        return;
    }
    
    __block NSString *adID = payload[KEY_AD_ID];
    __block NSArray *testDevices = payload[KEY_TEST_DEVICES];
    
    __weak TEALGoogleDFPRemoteCommands *weakSelf = self;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        DFPInterstitial *interstitial = [[DFPInterstitial alloc] initWithAdUnitID:adUnitID];
        [interstitial teal_setAdID:adID];
        interstitial.adUnitID = adUnitID;
        interstitial.delegate = weakSelf;
        interstitial.appEventDelegate = weakSelf;
        
        // Not supported
        interstitial.customRenderedInterstitialDelegate = nil;
        
        DFPRequest *request = [DFPRequest request];
        request.testDevices = testDevices;
        [interstitial loadRequest:request];
        
        [interstitial teal_setStatus:STATUS_STRING_CREATED];
        [weakSelf.store addInterstitialAd:interstitial];
        
        response.status = TealiumRC_Success;
        [response send];
    });
}

- (void) getAdsWithResponse:(TealiumRemoteCommandResponse*)response {
    NSLog(@"%s ", __FUNCTION__);
    
    NSString *jsonString = @"";
    NSArray *allAds = [self.store allAdsJSONReady];
    
    response.status = TealiumRC_Malformed;

    if ([NSJSONSerialization isValidJSONObject:allAds]) {
        
        NSError *error = [[NSError alloc] init];
        
        @autoreleasepool {
            NSData *jsonData = [NSJSONSerialization dataWithJSONObject:allAds options:0 error:&error];
            
            if (jsonData != nil) {
                jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
                response.status = TealiumRC_Success;
            }
        }
    }
    
    response.body = jsonString;
    [response send];
    
}

- (void) showInterstitialAdWithResponse:(TealiumRemoteCommandResponse*)response {
        NSLog(@"%s ", __FUNCTION__);
    
    NSDictionary *payload = response.requestPayload;
    NSString *adID = payload[KEY_AD_ID];
    NSString *adUnitID = payload[KEY_AD_UNIT_ID];
    
    NSArray *interstitials = [self.store interstitialAdsForAdID:adID orAdUnitID:adUnitID];
    
    DFPInterstitial *interstitial = interstitials[0];
    if (!interstitial) {
        response.body = @"Interstitial ad requested to show has not yet been created.";
        response.status = STATUS_AD_NOT_FOUND;
    }
    else if (!self.activeViewController) {
        response.body = @"No active view controller to display interstitial ad";
        response.status = STATUS_NO_VIEW;
    }
    else if (!interstitial.isReady){
        response.body = @"Interstitial ad not ready to display";
        response.status = STATUS_AD_NOT_READY;
    }
    else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [interstitial teal_setStatus:STATUS_STRING_OPENED];
            [interstitial presentFromRootViewController:self.activeViewController];
        });
        response.status = TealiumRC_Success;
    }
    [response send];
}

- (void) removeAdWithResponse:(TealiumRemoteCommandResponse*)response {
        NSLog(@"%s ", __FUNCTION__);
    
    NSString *adID = response.requestPayload[KEY_AD_ID];
    NSString *adUnitID = response.requestPayload[KEY_AD_UNIT_ID];
    
    BOOL adRemoved = [self.store removeAllAdsWithAdID:adID adUnitID:adUnitID];
    
    if (!adRemoved){
        response.status = STATUS_ALREADY_REMOVED;
    }
    else {
        response.status = TealiumRC_Success;
    }
    
    [response send];
}

#pragma mark - PRIVATE HELPERS

- (GADAdSize) firstAdSizeFromStringArray:(NSArray*)array {
    NSString *string = array[0];
    if ([string isKindOfClass:([NSString class])]){
        if ([string isEqualToString:BANNER]){
            return kGADAdSizeBanner;
        }
        else if ([string isEqualToString:BANNER_LARGE]){
            return kGADAdSizeLargeBanner;
        }
        else if ([string isEqualToString:BANNER_MEDIUM_RECTANGLE]){
            return kGADAdSizeMediumRectangle;
        }
        else if ([string isEqualToString:BANNER_FULL]){
            return kGADAdSizeFullBanner;
        }
        else if ([string isEqualToString:BANNER_LEADERBOARD]){
            return kGADAdSizeLeaderboard;
        }
        else if ([string isEqualToString:BANNER_SMART]){
            // TODO: switch depending on device orientation
            
            UIDeviceOrientation deviceOrientation = [[UIDevice currentDevice] orientation];
            if (deviceOrientation == UIDeviceOrientationLandscapeLeft || deviceOrientation == UIDeviceOrientationLandscapeRight ) {
                return kGADAdSizeSmartBannerLandscape;
            }
            else if (deviceOrientation == UIDeviceOrientationPortrait || deviceOrientation == UIDeviceOrientationPortraitUpsideDown){
                return kGADAdSizeSmartBannerPortrait;
            }
            
        }
        else {
            // TODO: ERROR
            return kGADAdSizeInvalid;
        }
    }
    return kGADAdSizeInvalid;
}

- (NSArray *) bannerAdSizesFromArrayOfStrings:(NSArray*)array {
    NSMutableArray *adSizes = [NSMutableArray array];
    
    for (NSString *string in array) {
        if ([string isEqualToString:BANNER]){
            [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeBanner)];
        }
        else if ([string isEqualToString:BANNER_LARGE]){
            [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeLargeBanner)];
        }
        else if ([string isEqualToString:BANNER_MEDIUM_RECTANGLE]){
            [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeMediumRectangle)];
            
        }
        else if ([string isEqualToString:BANNER_FULL]){
            [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeFullBanner)];
            
        }
        else if ([string isEqualToString:BANNER_LEADERBOARD]){
            [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeLeaderboard)];
            
        }
        else if ([string isEqualToString:BANNER_SMART]){
            // TODO: switch depending on device orientation
            
            UIDeviceOrientation deviceOrientation = [[UIDevice currentDevice] orientation];
            if (deviceOrientation == UIDeviceOrientationLandscapeLeft || deviceOrientation == UIDeviceOrientationLandscapeRight ) {
                
                [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeSmartBannerLandscape)];
            }
            else if (deviceOrientation == UIDeviceOrientationPortrait || deviceOrientation == UIDeviceOrientationPortraitUpsideDown){
                
                [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeSmartBannerPortrait)];
            }
            
        }
        else {
            // TODO: ERROR
        }
    }
    return [NSArray arrayWithArray:adSizes];
    
}

#pragma mark - ERROR HANDLING

//- (NSError *) error

#pragma mark - GADAdSize + GADAppEvent Delegates

// !!!: GADAdSizeDelegate
- (void)adView:(DFPBannerView *)view willChangeAdSizeTo:(GADAdSize)size {
    NSLog(@"%s ", __FUNCTION__);
}

// !!!: GADAppEventDelegate
- (void)adView:(GADBannerView *)banner
didReceiveAppEvent:(NSString *)name
      withInfo:(NSString *)info{
    NSLog(@"%s ", __FUNCTION__);
    
    
}

#pragma mark - GADInterstitial Delegates

/// Called when the interstitial receives an app event.
- (void)interstitial:(GADInterstitial *)interstitial
  didReceiveAppEvent:(NSString *)name
            withInfo:(NSString *)info{
    NSLog(@"%s ", __FUNCTION__);
    
}

- (void)interstitial:(GADInterstitial *)ad didFailToReceiveAdWithError:(GADRequestError *)error{
    NSLog(@"%s ERROR: %@", __FUNCTION__, [error localizedDescription]);
    
    [ad teal_setStatus:STATUS_STRING_FAILEDTOLOAD];
}

- (void)interstitialDidDismissScreen:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
    
    [ad teal_setStatus:STATUS_STRING_CLOSED];
}

- (void)interstitialDidReceiveAd:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
}

- (void)interstitialWillDismissScreen:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
    
    [ad teal_setStatus:STATUS_STRING_CLOSED];
}

- (void)interstitialWillLeaveApplication:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
    
    [ad teal_setStatus:STATUS_STRING_LEFTAPPLICATION];
}

- (void)interstitialWillPresentScreen:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
}

#pragma mark - GADBannerView Delegates

// !!!: GADBannerViewDelegate
- (void)adViewDidReceiveAd:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    
    dispatch_async(dispatch_get_main_queue(), ^{
    
        UIView *view = self.activeViewController.view;
        CGFloat viewH = view.frame.size.height;
        CGFloat bannerH = bannerView.frame.size.height;
        CGFloat bannerW = bannerView.frame.size.width;
        
        [UIView animateWithDuration:0.5 animations:^{
            
            bannerView.frame = CGRectMake(0.0,
                                          viewH - bannerH,
                                          bannerW,
                                          bannerH);
            [self.activeViewController.view addSubview:bannerView];
        }];
    });
}

- (void)adView:(DFPBannerView *)bannerView
didFailToReceiveAdWithError:(GADRequestError *)error{
    NSLog(@"%s ERROR:%@ ", __FUNCTION__, [error localizedDescription]);
    
    [bannerView teal_setStatus:STATUS_STRING_FAILEDTOLOAD];
    
}

- (void)adViewWillPresentScreen:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    [bannerView teal_setStatus:STATUS_STRING_OPENED];
}

- (void)adViewDidDismissScreen:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    [bannerView teal_setStatus:STATUS_STRING_CLOSED];
}
- (
   void)adViewWillDismissScreen:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    
}

- (void)adViewWillLeaveApplication:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    [bannerView teal_setStatus:STATUS_STRING_LEFTAPPLICATION];
}


@end
