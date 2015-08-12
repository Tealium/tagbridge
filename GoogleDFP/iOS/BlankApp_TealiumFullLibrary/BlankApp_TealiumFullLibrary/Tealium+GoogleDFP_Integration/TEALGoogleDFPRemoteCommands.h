//
//  TEALGoogleDFPRemoteCommands.h
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/6/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GoogleMobileAds/GoogleMobileAds.h>

//typedef void (^TEALBooleanCompletionBlock)(BOOL success, NSError *error);

@interface TEALGoogleDFPRemoteCommands : NSObject <GADAppEventDelegate, GADBannerViewDelegate, GADInterstitialDelegate, GADAdSizeDelegate>

@property (nonatomic, weak)  UIViewController *activeViewController;
@property (nonatomic, strong) dispatch_queue_t dispatchQueue;

+ (instancetype) sharedInstance;

- (void) enable;

@end
