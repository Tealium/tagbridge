//
//  TealiumADBMobileTagBridge_Tests.m
//  Modules_UICatelog
//
//  Created by George Webster on 12/17/14.
//  Copyright (c) 2014 f. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "TealiumADBMobileTagBridge.h"

@interface TealiumADBMobileTagBridge ()
- (TealiumADBMobileMethod)methodFromString:(NSString *)value;
@end

@interface TealiumADBMobileTagBridge_Tests : XCTestCase

@end

@implementation TealiumADBMobileTagBridge_Tests

- (void)setUp {
    [super setUp];

}

- (void)tearDown {
    [super tearDown];
}

- (void)testMethodFromString {

    TealiumADBMobileMethod methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"set_privacy_status"];
    
    XCTAssertEqual(TealiumADBMobileMethodSetPrivacyStatus, methodType, @"returned method type should match");

    methodType = TealiumADBMobileMethodNone;

    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"set_user_identifier"];
    
    XCTAssertEqual(TealiumADBMobileMethodSetUserIdentifier, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"collect_lifecycle_data"];
    
    XCTAssertEqual(TealiumADBMobileMethodCollectLifecycleData, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"keep_lifecycle_session_alive"];
    
    XCTAssertEqual(TealiumADBMobileMethodKeepLifecycleSessionAlive, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_state"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackState, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_action"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackAction, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_action_from_background"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackActionFromBackground, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;

    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_lifetime_value_increase"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackLifetimeValueIncrease, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_location"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackLocation, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_beacon"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackBeacon, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"tracking_clear_current_beacon"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackingClearCurrentBeacon, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_timed_action_start"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackTimedActionStart, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_timed_action_update"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackTimedActionUpdate, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"track_timed_action_end"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackTimedActionEnd, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"tracking_send_queued_hits"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackingSendQueuedHits, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"tracking_clear_queue"];
    
    XCTAssertEqual(TealiumADBMobileMethodTrackingClearQueue, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"media_close"];
    
    XCTAssertEqual(TealiumADBMobileMethodMediaClose, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"media_play"];
    
    XCTAssertEqual(TealiumADBMobileMethodMediaPlay, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"media_complete"];
    
    XCTAssertEqual(TealiumADBMobileMethodMediaComplete, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;

    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"media_stop"];
    
    XCTAssertEqual(TealiumADBMobileMethodMediaStop, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"media_click"];
    
    XCTAssertEqual(TealiumADBMobileMethodMediaClick, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
    methodType = [[TealiumADBMobileTagBridge sharedInstance] methodFromString:@"media_track"];
    
    XCTAssertEqual(TealiumADBMobileMethodMediaTrack, methodType, @"returned method type should match");
    
    methodType = TealiumADBMobileMethodNone;
    
}

@end
