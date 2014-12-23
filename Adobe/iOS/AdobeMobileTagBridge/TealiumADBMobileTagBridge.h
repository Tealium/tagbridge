//
//  TealiumADBMobileTagBridge.h
//  UICatalog
//
//  Created by George Webster on 10/29/14.
//  Copyright (c) 2014 f. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ADBMobile.h"

typedef NS_ENUM(NSInteger, TealiumADBMobileMethod) {
    TealiumADBMobileMethodNone = 0,
    TealiumADBMobileMethodSetPrivacyStatus = 1,         //setPrivacyStatus:
    TealiumADBMobileMethodSetUserIdentifier,            //setUserIdentifier:
    TealiumADBMobileMethodCollectLifecycleData,         //collectLifecycleData
    TealiumADBMobileMethodKeepLifecycleSessionAlive,                          //keepLifecycleSessionAlive
    TealiumADBMobileMethodTrackState,                   //trackState:customData:
    TealiumADBMobileMethodTrackAction,                  //trackAction:(NSString *)actionName customData:
    TealiumADBMobileMethodTrackActionFromBackground,    //trackActionFromBackground:(NSString *)actionName customData:
    TealiumADBMobileMethodTrackLifetimeValueIncrease,   //trackLifetimeValueIncrease:(NSDecimalNumber *)ammount customData:
    TealiumADBMobileMethodTrackLocation,                //trackLocation:(CLLocation *)location data
    TealiumADBMobileMethodTrackBeacon,                  //trackBeacon:(CLBeacon *)beacon data:
    TealiumADBMobileMethodTrackingClearCurrentBeacon,   //trackingClearCurrentBeacon
    TealiumADBMobileMethodTrackTimedActionStart,        //trackTimedActionStart
    TealiumADBMobileMethodTrackTimedActionUpdate,      //trackTimedActionUpdate
    TealiumADBMobileMethodTrackTimedActionEnd,         //trackTimedActionEnd
    TealiumADBMobileMethodTrackingSendQueuedHits,   //trackingSendQueuedHits
    TealiumADBMobileMethodTrackingClearQueue,   //trackingClearQueue
    TealiumADBMobileMethodMediaClose,       //mediaClose:
    TealiumADBMobileMethodMediaPlay,        //mediaPlay:(NSString *)name offset
    TealiumADBMobileMethodMediaComplete,    //mediaComplete:(NSString *)name offset
    TealiumADBMobileMethodMediaStop,    //mediaStop:(NSString *)name offset:
    TealiumADBMobileMethodMediaClick,   //mediaClick:offset:
    TealiumADBMobileMethodMediaTrack    //mediaTrack:data:
};

@interface TealiumADBMobileTagBridge : NSObject

+ (instancetype)sharedInstance;

- (void)addRemoteCommandHandlers;

#ifdef __CORELOCATION__

- (void)addTrackingBeacon:(CLBeacon *)beacon

#endif

@end
