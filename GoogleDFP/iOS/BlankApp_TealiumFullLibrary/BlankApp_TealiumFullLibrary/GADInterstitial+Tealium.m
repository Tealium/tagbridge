//
//  DFPInterstitial+Tealium.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/10/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "GADInterstitial+Tealium.h"
#import "TEALGoogleDFPConstants.h"
#import <objc/runtime.h>

static CFStringRef  const TEALIUM_KVO_INTERSTITIAL_ADID = CFSTR("com.tealium.google.dfp.kvo.interstitial.adid");
static CFStringRef  const TEALIUM_KVO_INTERSTITIAL_STATUS = CFSTR("com.tealium.google.dfp.kvo.interstitial.status");

@implementation GADInterstitial (Tealium)

- (NSString *) teal_adID {
    
    NSString * tealID = nil;
    
    id rawObj = objc_getAssociatedObject(self, TEALIUM_KVO_INTERSTITIAL_ADID);
    
    if ([rawObj isKindOfClass:([NSString class])]){
        tealID = rawObj;
    }
    
    return tealID;
}

- (BOOL) teal_setAdID:(NSString *)string {
    
    NSString *copy = [string copy];
    if (![copy isKindOfClass:([NSString class])]){
        return NO;
    }
    
    objc_setAssociatedObject(self, TEALIUM_KVO_INTERSTITIAL_ADID, copy, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    return YES;
    
}

- (NSString *) teal_status {
    
    NSString * status = nil;
    
    id rawObj = objc_getAssociatedObject(self, TEALIUM_KVO_INTERSTITIAL_STATUS);
    
    if ([rawObj isKindOfClass:([NSString class])]){
        status = rawObj;
    }
    
    return status;
}

- (BOOL) teal_setStatus:(NSString *)status {
    
    NSString *copy = [status copy];
    if (![copy isKindOfClass:([NSString class])]){
        return NO;
    }
    
    objc_setAssociatedObject(self, TEALIUM_KVO_INTERSTITIAL_STATUS, copy, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    return YES;
}

- (NSString *) teal_type {
    return @"INTERSTITIAL";
}

- (NSDictionary *) teal_json_output {
    
    NSMutableDictionary *mDict = [NSMutableDictionary dictionary];
    
    mDict[KEY_AD_ID] = [self teal_adID];
    mDict[KEY_AD_UNIT_ID] = self.adUnitID;
    mDict[KEY_STATUS] = [self teal_status];
    mDict[KEY_TYPE] = [self teal_type];
    
    return [NSDictionary dictionaryWithDictionary:mDict];
}

@end
