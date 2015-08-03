//
//  TealiumDFPTagBridge.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Patrick McWilliams on 6/7/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "TealiumDFPTagBridge.h"
#import <TealiumLibrary/Tealium.h>


typedef NSDictionary* (^__VendorMethod)(__weak NSDictionary *data);
static NSString *const responseCode = @"responseCode";
static NSString *const responseBody = @"responseBody";

@interface __TLMDFPDelegates : NSObject <GADAppEventDelegate, GADBannerViewDelegate, GADAdSizeDelegate>

@end

@implementation __TLMDFPDelegates

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken = 0;
    __strong static __TLMDFPDelegates *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[__TLMDFPDelegates alloc] init];
    });
    
    return _sharedObject;
}

// !!!: GADAdSizeDelegate
- (void)adView:(DFPBannerView *)view willChangeAdSizeTo:(GADAdSize)size {
    
}

// !!!: GADAppEventDelegate
- (void)adView:(GADBannerView *)banner
didReceiveAppEvent:(NSString *)name
      withInfo:(NSString *)info{
    
}

/// Called when the interstitial receives an app event.
- (void)interstitial:(GADInterstitial *)interstitial
  didReceiveAppEvent:(NSString *)name
            withInfo:(NSString *)info{
    
}

// !!!: GADBannerViewDelegate
- (void)adViewDidReceiveAd:(DFPBannerView *)bannerView{
    
}
- (void)adView:(DFPBannerView *)bannerView
didFailToReceiveAdWithError:(GADRequestError *)error{
    
}

- (void)adViewWillPresentScreen:(DFPBannerView *)bannerView{

}
- (void)adViewDidDismissScreen:(DFPBannerView *)bannerView{
    
}
- (void)adViewWillDismissScreen:(DFPBannerView *)bannerView{
    
}
- (void)adViewWillLeaveApplication:(DFPBannerView *)bannerView{
    
}

@end

@interface __TLMDFPBannerView: NSObject
+ (__VendorMethod)createAd;

@end


@implementation __TLMDFPBannerView

+ (__VendorMethod)createAd {
    return ^NSDictionary* (__weak NSDictionary *data){
        NSInteger banner_width = [[data valueForKey:@"banner_width"] integerValue];
        NSInteger banner_height = [[data valueForKey:@"banner_height"] integerValue];
        NSInteger view_width = [[data valueForKey:@"view_width"] integerValue];
        NSInteger view_height = [[data valueForKey:@"view_height"] integerValue];
        NSNumber *viewMarginLeft = [NSNumber numberWithInt:-1];
        if([data objectForKey:@"view_margin_left"]){
            [[data objectForKey:@"view_margin_left"] integerValue];
        }
        NSNumber *viewMarginTop = [NSNumber numberWithInt:-1];
        if([data objectForKey:@"view_margin_top"]){
            [[data objectForKey:@"view_margin_top"] integerValue];
        }
        NSNumber *viewMarginRight = [NSNumber numberWithInt:-1];
        if([data objectForKey:@"view_margin_right"]){
            [[data objectForKey:@"view_margin_right"] integerValue];
        }
        NSNumber *viewMarginBottom = [NSNumber numberWithInt:-1];
        if([data objectForKey:@"view_margin_bottom"]){
            [[data objectForKey:@"view_margin_bottom"] integerValue];
        }
        NSString *viewPosition = [data objectForKey:@"view_position"];

        NSString *add_unit_id = [data valueForKey:@"ad_unit_id"];
        GADAdSize customAdSize;
        if (banner_height && banner_width) {
            if (!view_height || !view_width) {
                view_height = banner_height;
                view_width = banner_width;
            }
            customAdSize = GADAdSizeFromCGSize(CGSizeMake(banner_width, banner_height));
        }
        else {
            UIDeviceOrientation deviceOrientation = [[UIDevice currentDevice] orientation];
            if (deviceOrientation == UIDeviceOrientationLandscapeLeft || deviceOrientation == UIDeviceOrientationLandscapeRight ) {
                customAdSize = kGADAdSizeSmartBannerLandscape;
            }
            else if (deviceOrientation == UIDeviceOrientationPortrait || deviceOrientation == UIDeviceOrientationPortraitUpsideDown){
                customAdSize = kGADAdSizeSmartBannerPortrait;
            }
        }
        
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:responseCode];
        
        UIViewController *viewController = [[TealiumDFPTagBridge sharedInstance] getActiveViewController];
        
        if (viewController) {
            TealiumDFPTagBridge *TLMDFP = [TealiumDFPTagBridge sharedInstance];
            dispatch_async(dispatch_get_main_queue(), ^{
                TLMDFP.bannerView = [[DFPBannerView alloc] initWithAdSize:customAdSize];
                TLMDFP.bannerView.frame = CGRectMake(0, 0, view_width, view_height);
                TLMDFP.bannerView.adSizeDelegate = [__TLMDFPDelegates sharedInstance];
                TLMDFP.bannerView.appEventDelegate = [__TLMDFPDelegates sharedInstance];
                TLMDFP.bannerView.delegate = [__TLMDFPDelegates sharedInstance];
                TLMDFP.bannerView.backgroundColor = [UIColor lightGrayColor];
                [TLMDFP.bannerView setTranslatesAutoresizingMaskIntoConstraints:NO];
                [viewController.view addSubview:TLMDFP.bannerView];
                if ([viewMarginLeft integerValue] > 0 ||
                    [viewMarginRight integerValue] > 0 ||
                    [viewMarginTop integerValue] > 0 ||
                    [viewMarginBottom integerValue] > 0){
                    if ([viewMarginTop integerValue] >= 0){
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-top-[bannerView]" options:0 metrics:@{@"top":viewMarginTop} views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                    if ([viewMarginBottom integerValue] >= 0){
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[bannerView]-bottom-|" options:0 metrics:@{@"bottom":viewMarginBottom} views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                    if ([viewMarginLeft integerValue] >= 0){
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-left-[bannerView]" options:0 metrics:@{@"left":viewMarginLeft} views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                    if ([viewMarginRight integerValue] >= 0){
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[bannerView]-right-|" options:0 metrics:@{@"right":viewMarginRight} views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                }
                else if (viewPosition) {
                    [viewPosition lowercaseString];
                    if ([viewPosition isEqualToString:@"top"]) {
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-0-[bannerView]" options:0 metrics:0 views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                    else if ([viewPosition isEqualToString:@"bottom"]) {
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[bannerView]-0-|" options:0 metrics:0 views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                    else if ([viewPosition isEqualToString:@"left"]) {
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-0-[bannerView]" options:0 metrics:0 views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                    else if ([viewPosition isEqualToString:@"right"]) {
                        [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[bannerView]-0-|" options:0 metrics:0 views:@{@"bannerView":TLMDFP.bannerView}]];
                    }
                }
                else {
                    [viewController.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-0-[bannerView]" options:0 metrics:0 views:@{@"bannerView":TLMDFP.bannerView}]];
                }
                
                TLMDFP.bannerView.adUnitID = add_unit_id;
                TLMDFP.bannerView.rootViewController = viewController;
                DFPRequest *request = [DFPRequest request];
                [TLMDFP.bannerView loadRequest:request];
            });

        }
        else {
            [responseData setValue:@500 forKey:responseCode];
            [responseData setValue:@"no active view controller set" forKey:responseBody];
        }

        return responseData;
    };
}


@end


@interface TealiumDFPTagBridge()

@property (nonatomic, strong) dispatch_queue_t googleDFPDispatchQueue;
@property (nonatomic, strong) NSMutableDictionary *googleDFPMethodStrings;
@property (nonatomic, weak) UIViewController *activeViewController;
@property (nonatomic) BOOL initiliazed;

@end

@implementation TealiumDFPTagBridge


NSString *const google_dfp = @"google_dfp";
NSString *const google_dfp_description = @"google dfp";
NSString *const create_ad = @"create_ad";

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken = 0;
    __strong static TealiumDFPTagBridge *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[TealiumDFPTagBridge alloc] init];
    });
    
    return _sharedObject;
}


- (void)addRemoteCommandHandlers
{
    // Customize CommandID and description
    [Tealium addRemoteCommandId:google_dfp
                    description:google_dfp_description
                    targetQueue:self.googleDFPDispatchQueue
                          block:^(TealiumRemoteCommandResponse *response) {
                              
                              NSString *methodString = response.requestPayload[@"method"];
                              NSDictionary *data = response.requestPayload[@"arguments"];
                              NSDictionary *responseData = [self handleMethodOfType:methodString
                                                                      withInputData:data];
                              NSString *thisResponseBody = [responseData objectForKey:responseBody];
                              NSNumber *thisResponseCode = [responseData objectForKey:responseCode];
                              if ([thisResponseCode isEqual:@200]) {
                                  
                                  NSDictionary *responseJSON = nil;
                                  NSError *error = nil;
                                  if(thisResponseBody != nil){
                                      NSData *responseData = [thisResponseBody dataUsingEncoding:NSUTF8StringEncoding];
                                      if (responseData){
                                          responseJSON = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:&error];
                                      }
                                      if(responseJSON){
                                          response.body = [[NSString alloc] initWithData:responseData
                                                                                encoding:NSUTF8StringEncoding];
                                      }
                                      else if (error){
                                          NSString *errorOutput = [[NSString alloc] stringByAppendingFormat: @"problem serializing response body: %@", [error localizedDescription]];
                                          response.body = errorOutput;
                                          NSLog(@"%@", errorOutput);
                                          
                                      }
                                  }
                                  else{
                                      response.body = @"";
                                  }
                                  response.status = TealiumRC_Success;
                              }
                              else {
                                  response.status = TealiumRC_Failure;
                              }
                              
                              [response send];
                          }];
    
}


- (instancetype)init
{
    self = [super init];
    self.initiliazed = false;
    
    if (self) {
        _googleDFPDispatchQueue = dispatch_queue_create("com.tealium.tagbridge.google.dfp", NULL);
        
        // Add methods here
        _googleDFPMethodStrings = [[NSMutableDictionary alloc] init];
        _googleDFPMethodStrings[create_ad] = [__TLMDFPBannerView createAd];
        
    }
    return self;
}


- (NSDictionary *)handleMethodOfType:(NSString*)methodType
                       withInputData:(NSDictionary *)inputData
{
    __VendorMethod thisVendorMethod = (__VendorMethod)[_googleDFPMethodStrings objectForKey:methodType];
    NSDictionary *responseDict = [[NSDictionary alloc] init];
    if (thisVendorMethod){
        responseDict = thisVendorMethod(inputData);
    }
    else{
        NSLog(@"unsupported method type: %@", methodType);
        [responseDict setValue:@500 forKey:@"responseCode"];
        [responseDict setValue:@"No method found" forKey:@"responseBody"];
        return responseDict;
    }
    
    return responseDict;
}


- (void)activeViewController:(UIViewController*)viewController{
    self.activeViewController = viewController;
}

- (UIViewController*)getActiveViewController{
    return self.activeViewController;
}

- (void)bannerView:(DFPBannerView*)viewController{
    self.bannerView = viewController;
}

- (DFPBannerView*)getBannerView{
    return self.bannerView;
}

@end



