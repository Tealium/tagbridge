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

@property (nonatomic, strong) DFPBannerView *activeBanner;
@property (nonatomic, strong) TEALGoogleDFPAdStore *store;
@property (nonatomic, strong) dispatch_queue_t dispatchQueue;
@property (nonatomic) BOOL logsEnabled;

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
                    targetQueue:dispatch_get_main_queue()
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
    
    [self enableNotifications];
}

- (void) refresh {
    if (!self.activeBanner){
        return;
    }
    
    [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    [self adViewDidReceiveAd:self.activeBanner];
}

- (void) enableLogs:(BOOL)enable {
    self.logsEnabled = enable;
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

- (void) enableNotifications {
    NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
    
    [defaultCenter addObserver:self
                      selector:@selector(updateBannerLocation)
                          name:UIDeviceOrientationDidChangeNotification
                        object:[UIDevice currentDevice]];
    
    
}

- (void) dealloc {
    [self disableNotifications];
}

- (void) disableNotifications {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (GADAdSize)initalAdSize:(NSArray *)bannerAdSizesStrings {
    // Embedded functions as this does not appear to return correctly from an outside function
    __block GADAdSize initialSize = kGADAdSizeInvalid;
    
    for (NSString *string in bannerAdSizesStrings) {
        GADAdSize newSize;
        if ([string isEqualToString:BANNER_SMART]){

            // Replace with UITraitCollection + interface determination logic here for 8.0+ as desired
            
            UIInterfaceOrientation orientation = self.activeViewController.interfaceOrientation;
            if (UIInterfaceOrientationIsLandscape(orientation)) {
                newSize = [self adSizeWithHigherPriorityBetweenFirstSize:kGADAdSizeSmartBannerLandscape secondSize:initialSize];
            }
            else if (UIInterfaceOrientationIsPortrait(orientation)){
                newSize = [self adSizeWithHigherPriorityBetweenFirstSize:kGADAdSizeSmartBannerPortrait secondSize:initialSize];
            }
            
        }
        else if ([string isEqualToString:BANNER_FULL]){
            newSize = [self adSizeWithHigherPriorityBetweenFirstSize:kGADAdSizeFullBanner secondSize:initialSize];
        }
        else if ([string isEqualToString:BANNER_LARGE]){
            newSize = [self adSizeWithHigherPriorityBetweenFirstSize:kGADAdSizeLargeBanner secondSize:initialSize];
        }
        else if ([string isEqualToString:BANNER_MEDIUM_RECTANGLE]){
            newSize = [self adSizeWithHigherPriorityBetweenFirstSize:kGADAdSizeMediumRectangle secondSize:initialSize];
        }
        else if ([string isEqualToString:BANNER_LEADERBOARD]){
            newSize = [self adSizeWithHigherPriorityBetweenFirstSize:kGADAdSizeLeaderboard secondSize:initialSize];
        }
        else if ([string isEqualToString:BANNER]){
            newSize = [self adSizeWithHigherPriorityBetweenFirstSize:kGADAdSizeBanner secondSize:initialSize];
        }
        initialSize = newSize;
    }
    return initialSize;
}

- (void) createBannerAdWithResponse:(TealiumRemoteCommandResponse*)response {
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    NSDictionary *payload = response.requestPayload;
    NSString *adUnitID = payload[KEY_AD_UNIT_ID];
    
    if (!adUnitID){
        //TODO: Error reporting
        response.body = @"No Ad Unit Id passed in from request payload.";
        response.status = TealiumRC_Malformed;
        [response send];
        return;
    }
    if (!self.activeViewController) {
        // TODO: add error details
        response.body = @"No active view controller to display ad.";
        response.status = STATUS_NO_VIEW;
        [response send];
        return;
    }
    
    NSString *adID = payload[KEY_AD_ID];
    
    [self.store removeAllAdsWithAdID:adID adUnitID:nil];

    NSString *anchor = payload[KEY_BANNER_ANCHOR];
    NSArray *bannerAdSizesStrings = payload[KEY_BANNER_AD_SIZES];
    NSArray *bannerSizes = [self bannerAdSizesFromArrayOfStrings:bannerAdSizesStrings];
    BOOL manualImpressions = [payload[KEY_MANUAL_IMPRESSIONS] boolValue];

    if ([bannerSizes count] == 0) {
        response.body = @"No valid banner sizes received from request payload";
        response.status = TealiumRC_Malformed;
        [response send];
        return;
    }
    
    
    GADAdSize initialSize;
    initialSize = [self initalAdSize:bannerAdSizesStrings];
    
    DFPBannerView *bannerView = [[DFPBannerView alloc] initWithAdSize:initialSize];
    
    [bannerView teal_setAdID:adID];
    [bannerView teal_setAnchor:anchor];
    bannerView.validAdSizes = bannerSizes;
    bannerView.adUnitID = adUnitID;
    bannerView.adSizeDelegate = self;
    bannerView.appEventDelegate = self;
    bannerView.delegate = self;
    bannerView.rootViewController = self.activeViewController;
    bannerView.enableManualImpressions = manualImpressions;
    
    DFPRequest *request = [self dfpRequestFromPayload:payload];
    [bannerView loadRequest:request];
    
    if ([self.store addBannerView:bannerView]){
        self.activeBanner = bannerView;
        response.status = TealiumRC_Success;
    }
    else {
        response.body = @"BannerView was not created - check for unexpected or malformed property.";
        response.status = TealiumRC_Malformed;
    }
    [response send];

    
}


- (void) createInterstitialAdWithResponse:(TealiumRemoteCommandResponse*)response {
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    NSDictionary *payload = response.requestPayload;
    NSString *adUnitID = payload[KEY_AD_UNIT_ID];
    
    if (!adUnitID){
        //TODO: Error reporting
        response.body = @"No Ad Unit Id passed in from request payload.";
        response.status = TealiumRC_Malformed;
        [response send];
        return;
    }
    
    NSString *adID = payload[KEY_AD_ID];
    
    [self.store removeAllAdsWithAdID:adID adUnitID:nil];
    
    DFPInterstitial *interstitial = [[DFPInterstitial alloc] initWithAdUnitID:adUnitID];
    [interstitial teal_setAdID:adID];
    interstitial.delegate = self;
    interstitial.appEventDelegate = self;
    
    // Not supported
    interstitial.customRenderedInterstitialDelegate = nil;
    
    DFPRequest *request = [self dfpRequestFromPayload:payload];
    [interstitial loadRequest:request];
    
    [interstitial teal_setStatus:STATUS_STRING_CREATED];
    [self.store addInterstitialAd:interstitial];
    
    response.status = TealiumRC_Success;
    [response send];
}

- (void) getAdsWithResponse:(TealiumRemoteCommandResponse*)response {
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
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
       [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
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
        [interstitial teal_setStatus:STATUS_STRING_OPENED];
        [interstitial presentFromRootViewController:self.activeViewController];
        response.status = TealiumRC_Success;
    }
    [response send];
}

- (void) removeAdWithResponse:(TealiumRemoteCommandResponse*)response {
       [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    NSString *adID = response.requestPayload[KEY_AD_ID];
    NSString *adUnitID = response.requestPayload[KEY_AD_UNIT_ID];
    
    BOOL adRemoved = [self.store removeAllAdsWithAdID:adID adUnitID:adUnitID];
    
    if (!adRemoved){
        response.body = @"Ad already removed or was not yet created.";
        response.status = STATUS_ALREADY_REMOVED;
    }
    else {
        response.status = TealiumRC_Success;
    }
    
    [response send];
}

- (DFPRequest *) dfpRequestFromPayload:(NSDictionary *)payload {
    
    DFPRequest *request = [[DFPRequest alloc] init];
    
    // Extract payload data
    double birthday = [payload[KEY_BIRTHDAY] doubleValue]; // milliseconds
    NSDate *birthdayDate = [NSDate dateWithTimeIntervalSince1970:(birthday / 1000)]; // seconds
    NSArray *categoryExclusions = payload[KEY_CATEGORY_EXCLUSIONS];
    BOOL childTreatment = [payload[KEY_TAG_FOR_CHILD_DIRECTED_TREATMENT] boolValue];
    NSDictionary *customTargeting = payload[KEY_CUSTOM_TARGETING];
    NSArray *keywords = payload[KEY_KEYWORDS];
    NSDictionary *location = payload[KEY_LOCATION];
    NSString *gender = payload[KEY_GENDER];
    GADGender gadGender = kGADGenderUnknown;
    if ([gender isEqualToString:@"MALE"]){
        gadGender = kGADGenderMale;
    }
    if ([gender isEqualToString:@"FEMALE"]){
        gadGender = kGADGenderFemale;
    }
    NSString *publisherID = payload[KEY_PUBLISHER_PROVIDED_ID];
    NSString *requestAgent = payload[KEY_REQUEST_AGENT];
    NSArray *testDevices = payload[KEY_TEST_DEVICES];

    // Safely add to request
    [request tagForChildDirectedTreatment:childTreatment];
    
    if (birthdayDate) request.birthday = birthdayDate;
    if (categoryExclusions) request.categoryExclusions = categoryExclusions;
    if (customTargeting) request.customTargeting = customTargeting;
    if (keywords) request.keywords = keywords;
    if (gadGender) request.gender = gadGender;
    if (location) {
        
        double lat = [location[KEY_LATITUDE] doubleValue];
        double lon = [location[KEY_LONGITUDE] doubleValue];
        
        if (lat && lon){
            [request setLocationWithLatitude:lat longitude:lon accuracy:100.0];
        }
        
    }
    if (publisherID) request.publisherProvidedID = publisherID;
    if (requestAgent) request.requestAgent = requestAgent;
    if (testDevices) request.testDevices = testDevices;
    
    return request;
}


#pragma mark - PRIVATE HELPERS

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
            [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeSmartBannerLandscape)];
            [adSizes addObject:NSValueFromGADAdSize(kGADAdSizeSmartBannerPortrait)];
        }
        else {
            // TODO: ERROR
        }
    }
    return [NSArray arrayWithArray:adSizes];
    
}

- (GADAdSize) adSizeWithHigherPriorityBetweenFirstSize:(GADAdSize)firstSize secondSize:(GADAdSize)secondSize {
    
    if (!IsGADAdSizeValid(firstSize)) return secondSize;
    if (!IsGADAdSizeValid(secondSize)) return firstSize;
    
    CGSize size1 = CGSizeFromGADAdSize(firstSize);
    CGSize size2 = CGSizeFromGADAdSize(secondSize);
    
    if ([self isThisCGSize:size1 smallerThanThisCGSize:size2]){
        // YES size 1 is smaller - otherwise equal or size 2 larger
        return secondSize;
    }
    
    return firstSize;

}

- (BOOL) isThisCGSize:(CGSize)firstSize smallerThanThisCGSize:(CGSize)secondSize {
    CGFloat firstArea = firstSize.height * firstSize.width;
    CGFloat secondArea = secondSize.height * secondSize.width;
    
    if ( firstArea <= secondArea) return YES;
    
    return NO;
}

- (void) log:(NSString *)message, ... {
    
    if (!self.logsEnabled){
        return;
    }

    NSLog(message, nil);
    
}

- (void) updateBannerLocation {
    
    if (!self.activeBanner) return;
    
    
    [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    
    [self adViewDidReceiveAd:self.activeBanner];
 }

#pragma mark - GADAdSize + GADAppEvent Delegates

- (void)adView:(DFPBannerView *)view willChangeAdSizeTo:(GADAdSize)size {
    CGSize cgSize = CGSizeFromGADAdSize(size);
    [self log:[NSString stringWithFormat:@"%s \n viewH:%f \n viewW:%f \n toSizeH:%f \n toSizeW:%f", __FUNCTION__, view.frame.size.height, view.frame.size.width, cgSize.height, cgSize.width]];
}

- (void)adView:(GADBannerView *)banner
didReceiveAppEvent:(NSString *)name
      withInfo:(NSString *)info{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    
}

#pragma mark - GADInterstitial Delegates

- (void)interstitial:(GADInterstitial *)interstitial
  didReceiveAppEvent:(NSString *)name
            withInfo:(NSString *)info{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
}

- (void)interstitial:(GADInterstitial *)ad didFailToReceiveAdWithError:(GADRequestError *)error{
    
    
    [self log:[NSString stringWithFormat:@"%s ERROR: %@", __FUNCTION__, [error localizedDescription]]];
    
    [ad teal_setStatus:STATUS_STRING_FAILEDTOLOAD];
}

- (void)interstitialDidDismissScreen:(GADInterstitial *)ad{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    [ad teal_setStatus:STATUS_STRING_CLOSED];
}

- (void)interstitialDidReceiveAd:(GADInterstitial *)ad{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
}

- (void)interstitialWillDismissScreen:(GADInterstitial *)ad{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    [ad teal_setStatus:STATUS_STRING_CLOSED];
}

- (void)interstitialWillLeaveApplication:(GADInterstitial *)ad{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
    [ad teal_setStatus:STATUS_STRING_LEFTAPPLICATION];
}

- (void)interstitialWillPresentScreen:(GADInterstitial *)ad{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
}

#pragma mark - GADBannerView Delegates

- (void)adViewDidReceiveAd:(DFPBannerView *)bannerView{
    
    if (!self.activeViewController) return;
    if (!self.activeBanner) return;
    
    dispatch_async(dispatch_get_main_queue(), ^{
    
        if ([self viewControllerAlreadyDisplayingBannerView:bannerView]) return;
        
       [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];

        bannerView.alpha = 0.0;
        UIView *view = self.activeViewController.view;
        CGFloat viewH = view.frame.size.height;
        CGFloat bannerH = bannerView.frame.size.height;
        CGFloat bannerW = bannerView.frame.size.width;
        CGFloat startY = viewH - bannerH;
        NSString *anchor = [bannerView teal_anchor];
        if ([anchor isEqualToString:VALUE_TOP]){
            
            UIApplication *sharedApplication = [UIApplication sharedApplication];
            CGFloat statusBarH = sharedApplication.statusBarFrame.size.height;
            if (sharedApplication.statusBarHidden) statusBarH = 0.0;
            
            UINavigationBar *navBar = self.activeViewController.navigationController.navigationBar;
            
            CGFloat navBarH = navBar.frame.size.height;
            if (navBar.isHidden || navBar.alpha == 0) navBarH = 0.0;
            
            startY = navBarH + statusBarH;
        }
        bannerView.frame = CGRectMake(0.0,
                                      startY,
                                      bannerW,
                                      bannerH);
        [self.activeViewController.view addSubview:bannerView];

        [UIView animateWithDuration:0.5 animations:^{
            bannerView.alpha = 1.0;
        }];
    });
}

- (BOOL) viewControllerAlreadyDisplayingBannerView:(DFPBannerView *)bannerView {
    NSArray *subViews = self.activeViewController.view.subviews;
    for (UIView *view in subViews) {
        if (view == bannerView) {
            return YES;
        }
    }
    return NO;
}

- (void)adView:(DFPBannerView *)bannerView
didFailToReceiveAdWithError:(GADRequestError *)error{
    [self log:[NSString stringWithFormat:@"%s ERROR:%@ ", __FUNCTION__, [error localizedDescription]]];
    
    [bannerView teal_setStatus:STATUS_STRING_FAILEDTOLOAD];
    
}

- (void)adViewWillPresentScreen:(DFPBannerView *)bannerView{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    [bannerView teal_setStatus:STATUS_STRING_OPENED];
}

- (void)adViewDidDismissScreen:(DFPBannerView *)bannerView{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    [bannerView teal_setStatus:STATUS_STRING_CLOSED];
    if (self.activeBanner == bannerView) self.activeBanner = nil;
}
- (
   void)adViewWillDismissScreen:(DFPBannerView *)bannerView{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    
}

- (void)adViewWillLeaveApplication:(DFPBannerView *)bannerView{
   [self log:[NSString stringWithFormat:@"%s", __FUNCTION__]];
    [bannerView teal_setStatus:STATUS_STRING_LEFTAPPLICATION];
}


@end
