//
//  TealiumArtisaMobileTagBridge.h
//
//  Created by George Webster on 10/29/14.
//  Modified by Patrick McWilliams on 04/10/15
//  Copyright (c) 2014 f. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ADBMobile.h"


@interface TealiumADBMobileTagBridge : NSObject

+ (instancetype)sharedInstance;

- (void)addRemoteCommandHandlers;

//#ifdef __CORELOCATION__
//
//- (void)addTrackingBeacon:(CLBeacon *)beacon
//
//#endif

@end
