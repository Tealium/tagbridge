//
//  TEALGoogleDFPConstants.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/11/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "TEALGoogleDFPConstants.h"


NSString *const BANNER = @"BANNER";
NSString *const BANNER_FULL = @"FULL_BANNER";
NSString *const BANNER_LARGE = @"LARGE_BANNER";
NSString *const BANNER_LEADERBOARD = @"LEADERBOARD";
NSString *const BANNER_MEDIUM_RECTANGLE = @"MEDIUM_RECTANGLE";
NSString *const BANNER_SMART = @"SMART_BANNER";

NSString *const COMMAND_CREATE_BANNER_AD = @"create_banner_ad";
NSString *const COMMAND_CREATE_INTERSTITIAL_AD = @"create_interstitial_ad";
NSString *const COMMAND_SHOW_INTERSTITIAL_AD =  @"show_interstitial_ad";
NSString *const COMMAND_GET_ADS = @"get_ads";
NSString *const COMMAND_REMOVE_AD = @"remove_ad";

NSString *const KEY_AD_ID = @"ad_id";
NSString *const KEY_AD_UNIT_ID = @"ad_unit_id";
NSString *const KEY_BANNER_ANCHOR = @"banner_anchor";
NSString *const KEY_BANNER_AD_SIZES = @"banner_ad_sizes";
NSString *const KEY_BIRTHDAY = @"birthday";
NSString *const KEY_CATEGORY_EXCLUSIONS = @"category_exclusions";
NSString *const KEY_CUSTOM_TARGETING = @"custom_targeting";
NSString *const KEY_GENDER = @"gender";
NSString *const KEY_KEYWORDS = @"keywords";
NSString *const KEY_LOCATION = @"location";
NSString *const KEY_LATITUDE = @"latitude";
NSString *const KEY_LONGITUDE = @"longitude";
NSString *const KEY_MANUAL_IMPRESSIONS = @"manual_impressions";
NSString *const KEY_PUBLISHER_PROVIDED_ID = @"publisher_provided_id";
NSString *const KEY_REQUEST_AGENT = @"request_agent";
NSString *const KEY_STATUS = @"status";
NSString *const KEY_TAG_FOR_CHILD_DIRECTED_TREATMENT = @"tag_for_child_directed_treatment";
NSString *const KEY_TEST_DEVICES = @"test_devices";
NSString *const KEY_TYPE = @"type";


NSUInteger const STATUS_NO_VIEW = 418;
NSUInteger const STATUS_INCOMPATIBLE = 419;
NSUInteger const STATUS_AD_NOT_FOUND = 420;
NSUInteger const STATUS_ALREADY_REMOVED = 421;
NSUInteger const STATUS_AD_NOT_READY = 422;

NSString *const STATUS_STRING_CREATED = @"CREATED";
NSString *const STATUS_STRING_CLOSED = @"CLOSED";
NSString *const STATUS_STRING_FAILEDTOLOAD = @"FAILED_TO_LOAD";
NSString *const STATUS_STRING_LEFTAPPLICATION = @"LEFT_APPLICATION";
NSString *const STATUS_STRING_OPENED = @"OPENED";
NSString *const STATUS_STRING_LOADED = @"LOADED";

NSString *const VALUE_BOTTOM = @"BOTTOM";
NSString *const VALUE_TOP = @"TOP";


@implementation TEALGoogleDFPConstants

@end
