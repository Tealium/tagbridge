//
//  TealiumLocationTagBridge.m
//  Modules_UICatelog
//
//  Created by George Webster on 12/17/14.
//  Copyright (c) 2014 f. All rights reserved.
//

#import "TealiumLocationTagBridge.h"
#import <CoreLocation/CoreLocation.h>
#import <TealiumLibrary/Tealium.h>

@interface TealiumLocationTagBridge() <CLLocationManagerDelegate>

@property (nonatomic, strong) dispatch_queue_t dispatchQueue;

@property (nonatomic, strong) CLLocationManager *locationManager;

@end

@implementation TealiumLocationTagBridge

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken = 0;
    __strong static TealiumLocationTagBridge *_sharedObject = nil;
    
    dispatch_once(&onceToken, ^{
        _sharedObject = [[TealiumLocationTagBridge alloc] init];
    });
    
    return _sharedObject;
}

- (instancetype)init
{
    self = [super init];
    
    if (self) {
        _dispatchQueue = dispatch_queue_create("com.tealium.tagbridge.location", NULL);
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
                              
                              NSDictionary *data = response.requestPayload[@"arguments"];
                              
                              if ([self handleMethod:methodString withData:data]) {
                                  response.status = TealiumRC_Success;
                              } else {
                                  response.status = TealiumRC_Failure;
                              }
                              
                              [response send];
                          }];
}

- (BOOL)handleMethod:(NSString *)method withData:(NSDictionary *)data
{
    BOOL wasSuccess = NO;

    return wasSuccess;
}


#pragma mark - CLLocationManager delegate

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    CLLocation *currentLocation = [locations lastObject];
    
    if (currentLocation.horizontalAccuracy <= 100 && currentLocation.verticalAccuracy <= 100) {
        
        [self.locationManager stopUpdatingLocation];
        
        NSString *methodName = @"catpure_current_location";
        
        NSDictionary *customData = @{
                                     @"method": methodName,
                                     @"latitude" : @(currentLocation.coordinate.latitude),
                                     @"longitude"  : @(currentLocation.coordinate.longitude)
                                     };
        
        
        [Tealium trackCallType:TealiumEventCall
                    customData:customData
                        object:nil];
        
    }
}

- (BOOL)captureCurrentLocation
{
    //current location
    self.locationManager = [[CLLocationManager alloc] init];
    
    self.locationManager.delegate                             = self;
    self.locationManager.desiredAccuracy                      = kCLLocationAccuracyBest;
    self.locationManager.pausesLocationUpdatesAutomatically   = NO;
    
    [self.locationManager startUpdatingLocation];
    
    return YES;
}


@end
