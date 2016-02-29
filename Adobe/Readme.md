
# Adobe TagBridge Module

This module is a wrapper around the native Adobe Marketing Cloud SDKs for Android and iOS. By adding TagBridge commands to the Tealium SDK control of the Adobe mobile SDK is now possible via a tag congifured in [Tealium iQ](https://my.tealiumiq.com).  

## Resources

* [API Reference](api_reference.md)
* [Adobe Mobile Servics ](https://mobilemarketing.adobe.com) ( Native SDK download / reference )
* [iOS Library Setup](#ios-library-setup)
* [iOS Adobe Method Reference](https://marketing.adobe.com/resources/help/en_US/mobile/ios/methods.html)
* [Android Library Setup](#android-library-setup)
* [Android Adobe Method Reference](https://marketing.adobe.com/resources/help/en_US/mobile/android/methods.html)

---

## Setup

###iOS Library Setup:

If you are currently using the Adobe native SDK replace the Adobe import statement with the Tealium library and TagBridge module import statements:
```objective-c
#import "ADBMobile.h"
```

Tealium import statements:

```objective-c
#import <TealiumLibrary/Tealium.h>
#import "TealiumADBMobileTagBridge.h"
```

For most implementations a single method to add the main Adobe tag bridge remote command after Tealium is initialized is all that is needed:

```objective-c
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

	//...
	
    [Tealium initSharedInstance:@"tealiummobile"
                        profile:@"demo"
                         target:@"dev"];

    [[TealiumADBMobileTagBridge sharedInstance] addRemoteCommandHandlers];
    
    return YES;
}
```

####Requirments

* Application specific Adobe SDK, downloaded by loging into [Adobe Mobile Servics](https://mobilemarketing.adobe.com).
* Tealium [iOS library](https://github.com/tealium/ios-library) version 4.1 or greater.
* Adobe module or custom tag added to your profile in [Tealium iQ](https://my.tealiumiq.com) utilizing the [TagBridge API](api_reference.md)


[top](#adobe-tagbridge-module)


###Android Library Setup:



[top](#adobe-tagbridge-module)


---

