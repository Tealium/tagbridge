//
//  TealiumADBMobileTagBridge.m
//  UICatalog
//
//  Created by George Webster on 10/29/14.
//  Copyright (c) 2014 f. All rights reserved.
//

#import "TealiumADBMobileTagBridge.h"
#import <TealiumLibrary/Tealium.h>
#import <CoreLocation/CoreLocation.h>
#import "ADBMobile.h"




@interface TealiumADBMobileTagBridge()

@property (nonatomic, strong) dispatch_queue_t dispatchQueue;

@property (nonatomic, strong) NSArray *methodStrings;

#ifdef __CORELOCATION__
@property (nonatomic, strong) NSMutableDictionary *beacons;
#endif


@end

@implementation TealiumADBMobileTagBridge

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken = 0;
    __strong static TealiumADBMobileTagBridge *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[TealiumADBMobileTagBridge alloc] init];
    });
    
    return _sharedObject;
}

- (instancetype)init
{
    self = [super init];
    
    if (self) {
        _dispatchQueue = dispatch_queue_create("com.tealium.tagbridge.adobe", NULL);
        
        _methodStrings = @[@"none",
                           @"set_privacy_status",
                           @"set_user_identifier",
                           @"collect_lifecycle_data",
                           @"keep_lifecycle_session_alive",
                           @"track_state",
                           @"track_action",
                           @"track_action_from_background",
                           @"track_lifetime_value_increase",
                           @"track_location",
                           @"track_beacon",
                           @"tracking_clear_current_beacon",
                           @"track_timed_action_start",
                           @"track_timed_action_update",
                           @"track_timed_action_end",
                           @"tracking_send_queued_hits",
                           @"tracking_clear_queue",
                           @"media_close",
                           @"media_play",
                           @"media_complete",
                           @"media_stop",
                           @"media_click",
                           @"media_track"
                           ];
    }
    return self;
}

- (void)addRemoteCommandHandlers
{
    
    [Tealium addRemoteCommandId:@"adobe"
                    description:@"ADBMobile Tag Bridge"
                    targetQueue:self.dispatchQueue
                          block:^(TealiumRemoteCommandResponse *response) {

                              NSString *methodString = response.requestPayload[@"method"];

                              [self updateDebugValuesFromPlayload:response.requestPayload];

                              TealiumADBMobileMethod method = [self methodFromString:methodString];

                              NSDictionary *data = response.requestPayload[@"arguments"];

                              NSDictionary *output = nil;
                              
                              if ([self handleMethodOfType:method
                                             withInputData:data
                                                outputData:&output]) {
                                  response.status = TealiumRC_Success;
                                  
                                  if (output) {
                                      NSError *error = nil;
                                      NSData *jsonData = [NSJSONSerialization dataWithJSONObject:output
                                                                                         options:0
                                                                                           error:&error];
                                      if (jsonData) {
                                          response.body = [[NSString alloc] initWithData:jsonData
                                                                                encoding:NSUTF8StringEncoding];
                                      } else if (error) {
                                          response.status = TealiumRC_NoContent;
                                          NSLog(@"problem serializing response body: %@", [error localizedDescription]);
                                      } else {
                                          response.status = TealiumRC_NoContent;
                                      }
                                  }
                                  
                              } else {
                                  response.status = TealiumRC_Failure;
                              }
                              
                              [response send];
                          }];
}

- (TealiumADBMobileMethod)methodFromString:(NSString *)value
{
    if (value == nil || ![value respondsToSelector:@selector(isEqualToString:)]) {
        return TealiumADBMobileMethodNone;
    }
    
    NSString *stringToCompare = [value lowercaseString];
    NSUInteger index = [self.methodStrings indexOfObject:stringToCompare];
    
    if (index == NSNotFound) {
        return TealiumADBMobileMethodNone;
    }
    
    return (TealiumADBMobileMethod)index;
}

- (BOOL)handleMethodOfType:(TealiumADBMobileMethod)methodType
             withInputData:(NSDictionary *)inputData
                outputData:(NSDictionary * __autoreleasing *)outputData
{
    BOOL wasSuccess = NO;

    switch (methodType) {
        case TealiumADBMobileMethodNone:
            wasSuccess = NO;
            break;
        case TealiumADBMobileMethodSetPrivacyStatus:
            
            wasSuccess = [self setPrivacyStatusWithData:inputData];
            break;
        case TealiumADBMobileMethodSetUserIdentifier:
            wasSuccess = [self setUserIdentifierWithData:inputData];
            break;
        case TealiumADBMobileMethodCollectLifecycleData:

            [ADBMobile collectLifecycleData];
            wasSuccess = YES;
            break;
        case TealiumADBMobileMethodKeepLifecycleSessionAlive:
            [ADBMobile keepLifecycleSessionAlive];
            wasSuccess = YES;
            break;
        case TealiumADBMobileMethodTrackState:
            wasSuccess = [self trackStateWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackAction:
            wasSuccess = [self trackActionWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackActionFromBackground:
            wasSuccess = [self trackActionFromBackgroundWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackLifetimeValueIncrease:
            wasSuccess = [self trackLifetimeValueIncreaseWithInputData:inputData
                                                            outputData:outputData];
            break;
        case TealiumADBMobileMethodTrackLocation:
            wasSuccess = [self trackLocationWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackBeacon:
            wasSuccess = [self trackBeaconWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackingClearCurrentBeacon:
            [ADBMobile trackingClearCurrentBeacon];
            wasSuccess = YES;
            break;
        case TealiumADBMobileMethodTrackTimedActionStart:
            wasSuccess = [self trackTimedActionStartWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackTimedActionUpdate:
            wasSuccess = [self trackTimedActionUpdateWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackTimedActionEnd:
            wasSuccess = [self trackTimedActionEndWithData:inputData];
            break;
        case TealiumADBMobileMethodTrackingSendQueuedHits:
            [ADBMobile trackingSendQueuedHits];
            wasSuccess = YES;
            break;
        case TealiumADBMobileMethodTrackingClearQueue:
            [ADBMobile trackingClearQueue];
            wasSuccess = YES;
            break;
        case TealiumADBMobileMethodMediaClose:
        case TealiumADBMobileMethodMediaPlay:
        case TealiumADBMobileMethodMediaComplete:
        case TealiumADBMobileMethodMediaStop:
        case TealiumADBMobileMethodMediaClick:
        case TealiumADBMobileMethodMediaTrack:
            wasSuccess = [self mediaTrackOfType:methodType withData:inputData];
            break;
        default:
            NSLog(@"unsupported method type: %ld", methodType);
            wasSuccess = NO;
            break;
    }
    
    return wasSuccess;
}

- (void)updateDebugValuesFromPlayload:(NSDictionary *)payload
{
    NSString *debug = [payload valueForKey:@"debug"];
    if (debug && [debug isEqualToString:@"true"]) {
        [ADBMobile setDebugLogging:YES];
    } else {
        [ADBMobile setDebugLogging:NO];
    }
}

#pragma mark - Settings

- (BOOL)setPrivacyStatusWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    NSString *privacyStatus = [data valueForKey:@"privacy_status"];
    if (privacyStatus) {
        ADBMobilePrivacyStatus status = [privacyStatus integerValue];
        [ADBMobile setPrivacyStatus:status];
    }
    
    return YES;
}

- (BOOL)setUserIdentifierWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    NSString *identifier = [data valueForKey:@"identifier"];
    
    if (identifier) {
        [ADBMobile setUserIdentifier:identifier];
        return YES;
    }
    return NO;
}


#pragma mark - Tracking
- (BOOL)trackStateWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }

    NSString *stateName         = [data valueForKey:@"state_name"];
    NSDictionary *customData    = [data valueForKey:@"custom_data"];
    
    [ADBMobile trackState:stateName data:customData];
    
    return YES;
}

- (BOOL)trackActionWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    NSString *actionName        = [data valueForKey:@"action_name"];
    NSDictionary *customData    = [data valueForKey:@"custom_data"];
    
    [ADBMobile trackAction:actionName data:customData];
    
    return YES;
}

- (BOOL)trackActionFromBackgroundWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    NSString *actionName        = [data valueForKey:@"action_name"];
    NSDictionary *customData    = [data valueForKey:@"custom_data"];
    
    [ADBMobile trackActionFromBackground:actionName data:customData];
    
    return YES;
}

#pragma mark - Lifetime Value

- (BOOL)trackLifetimeValueIncreaseWithInputData:(NSDictionary *)inputData outputData:(NSDictionary * __autoreleasing *)outputData
{
    if (inputData == nil) {
        return NO;
    }
    
    NSString *rawAmmount        = [inputData valueForKey:@"ammount"];
    
    NSDecimalNumber *ammount    = [NSDecimalNumber decimalNumberWithString:rawAmmount];
    NSDictionary *customData    = [inputData valueForKey:@"custom_data"];

    [ADBMobile trackLifetimeValueIncrease:ammount data:customData];
    
    *outputData = @{ @"lifetime_value" : [ADBMobile lifetimeValue] };
    
    return YES;
}

#ifdef __CORELOCATION__

- (BOOL) trackLocationWithData:(NSDictionary *)data
{
    
    if (data == nil) {
        return NO;
    }
    
    CLLocation *location = [self locationWithData:data];
    
    if (location == nil) {
        return NO;
        
    }

    NSDictionary *customData = [data valueForKey:@"custom_data"];

    [ADBMobile trackLocation:location data:customData];
    
    return YES;
}

- (CLLocation *)locationWithData:(NSDictionary *)data
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


#pragma mark - Beacon

- (BOOL) trackBeaconWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    if (NSFoundationVersionNumber >= NSFoundationVersionNumber_iOS_7_0) {

        NSString *proximityUUIDString = data[@"proximity_uuid"];
        
        if (proximityUUIDString) {
            CLBeacon *beacon = self.beacons[proximityUUIDString];
            
            if (beacon) {
                NSDictionary *customData = [data valueForKey:@"custom_data"];
                
                [ADBMobile trackBeacon:beacon data:customData];
                
                [self.beacons removeObjectForKey:proximityUUIDString];
                
                return YES;
            }
        }
    }
    
    return NO;
}

- (void)addTrackingBeacon:(CLBeacon *)beacon
{
    if (NSFoundationVersionNumber >= NSFoundationVersionNumber_iOS_7_0) {

        NSUUID *proximityUUID = [beacon proximityUUID];
        
        if (proximityUUID) {
            NSString *key = [proximityUUID UUIDString];

            self.beacons[key] = beacon;

            [Tealium trackCallType:TealiumEventCall
                        customData:@{@"beacon_added_with_uuid":key}
                            object:nil];
        }
            
    }
}
#endif

#pragma mark - Timed Actions

- (BOOL) trackTimedActionStartWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    NSString *action            = [data valueForKey:@"action"];
    NSDictionary *customData    = [data valueForKey:@"custom_data"];

    [ADBMobile trackTimedActionStart:action data:customData];
    
    
    return YES;
}

- (BOOL) trackTimedActionUpdateWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    NSString *action            = [data valueForKey:@"action"];
    NSDictionary *customData    = [data valueForKey:@"custom_data"];

    [ADBMobile trackTimedActionUpdate:action data:customData];
    
    return YES;
}

- (BOOL)trackTimedActionEndWithData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }
    
    NSString *action            = [data valueForKey:@"action"];
    NSDictionary *customData    = [data valueForKey:@"custom_data"];
    NSString *shouldSendEvent   = [data valueForKey:@"should_send_event"];

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
    
    return YES;
}

#pragma mark - Media / Video Analytics

- (BOOL) mediaTrackOfType:(TealiumADBMobileMethod)method withData:(NSDictionary *)data
{
    if (data == nil) {
        return NO;
    }

    NSString *name = [data valueForKey:@"name"];
    NSString *offset = [data valueForKey:@"offset"];
    double offsetValue = 0.0;
    
    if (offset) {
        offsetValue = [offset doubleValue];
    }

    switch(method) {
        case TealiumADBMobileMethodMediaClose:
            [ADBMobile mediaClose:name];
            break;
        case TealiumADBMobileMethodMediaPlay:
            [ADBMobile mediaPlay:name offset:offsetValue];
            break;
        case TealiumADBMobileMethodMediaComplete:
            [ADBMobile mediaComplete:name offset:offsetValue];
            break;
        case TealiumADBMobileMethodMediaStop:
            [ADBMobile mediaStop:name offset:offsetValue];
            break;
        case TealiumADBMobileMethodMediaClick:
            [ADBMobile mediaClick:name offset:offsetValue];
            break;
        case TealiumADBMobileMethodMediaTrack:
        {
            NSDictionary *customData = [data valueForKey:@"custom_data"];

            [ADBMobile mediaTrack:name data:customData];
        }
            break;
        default:
            NSLog(@"unsupported media track method");
            break;
    }
    
    return YES;
}

@end
