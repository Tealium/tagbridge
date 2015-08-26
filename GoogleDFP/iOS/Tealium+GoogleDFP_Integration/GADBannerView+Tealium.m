//
//  DFPBannerView+Tealium.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/10/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "GADBannerView+Tealium.h"
#import <objc/runtime.h>
#import "TEALGoogleDFPConstants.h"

static CFStringRef  const TEALIUM_KVO_BANNER_ADID = CFSTR("com.tealium.google.dfp.kvo.banner.apid");
static CFStringRef  const TEALIUM_KVO_BANNER_ANCHOR = CFSTR("com.tealium.google.dfp.kvo.banner.anchor");
static CFStringRef  const TEALIUM_KVO_BANNER_STATUS = CFSTR("com.tealium.google.dfp.kvo.banner.status");

@implementation GADBannerView (Tealium)

- (NSString *) teal_adID {
    
    NSString * tealID = @"";
    
    id rawObj = objc_getAssociatedObject(self, TEALIUM_KVO_BANNER_ADID);
    
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
    
    objc_setAssociatedObject(self, TEALIUM_KVO_BANNER_ADID, copy, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    return YES;
    
}

- (NSString *) teal_anchor {
    
    NSString * anchor = @"";
    
    id rawObj = objc_getAssociatedObject(self, TEALIUM_KVO_BANNER_ANCHOR);
    
    if ([rawObj isKindOfClass:([NSString class])]){
        anchor = rawObj;
    }
    
    return anchor;
}

- (BOOL) teal_setAnchor:(NSString *)string {
    
    NSString *copy = [string copy];
    if (![copy isKindOfClass:([NSString class])]){
        return NO;
    }
    
    objc_setAssociatedObject(self, TEALIUM_KVO_BANNER_ANCHOR, copy, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    return YES;
    
}

- (BOOL) teal_isVisible {
    if (!self.window ||
        self.alpha == 0 ||
        self.hidden == YES){
        return NO;
    }
    return YES;
}

- (NSString *) teal_status {
    
    NSString * status = STATUS_STRING_CREATED;
    
    id rawObj = objc_getAssociatedObject(self, TEALIUM_KVO_BANNER_STATUS);
    
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
    
    objc_setAssociatedObject(self, TEALIUM_KVO_BANNER_STATUS, copy, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    return YES;
}

- (NSString *) teal_type {
    return @"BANNER";
}

- (NSDictionary *) teal_json_output {
    
    NSMutableDictionary *mDict = [NSMutableDictionary dictionary];
    
    mDict[KEY_AD_ID] = [self teal_adID];
    mDict[KEY_AD_UNIT_ID] = self.adUnitID;
    mDict[KEY_BANNER_ANCHOR] = [self teal_anchor];
    mDict[KEY_STATUS] = [self teal_status];
    mDict[KEY_TYPE] = [self teal_type];
    
    return [NSDictionary dictionaryWithDictionary:mDict];
}

@end
