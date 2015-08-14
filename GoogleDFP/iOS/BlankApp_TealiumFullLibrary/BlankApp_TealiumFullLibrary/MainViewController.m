//
//  VendorAdViewController.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Patrick McWilliams on 6/7/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "MainViewController.h"
#import "TEALGoogleDFPRemoteCommands.h"

@interface MainViewController ()

@end

@implementation MainViewController

- (void) viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[TEALGoogleDFPRemoteCommands sharedInstance] setActiveViewController:self];
    [[TEALGoogleDFPRemoteCommands sharedInstance] refresh];
}
@end
