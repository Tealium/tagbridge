//
//  TealiumDFPTagBridge.h
//  BlankApp_TealiumFullLibrary
//
//  Created by Patrick McWilliams on 6/7/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GoogleMobileAds/GoogleMobileAds.h>

@interface TealiumDFPTagBridge : NSObject
@property (nonatomic, strong) DFPBannerView *bannerView;
@property (nonatomic, strong) DFPInterstitial *interstitial;

+ (instancetype)sharedInstance;
- (void)addRemoteCommandHandlers;
- (void)activeViewController:(UIViewController*)viewController;
- (UIViewController*)getActiveViewController;

@end

