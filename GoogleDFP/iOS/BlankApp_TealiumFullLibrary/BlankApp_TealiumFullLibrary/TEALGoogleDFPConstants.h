//
//  TEALGoogleDFPConstants.h
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/11/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString *const BANNER;
extern NSString *const BANNER_FULL;
extern NSString *const BANNER_LARGE;
extern NSString *const BANNER_LEADERBOARD;
extern NSString *const BANNER_MEDIUM_RECTANGLE;
extern NSString *const BANNER_SMART;

extern NSString *const COMMAND_CREATE_BANNER_AD;
extern NSString *const COMMAND_CREATE_INTERSTITIAL_AD;
extern NSString *const COMMAND_SHOW_INTERSTITIAL_AD;
extern NSString *const COMMAND_GET_ADS;
extern NSString *const COMMAND_REMOVE_AD;

extern NSString *const KEY_AD_ID;
extern NSString *const KEY_AD_UNIT_ID;
extern NSString *const KEY_BANNER_ANCHOR;
extern NSString *const KEY_BANNER_AD_SIZES;
extern NSString *const KEY_BIRTHDAY;
extern NSString *const KEY_CATEGORY_EXCLUSIONS;
extern NSString *const KEY_CUSTOM_TARGETING;
extern NSString *const KEY_GENDER;
extern NSString *const KEY_KEYWORDS;
extern NSString *const KEY_LOCATION;
extern NSString *const KEY_MANUAL_IMPRESSIONS;
extern NSString *const KEY_PUBLISHER_PROVIDED_ID;
extern NSString *const KEY_REQUEST_AGENT;
extern NSString *const KEY_STATUS;
extern NSString *const KEY_TAG_FOR_CHILD_DIRECTED_TREATMENT;
extern NSString *const KEY_TEST_DEVICES;
extern NSString *const KEY_TYPE;

extern NSUInteger const STATUS_NO_VIEW;
extern NSUInteger const STATUS_INCOMPATIBLE;
extern NSUInteger const STATUS_AD_NOT_FOUND;
extern NSUInteger const STATUS_ALREADY_REMOVED;
extern NSUInteger const STATUS_AD_NOT_READY;

extern NSString *const STATUS_STRING_CREATED;
extern NSString *const STATUS_STRING_CLOSED;
extern NSString *const STATUS_STRING_FAILEDTOLOAD;
extern NSString *const STATUS_STRING_LEFTAPPLICATION;
extern NSString *const STATUS_STRING_OPENED;
extern NSString *const STATUS_STRING_LOADED;

extern NSString *const VALUE_BOTTOM;
extern NSString *const VALUE_TOP;

@interface TEALGoogleDFPConstants : NSObject

@end
