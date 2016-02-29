//
//  TealiumArtisaMobileTagBridge.m
//
//  Created by George Webster on 10/29/14.
//  Modified by Patrick McWilliams on 04/10/15
//  Copyright (c) 2014 f. All rights reserved.
//

#import "TealiumADBMobileTagBridge.h"
#import <TealiumLibrary/Tealium.h>
#import <CoreLocation/CoreLocation.h>
#import "ADBMobile.h"



typedef NSDictionary* (^__VendorMethod)(__weak NSDictionary *data);

@interface TealiumADBMobileTagBridge()

@property (nonatomic, strong) dispatch_queue_t adobeDispatchQueue;

@property (nonatomic, strong) NSMutableDictionary *adobeMethodStrings;

@property (nonatomic, strong) NSString *const ADOBE_VENDOR_NAME;
@property (nonatomic, strong) NSString *const ADOBE_VENDOR_DESCRIPTION;

@end

@interface __TLMADBMobile: NSObject
+ (__VendorMethod)setPrivacyStatus;
+ (__VendorMethod)setUserIdentifier;
+ (__VendorMethod)collectLifecycleData;
+ (__VendorMethod)keepLifecycleSessionAlive;
+ (__VendorMethod)trackState;
+ (__VendorMethod)trackAction;
+ (__VendorMethod)trackActionFromBackground;
+ (__VendorMethod)trackLifetimeValueIncrease;
+ (__VendorMethod)trackLocation;
+ (__VendorMethod)trackBeacon;
+ (__VendorMethod)clearTrackingBeacon;
+ (__VendorMethod)trackTimedActionStart;
+ (__VendorMethod)trackTimedActionUpdate;
+ (__VendorMethod)trackTimedActionEnd;
+ (__VendorMethod)trackingSendQueuedHits;
+ (__VendorMethod)trackingClearQueue;
+ (__VendorMethod)mediaClose;
+ (__VendorMethod)mediaPlay;
+ (__VendorMethod)mediaComplete;
+ (__VendorMethod)mediaStop;
+ (__VendorMethod)mediaClick;
+ (__VendorMethod)mediaTrack;
#ifdef __CORELOCATION__
+ (CLLocation *)locationWithData:(NSDictionary *)data;
#endif
@end

#ifdef __CORELOCATION__
@interface __TLMADBMobileLocation : NSObject <CLLocationManagerDelegate>
- (CLBeacon*) getLastBeaconTracked;
+ (instancetype)sharedInstance;
@property (nonatomic, strong) CLLocationManager * locationManager;
@property (nonatomic, strong) CLBeacon *lastBeaconTracked;
- (void)locationManager:(CLLocationManager *)manager didRangeBeacons:(NSArray *)beacons inRegion:(CLBeaconRegion *)region;
@end

@implementation __TLMADBMobileLocation

+ (instancetype) sharedInstance
{
    static dispatch_once_t onceToken = 0;
    __strong static __TLMADBMobileLocation *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[__TLMADBMobileLocation alloc] init];
    });
    
    return _sharedObject;
}

- (void) load {
    __TLMADBMobileLocation *tlmLocation = [[__TLMADBMobileLocation alloc] init];
    tlmLocation.locationManager = [[CLLocationManager alloc] init];
    tlmLocation.locationManager.delegate = tlmLocation;
}


- (CLBeacon*) getLastBeaconTracked{
    return _lastBeaconTracked;
}

- (void)locationManager:(CLLocationManager *)manager didRangeBeacons:(NSArray *)beacons inRegion:(CLBeaconRegion *)region {
    CLBeacon *beacon = [beacons objectAtIndex:0];
    if (beacon != nil) {
        CLBeacon *beaconToTrack = beacon;
        
        if (_lastBeaconTracked != nil) {
            bool stillActive = false;
            for (CLBeacon *thisBeacon in beacons){
                if ([thisBeacon.proximityUUID isEqual:_lastBeaconTracked.proximityUUID]){
                    stillActive = true;
                }
            }
            if (!stillActive){
                NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                [data setObject:@"clear_beacon" forKey:@"beacon_event"];
                [Tealium trackCallType:TealiumEventCall customData:data object:nil];
            }
        }
        
        @synchronized(self){
            _lastBeaconTracked = beaconToTrack;
        }

        NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
        [data setObject:@"did_range_beacons" forKey:@"beacon_event"];
        [data setObject:beaconToTrack.proximityUUID forKey:@"proximityUUID"];

        [Tealium trackCallType:TealiumEventCall customData:data object:nil];
    }
    else if (_lastBeaconTracked != nil){
        NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
        [data setObject:@"clear_beacon" forKey:@"beacon_event"];
        [Tealium trackCallType:TealiumEventCall customData:data object:nil];
    }
    
}

@end
#endif

@implementation __TLMADBMobile

+ (__VendorMethod)setPrivacyStatus{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        NSString *privacyStatus = [data valueForKey:@"privacy_status"];
        if (privacyStatus) {
            ADBMobilePrivacyStatus status = [privacyStatus integerValue];
            [ADBMobile setPrivacyStatus:status];
        }
        return responseData;
    };
}

+ (__VendorMethod)setUserIdentifier{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *identifier = [data objectForKey:@"identifier"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        
        if (data == nil || identifier == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }

        [ADBMobile setUserIdentifier:identifier];

        return responseData;
    };
}
+ (__VendorMethod)collectLifecycleData{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        NSString *filePath = [[NSBundle mainBundle] pathForResource:@"ADBMobileConfig" ofType:@"json"];
        NSString *jsonString = [data valueForKey:@"config_json"];
        NSDictionary *configJSON = nil;
        if(jsonString){
            NSData *configData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
            if (configData){
                NSError *error;
                configJSON = [NSJSONSerialization JSONObjectWithData:configData options:0 error:&error];
                NSOutputStream *os = [[NSOutputStream alloc] initToFileAtPath:filePath append:NO];
                
                [os open];
                [NSJSONSerialization writeJSONObject:configJSON toStream:os options:0 error:nil];
                [os close];
                
                [ADBMobile overrideConfigPath:filePath];
            }
        }
        
        [ADBMobile collectLifecycleData];
        
        return responseData;
    };
}
+ (__VendorMethod)keepLifecycleSessionAlive{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [ADBMobile keepLifecycleSessionAlive];
        
        return responseData;
    };
}
+ (__VendorMethod)trackState{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *stateName         = [data objectForKey:@"state_name"];
        NSDictionary *customData    = [data objectForKey:@"custom_data"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        
        if (data == nil || stateName == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        if(!customData){
            [ADBMobile trackState:stateName data:nil];
        }
        else{
            [ADBMobile trackState:stateName data:customData];
        }
        
        
        return responseData;
    };
}
+ (__VendorMethod)trackAction{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *actionName         = [data objectForKey:@"action_name"];
        NSDictionary *customData    = [data objectForKey:@"custom_data"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        
        if (data == nil || actionName == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        if(!customData){
            [ADBMobile trackAction:actionName data:nil];
        }
        else{
            [ADBMobile trackAction:actionName data:customData];
        }
        
        
        return responseData;
    };
}
+ (__VendorMethod)trackActionFromBackground{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *actionName         = [data objectForKey:@"action_name"];
        NSDictionary *customData    = [data objectForKey:@"custom_data"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        
        if (data == nil || actionName == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        if(!customData){
            [ADBMobile trackActionFromBackground:actionName data:nil];
        }
        else{
            [ADBMobile trackActionFromBackground:actionName data:customData];;
        }
        
        
        return responseData;
    };
}
+ (__VendorMethod)trackLifetimeValueIncrease{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *rawAmmount = [data objectForKey:@"ammount"];
        NSDecimalNumber *amount = [NSDecimalNumber decimalNumberWithString:rawAmmount];
        NSDictionary *customData = [data objectForKey:@"custom_data"];
        
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || rawAmmount == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        if (customData) {
            [ADBMobile trackLifetimeValueIncrease:amount data:nil];
        }
        else{
            [ADBMobile trackLifetimeValueIncrease:amount data:customData];
        }
        
        [responseData setValue:[ADBMobile lifetimeValue] forKey:@"responseBody"];
        
        return responseData;
    };
}
+ (__VendorMethod)trackLocation{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        if (data == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [responseData setValue:@500 forKey:@"responseCode"];
        [responseData setValue:@"CoreLocation not installed" forKey:@"responseBody"];
#ifdef __CORELOCATION__
        [responseData setValue:@200 forKey:@"responseCode"];
        [responseData removeObjectForKey:@"responseBody"];
        CLLocation *location = [self locationWithData:data];
        
        if (location == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"CoreLocation not installed" forKey:@"responseBody"];
            return responseData;
        }
        
        NSDictionary *customData = [data objectForKey:@"custom_data"];
        
        if (customData) {
            [ADBMobile trackLocation:location data:nil];
        }
        else {
            [ADBMobile trackLocation:location data:customData];
        }
#endif

        return responseData;
    };
}
+ (__VendorMethod)trackBeacon{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [responseData setValue:@500 forKey:@"responseCode"];
        [responseData setValue:@"CoreLocation not installed" forKey:@"responseBody"];
#ifdef __CORELOCATION__
        if (NSFoundationVersionNumber >= NSFoundationVersionNumber_iOS_7_0) {
            CLBeacon *beacon = [[__TLMADBMobileLocation sharedInstance] getLastBeaconTracked];
            NSString *proximityUUIDString = data[@"proximity_uuid"];
            
            if (proximityUUIDString) {
                if ([beacon.proximityUUID isEqual:proximityUUIDString]) {
                    NSDictionary *customData = [data objectForKey:@"custom_data"];
                    
                    if (!customData){
                        [ADBMobile trackBeacon:beacon data:nil];
                    }
                    else{
                        [ADBMobile trackBeacon:beacon data:customData];
                    }
                    [responseData setValue:@"Beacon tracked" forKey:@"responseBody"];
                    [responseData setValue:@200 forKey:@"responseCode"];
                    
                    return responseData;
                }
                else {
                    [responseData setValue:@500 forKey:@"responseCode"];
                    [responseData setValue:@"No valid UUID for beacon present" forKey:@"responseBody"];
                }
            }
            else {
                [responseData setValue:@500 forKey:@"responseCode"];
                [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            }
            
        }
        else {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"OS version < 7.0" forKey:@"responseBody"];
        }
#endif

        return responseData;
    };
}


+ (__VendorMethod)clearTrackingBeacon{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        
        [responseData setValue:@500 forKey:@"responseCode"];
        [responseData setValue:@"CoreLocation not installed" forKey:@"responseBody"];
#ifdef __CORELOCATION__
        [ADBMobile trackingClearCurrentBeacon];
        [responseData setValue:@"Beacon cleared" forKey:@"responseBody"];
        [responseData setValue:@200 forKey:@"responseCode"];
#endif
        return responseData;
    };
}


+ (__VendorMethod)trackTimedActionStart{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *action            = [data objectForKey:@"action"];
        NSDictionary *customData    = [data objectForKey:@"custom_data"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || action == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }

        if (data == nil) {
            [ADBMobile trackTimedActionStart:action data:nil];
        }
        else {
            [ADBMobile trackTimedActionStart:action data:customData];
        }
        
        return responseData;
    };
}
+ (__VendorMethod)trackTimedActionUpdate{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *action            = [data valueForKey:@"action"];
        NSDictionary *customData    = [data valueForKey:@"custom_data"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || action == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }

        if (data == nil) {
            [ADBMobile trackTimedActionUpdate:action data:nil];
        }
        else {
            [ADBMobile trackTimedActionUpdate:action data:customData];
        }
        
        return responseData;
    };
}
+ (__VendorMethod)trackTimedActionEnd{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *action            = [data valueForKey:@"action"];
        NSDictionary *customData    = [data valueForKey:@"custom_data"];
        NSString *shouldSendEvent   = [data valueForKey:@"should_send_event"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || action == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        BOOL shouldSend = YES;
        
        if (shouldSendEvent) {
            shouldSend = [shouldSendEvent boolValue];
        }
        
        [ADBMobile trackTimedActionEnd:action logic:^BOOL(NSTimeInterval inAppDuration, NSTimeInterval totalDuration, NSMutableDictionary *data) {
            
            if (customData) {
                [data addEntriesFromDictionary:customData];
            }
            // do something with custom data
            
            // put any custom handling here
            return shouldSend;
        }];
        
        return responseData;
    };
}
+ (__VendorMethod)trackingSendQueuedHits{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        
        [ADBMobile trackingSendQueuedHits];
        
        return responseData;
    };
}
+ (__VendorMethod)trackingClearQueue{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        
        [ADBMobile trackingClearQueue];
        
        return responseData;
    };
}

+ (__VendorMethod)mediaClose{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *name = [data valueForKey:@"name"];
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || name == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [ADBMobile mediaClose:name];
        
        return responseData;
    };
}
+ (__VendorMethod)mediaPlay{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *name = [data valueForKey:@"name"];
        NSString *offset = [data valueForKey:@"offset"];
        double offsetValue = 0.0;
        
        if (offset) {
            offsetValue = [offset doubleValue];
        }
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || name == nil || offset == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [ADBMobile mediaPlay:name offset:offsetValue];
        
        return responseData;
    };
}
+ (__VendorMethod)mediaComplete{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *name = [data valueForKey:@"name"];
        NSString *offset = [data valueForKey:@"offset"];
        double offsetValue = 0.0;
        
        if (offset) {
            offsetValue = [offset doubleValue];
        }
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || name == nil || offset == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [ADBMobile mediaComplete:name offset:offsetValue];
        
        return responseData;
    };
}
+ (__VendorMethod)mediaStop{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *name = [data valueForKey:@"name"];
        NSString *offset = [data valueForKey:@"offset"];
        double offsetValue = 0.0;
        
        if (offset) {
            offsetValue = [offset doubleValue];
        }
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || name == nil || offset == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [ADBMobile mediaStop:name offset:offsetValue];
        
        return responseData;
    };
}
+ (__VendorMethod)mediaClick{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *name = [data valueForKey:@"name"];
        NSString *offset = [data valueForKey:@"offset"];
        double offsetValue = 0.0;
        
        if (offset) {
            offsetValue = [offset doubleValue];
        }
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil || name == nil || offset == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        [ADBMobile mediaClick:name offset:offsetValue];
        
        return responseData;
    };
}
+ (__VendorMethod)mediaTrack{
    return ^NSDictionary* (__weak NSDictionary *data){
        NSString *name = [data valueForKey:@"name"];
        NSString *offset = [data valueForKey:@"offset"];
        NSDictionary *customData = [data valueForKey:@"custom_data"];
        double offsetValue = 0.0;
        
        if (offset) {
            offsetValue = [offset doubleValue];
        }
        NSMutableDictionary *responseData = [[NSMutableDictionary alloc] init];
        [responseData setValue:@200 forKey:@"responseCode"];
        if (data == nil) {
            [responseData setValue:@500 forKey:@"responseCode"];
            [responseData setValue:@"No arguments found" forKey:@"responseBody"];
            return responseData;
        }
        
        if (customData == nil){
            [ADBMobile mediaTrack:name data:nil];
        }
        else{
            [ADBMobile mediaTrack:name data:customData];
        }
        [ADBMobile mediaTrack:name data:customData];
        
        return responseData;
    };
}

#ifdef __CORELOCATION__
+ (CLLocation *)locationWithData:(NSDictionary *)data
{
    if (data == nil) {
        return nil;
    }
    
    NSString *latitude = [data valueForKey:@"latitude"];
    NSString *longitude = [data valueForKey:@"longitude"];
    
    if (latitude == nil || longitude == nil) {
        return nil;
    }
    
    CLLocationDegrees latitudeValue = [latitude doubleValue];
    CLLocationDegrees longitudeValue = [longitude doubleValue];
    
    return [[CLLocation alloc] initWithLatitude:latitudeValue
                                      longitude:longitudeValue];
}
#endif

@end



@implementation TealiumADBMobileTagBridge


NSString *const ADOBE_VENDOR_NAME = @"adobe";
NSString *const ADOBE_VENDOR_DESCRIPTION = @"Adobe Tag Bridge";

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken = 0;
    __strong static TealiumADBMobileTagBridge *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[TealiumADBMobileTagBridge alloc] init];
    });

    return _sharedObject;
}

- (void)addRemoteCommandHandlers
{
    // Customize CommandID and description
    [Tealium addRemoteCommandId:ADOBE_VENDOR_NAME
                    description:ADOBE_VENDOR_DESCRIPTION
                    targetQueue:self.adobeDispatchQueue
                          block:^(TealiumRemoteCommandResponse *response) {
                              
                              NSString *methodString = response.requestPayload[@"method"];
                              NSDictionary *data = response.requestPayload[@"arguments"];
                              NSDictionary *responseData = [self handleMethodOfType:methodString
                                                                      withInputData:data];
                              NSString *responseBody = [responseData objectForKey:@"responseBody"];
                              NSNumber *responseCode = [responseData objectForKey:@"responseCode"];
                              if ([responseCode  isEqual:@200]) {

                                  NSDictionary *responseJSON = nil;
                                  NSError *error = nil;
                                  if(responseBody != nil){
                                      NSData *responseData = [responseBody dataUsingEncoding:NSUTF8StringEncoding];
                                      if (responseData){
                                          responseJSON = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:&error];
                                      }
                                      if(responseJSON){
                                          response.body = [[NSString alloc] initWithData:responseData
                                                                                encoding:NSUTF8StringEncoding];
                                      }
                                      else if (error){
                                          NSString *errorOutput = [[NSString alloc] stringByAppendingFormat: @"problem serializing response body: %@", [error localizedDescription]];
                                          response.body = errorOutput;
                                          NSLog(@"%@", errorOutput);

                                      }
                                  }
                                  else{
                                      response.body = @"";
                                  }
                              }
                              else {
                                  response.status = TealiumRC_Failure;
                              }
                              
                              [response send];
                          }];
}


- (instancetype)init
{
    self = [super init];
    
    if (self) {
        _adobeDispatchQueue = dispatch_queue_create("com.tealium.tagbridge.adobe", NULL);
        
        // Add methods here
        _adobeMethodStrings = [[NSMutableDictionary alloc] init];
        _adobeMethodStrings[@"set_privacy_status"] = [__TLMADBMobile setPrivacyStatus];
        _adobeMethodStrings[@"set_user_identifier"] = [__TLMADBMobile setUserIdentifier];
        _adobeMethodStrings[@"collect_lifecycle_data"] = [__TLMADBMobile collectLifecycleData];
        _adobeMethodStrings[@"keep_lifecycle_session_alive"] = [__TLMADBMobile keepLifecycleSessionAlive];
        _adobeMethodStrings[@"track_state"] = [__TLMADBMobile trackState];
        _adobeMethodStrings[@"track_action"] = [__TLMADBMobile trackAction];
        _adobeMethodStrings[@"track_action_from_background"] = [__TLMADBMobile trackActionFromBackground];
        _adobeMethodStrings[@"track_lifetime_value_increase"] = [__TLMADBMobile trackLifetimeValueIncrease];
        _adobeMethodStrings[@"track_location"] = [__TLMADBMobile trackLocation];
        _adobeMethodStrings[@"track_beacon"] = [__TLMADBMobile trackBeacon];
        _adobeMethodStrings[@"clear_tracking_beacon"] = [__TLMADBMobile clearTrackingBeacon];
        _adobeMethodStrings[@"track_timed_action_start"] = [__TLMADBMobile trackTimedActionStart];
        _adobeMethodStrings[@"track_timed_action_update"] = [__TLMADBMobile trackTimedActionUpdate];
        _adobeMethodStrings[@"track_timed_action_end"] = [__TLMADBMobile trackTimedActionEnd];
        _adobeMethodStrings[@"tracking_send_queued_hits"] = [__TLMADBMobile trackingSendQueuedHits];
        _adobeMethodStrings[@"tracking_clear_queue"] = [__TLMADBMobile trackingClearQueue];
        _adobeMethodStrings[@"media_close"] = [__TLMADBMobile mediaClose];
        _adobeMethodStrings[@"media_play"] = [__TLMADBMobile mediaPlay];
        _adobeMethodStrings[@"media_complete"] = [__TLMADBMobile mediaComplete];
        _adobeMethodStrings[@"media_stop"] = [__TLMADBMobile mediaStop];
        _adobeMethodStrings[@"media_click"] = [__TLMADBMobile mediaClick];
        _adobeMethodStrings[@"media_track"] = [__TLMADBMobile mediaTrack];

    }
    return self;
}


- (NSDictionary *)handleMethodOfType:(NSString*)methodType
             withInputData:(NSDictionary *)inputData
{
    __VendorMethod thisVendorMethod = (__VendorMethod)[_adobeMethodStrings objectForKey:methodType];
    NSDictionary *responseDict = [[NSDictionary alloc] init];
    if (thisVendorMethod){
        responseDict = thisVendorMethod(inputData);
    }
    else{
        NSLog(@"unsupported method type: %@", methodType);
        [responseDict setValue:@500 forKey:@"responseCode"];
        [responseDict setValue:@"No method found" forKey:@"responseBody"];
        return responseDict;
    }
    
    return responseDict;
}



@end



