//
//  SecondViewController.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Jason Koo on 8/14/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "SecondViewController.h"
#import "TEALGoogleDFPRemoteCommands.h"

@implementation SecondViewController


- (void) viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[TEALGoogleDFPRemoteCommands sharedInstance] setActiveViewController:self];
    [[TEALGoogleDFPRemoteCommands sharedInstance] refresh];
}

@end
