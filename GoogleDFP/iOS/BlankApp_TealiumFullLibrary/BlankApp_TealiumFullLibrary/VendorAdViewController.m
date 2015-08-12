//
//  VendorAdViewController.m
//  BlankApp_TealiumFullLibrary
//
//  Created by Patrick McWilliams on 6/7/15.
//  Copyright (c) 2015 tealium. All rights reserved.
//

#import "VendorAdViewController.h"
#import "TEALGoogleDFPRemoteCommands.h"

@interface VendorAdViewController ()

@end

@implementation VendorAdViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [[TEALGoogleDFPRemoteCommands sharedInstance] setActiveViewController:self];
}

@end
