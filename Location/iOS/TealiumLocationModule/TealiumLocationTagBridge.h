//
//  TealiumLocationTagBridge.h
//  Modules_UICatelog
//
//  Created by George Webster on 12/17/14.
//  Copyright (c) 2014 f. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TealiumLocationTagBridge : NSObject

+ (instancetype)sharedInstance;

- (void)addRemoteCommandHandlers;

@end
