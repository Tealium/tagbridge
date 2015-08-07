//
//  TEALGoogleDFPRemoteCommands.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/6/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "TEALGoogleDFPRemoteCommands.h"
#import <TealiumLibrary/Tealium.h>

NSString *const BANNER = @"BANNER";
NSString *const BANNER_FULL = @"FULL_BANNER";
NSString *const BANNER_LARGE = @"LARGE_BANNER";
NSString *const BANNER_LEADERBOARD = @"LEADERBOARD";
NSString *const BANNER_MEDIUM_RECTANGLE = @"MEDIUM_RECTANGLE";
NSString *const BANNER_SMART = @"SMART_BANNER";

NSString *const COMMAND_CREATE_BANNER_AD = @"create_banner_ad";
NSString *const COMMAND_CREATE_INTERSTITIAL_AD = @"create_interstitial_ad";
NSString *const COMMAND_SHOW_INTERSTITIAL_AD =  @"show_interstitial_ad";
NSString *const COMMAND_GET_ADS = @"get_ads";
NSString *const COMMAND_REMOVE_AD = @"remove_ad";

NSString *const KEY_AD_ID = @"ad_id";
NSString *const KEY_AD_UNIT_ID = @"ad_unit_id";
NSString *const KEY_BANNER_ANCHOR = @"banner_anchor";
NSString *const KEY_BANNER_AD_SIZES = @"banner_ad_sizes";
NSString *const KEY_BIRTHDAY = @"birthday";
NSString *const KEY_CATEGORY_EXCLUSIONS = @"category_exclusions";
NSString *const KEY_CUSTOM_TARGETING = @"custom_targeting";
NSString *const KEY_GENDER = @"gender";
NSString *const KEY_KEYWORDS = @"keywords";
NSString *const KEY_LOCATION = @"location";
NSString *const KEY_MANUAL_IMPRESSIONS = @"manual_impressions";
NSString *const KEY_PUBLISHER_PROVIDED_ID = @"publisher_provided_id";
NSString *const KEY_REQUEST_AGENT = @"request_agent";
NSString *const KEY_TAG_FOR_CHILD_DIRECTED_TREATMENT = @"tag_for_child_directed_treatment";
NSString *const KEY_TEST_DEVICES = @"test_devices";

NSUInteger const STATUS_NO_VIEW = 418;
NSUInteger const STATUS_INCOMPATIBLE = 419;
NSUInteger const STATUS_AD_NOT_FOUND = 420;
NSUInteger const STATUS_ALREADY_REMOVED = 421;
NSUInteger const STATUS_AD_NOT_READY = 422;

@interface TEALGoogleDFPRemoteCommands()

@property (nonatomic, strong) NSMutableDictionary *bannerAds;
@property (nonatomic, strong) NSMutableDictionary *interstitialAds;

@end
@implementation TEALGoogleDFPRemoteCommands

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken = 0;
    __strong static TEALGoogleDFPRemoteCommands *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[TEALGoogleDFPRemoteCommands alloc] init];
    });
    
    return _sharedObject;
}

- (instancetype) init {
    self = [super init];
    if (self){
        _dispatchQueue = dispatch_queue_create("com.tealium.google.dfp", NULL);
        _bannerAds = [[NSMutableDictionary alloc] initWithCapacity:3];
        _interstitialAds = [[NSMutableDictionary alloc] initWithCapacity:3];
    }
    
    return self;
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
                                  [weakSelf createBannerAdFromResponse:response];
                              }
                              else if ([dfpCommand isEqualToString:COMMAND_CREATE_INTERSTITIAL_AD]){
                                  [weakSelf createInterstitialAdFromResponse:response];
                              }
                              else if ([dfpCommand isEqualToString:COMMAND_SHOW_INTERSTITIAL_AD]){
                                  [weakSelf showInterstitialAdFromResponse:response];
                              }
                              else if ([dfpCommand isEqualToString:COMMAND_REMOVE_AD]) {
                                  [weakSelf removeAdFromResponse:response];
                              }
                              else {
                                  response.body = [NSString stringWithFormat:@"Method %@ not found.", dfpCommand];
                                  response.status = TealiumRC_Failure;
                              }
                              
                          }];
}


- (void) createBannerAdFromResponse:(TealiumRemoteCommandResponse*)response {
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
        
#warning Size not being read correctly
        GADAdSize size = GADAdSizeFromNSValue(value);
        
        
        DFPBannerView *bannerView = [[DFPBannerView alloc] initWithAdSize:kGADAdSizeBanner];
        
        bannerView.validAdSizes = bannerSizes;
        bannerView.adUnitID = adUnitID;
        bannerView.adSizeDelegate = weakSelf;
        bannerView.appEventDelegate = weakSelf;
        bannerView.delegate = weakSelf;
        bannerView.rootViewController = weakSelf.activeViewController;
        
        DFPRequest *request = [DFPRequest request];
        request.testDevices = testDevices;

        [bannerView loadRequest:request];
        
        weakSelf.bannerAds[adUnitID] = bannerView;
        
        response.status = TealiumRC_Success;
        [response send];
    });

    
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

- (void) createInterstitialAdFromResponse:(TealiumRemoteCommandResponse*)response {
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
        interstitial.adUnitID = adUnitID;
        interstitial.delegate = weakSelf;
        interstitial.appEventDelegate = weakSelf;
        
        // Not supported
        interstitial.customRenderedInterstitialDelegate = nil;
        
        DFPRequest *request = [DFPRequest request];
        request.testDevices = testDevices;
        [interstitial loadRequest:request];
        
        [weakSelf.interstitialAds setObject:interstitial forKey:adUnitID];
        response.status = TealiumRC_Success;
        [response send];
    });
}

- (void) showInterstitialAdFromResponse:(TealiumRemoteCommandResponse*)response {
        NSLog(@"%s ", __FUNCTION__);
    
    NSDictionary *payload = response.requestPayload;
    NSString *adID = payload[KEY_AD_ID];
    NSString *adUnitID = payload[KEY_AD_UNIT_ID];
    
    DFPInterstitial *interstitial = [self.interstitialAds objectForKey:adUnitID];
    if (!interstitial){
        // TODO: add error details
        response.status = STATUS_AD_NOT_FOUND;
    }
    else if (!self.activeViewController) {
        // TODO: add error details
        response.status = STATUS_NO_VIEW;
    }
    else if (!interstitial.isReady){
        response.status = STATUS_AD_NOT_READY;
    }
    else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [interstitial presentFromRootViewController:self.activeViewController];
        });
        response.status = TealiumRC_Success;
    }
    [response send];
}

- (void) removeAdFromResponse:(TealiumRemoteCommandResponse*)response {
        NSLog(@"%s ", __FUNCTION__);
}

#pragma mark - PRIVATE


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
}

- (void)interstitialDidDismissScreen:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
}

- (void)interstitialDidReceiveAd:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
}

- (void)interstitialWillDismissScreen:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
}

- (void)interstitialWillLeaveApplication:(GADInterstitial *)ad{
    NSLog(@"%s ", __FUNCTION__);
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
    
}

- (void)adViewWillPresentScreen:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    
}

- (void)adViewDidDismissScreen:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    
}
- (
   void)adViewWillDismissScreen:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    
}

- (void)adViewWillLeaveApplication:(DFPBannerView *)bannerView{
    NSLog(@"%s ", __FUNCTION__);
    
}


@end
