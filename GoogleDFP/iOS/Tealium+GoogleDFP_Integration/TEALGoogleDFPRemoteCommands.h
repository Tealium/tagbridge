//
//  TEALGoogleDFPRemoteCommands.h
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/6/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GoogleMobileAds/GoogleMobileAds.h>


@interface TEALGoogleDFPRemoteCommands : NSObject <GADAppEventDelegate, GADBannerViewDelegate, GADInterstitialDelegate, GADAdSizeDelegate>

@property (nonatomic, weak)  UIViewController *activeViewController;

+ (instancetype) sharedInstance;

/*
 Starts the module listeners
 */
- (void) enable;

/*
 Recreates any active ads onto the current view controller. Call this after changing the active view controller if wanting to continuously display a previously presented banner ad.
 */
- (void) refresh;

/*
 Optional enable or disable console output logs. Default NO.
 */
- (void) enableLogs:(BOOL)enable;

@end
