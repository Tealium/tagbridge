//
//  DFPBannerView+Tealium.h
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/10/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GoogleMobileAds/GoogleMobileAds.h>

@interface GADBannerView (Tealium)

- (NSString *) teal_adID;

- (BOOL) teal_setAdID:(NSString *)string;

- (NSString *) teal_anchor;

- (BOOL) teal_setAnchor:(NSString *)string;

- (BOOL) teal_isVisible;

- (NSString *) teal_status;

- (BOOL) teal_setStatus:(NSString *)status;

- (NSString *) teal_type;

- (NSDictionary *) teal_json_output;

@end
