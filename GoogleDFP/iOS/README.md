Tealium + GoogleDFP Integration
===============================

###Brief###

The Tealium+GoogleDFP_Integration folder includes all the files needed to remotely configure the Google DFP library through [Tealium IQ](http://tealium.com/products/enterprise-tag-management/); all without needing to recode and redeploy an app within the scope of the integration.

First time implementations should read the [How Tealium Works](../../wiki/how-tealium-works) wiki page for a brief overview of how Tealium's SDK differs from conventional analytic SDKs. For any additional information, consult your Tealium account manager.

The remainder of this document provides quick install instructions for implementing the integration into your own project.

###Table of Contents###

- [Requirements](#requirements)
- [Quick Start](#quick-start)
    - [1. Add to Project](#1-add-to-project)
    - [2. Import Headers](#2-import-headers)
    - [3. Init Integration](#3-init-integration)
    - [4. TIQ Requirement](#4-tiq-requirement)
- [Contact Us](#contact-us)

###Requirements###

- [XCode (6.0+ recommended)](https://developer.apple.com/xcode/downloads/)
- Minimum target iOS Version 7.0+

###Quick Start###
This guide presumes you have already created an [iOS app using XCode](https://developer.apple.com/library/iOS/referencelibrary/GettingStarted/RoadMapiOS/index.html), added the [Google DFP library](https://developers.google.com/mobile-ads-sdk/docs/dfp/ios/quick-start), and added the [Tealium Full library](https://github.com/Tealium/ios-library).

####1. Add To Project 

Drag and drop the *Tealium+GoogleDFP_Integration" folder into your project.


####2. Import Headers

```objective-c

#import <TealiumLibrary/Tealium.h>
#import "TEALGoogleDFPRemoteCommands.h"

```

####3. Init Integration

```objective-c
- (void)applicationDidFinishLaunching:(UIApplication *)application
{
    //...

    [[TEALGoogleDFPRemoteCommands sharedInstance] enable];
    [[TEALGoogleDFPRemoteCommands sharedInstance] enableLogs:YES];

    //...

}
```

####4. TIQ Requirement

Add the Google DFP Mobile Tag to the TIQ account-profile specified in the Tealium Library init.


###Contact Us###

Questions or comments?

- Post code questions in the [issues page.](../../issues)
- Contact your Tealium account manager


--------------------------------------------

Copyright (C) 2012-2015, Tealium Inc.
