// Autogenerated from Pigeon (v10.1.6), do not edit directly.
// See also: https://pub.dev/packages/pigeon

#import "pigeon.h"

#if TARGET_OS_OSX
#import <FlutterMacOS/FlutterMacOS.h>
#else
#import <Flutter/Flutter.h>
#endif

#if !__has_feature(objc_arc)
#error File requires ARC to be enabled.
#endif

static NSArray *wrapResult(id result, FlutterError *error) {
  if (error) {
    return @[
      error.code ?: [NSNull null], error.message ?: [NSNull null], error.details ?: [NSNull null]
    ];
  }
  return @[ result ?: [NSNull null] ];
}
static id GetNullableObjectAtIndex(NSArray *array, NSInteger key) {
  id result = array[key];
  return (result == [NSNull null]) ? nil : result;
}

@interface DashBoardData ()
+ (DashBoardData *)fromList:(NSArray *)list;
+ (nullable DashBoardData *)nullableFromList:(NSArray *)list;
- (NSArray *)toList;
@end

@interface UpdatedTimeData ()
+ (UpdatedTimeData *)fromList:(NSArray *)list;
+ (nullable UpdatedTimeData *)nullableFromList:(NSArray *)list;
- (NSArray *)toList;
@end

@implementation DashBoardData
+ (instancetype)makeWithUserId:(NSString *)userId
    userName:(NSString *)userName
    userStatus:(NSNumber *)userStatus
    userIsOnboarded:(NSNumber *)userIsOnboarded {
  DashBoardData* pigeonResult = [[DashBoardData alloc] init];
  pigeonResult.userId = userId;
  pigeonResult.userName = userName;
  pigeonResult.userStatus = userStatus;
  pigeonResult.userIsOnboarded = userIsOnboarded;
  return pigeonResult;
}
+ (DashBoardData *)fromList:(NSArray *)list {
  DashBoardData *pigeonResult = [[DashBoardData alloc] init];
  pigeonResult.userId = GetNullableObjectAtIndex(list, 0);
  NSAssert(pigeonResult.userId != nil, @"");
  pigeonResult.userName = GetNullableObjectAtIndex(list, 1);
  NSAssert(pigeonResult.userName != nil, @"");
  pigeonResult.userStatus = GetNullableObjectAtIndex(list, 2);
  NSAssert(pigeonResult.userStatus != nil, @"");
  pigeonResult.userIsOnboarded = GetNullableObjectAtIndex(list, 3);
  NSAssert(pigeonResult.userIsOnboarded != nil, @"");
  return pigeonResult;
}
+ (nullable DashBoardData *)nullableFromList:(NSArray *)list {
  return (list) ? [DashBoardData fromList:list] : nil;
}
- (NSArray *)toList {
  return @[
    (self.userId ?: [NSNull null]),
    (self.userName ?: [NSNull null]),
    (self.userStatus ?: [NSNull null]),
    (self.userIsOnboarded ?: [NSNull null]),
  ];
}
@end

@implementation UpdatedTimeData
+ (instancetype)makeWithUpdatedTime:(nullable NSString *)updatedTime {
  UpdatedTimeData* pigeonResult = [[UpdatedTimeData alloc] init];
  pigeonResult.updatedTime = updatedTime;
  return pigeonResult;
}
+ (UpdatedTimeData *)fromList:(NSArray *)list {
  UpdatedTimeData *pigeonResult = [[UpdatedTimeData alloc] init];
  pigeonResult.updatedTime = GetNullableObjectAtIndex(list, 0);
  return pigeonResult;
}
+ (nullable UpdatedTimeData *)nullableFromList:(NSArray *)list {
  return (list) ? [UpdatedTimeData fromList:list] : nil;
}
- (NSArray *)toList {
  return @[
    (self.updatedTime ?: [NSNull null]),
  ];
}
@end

@interface DashBoardApiCodecReader : FlutterStandardReader
@end
@implementation DashBoardApiCodecReader
- (nullable id)readValueOfType:(UInt8)type {
  switch (type) {
    case 128: 
      return [DashBoardData fromList:[self readValue]];
    case 129: 
      return [UpdatedTimeData fromList:[self readValue]];
    default:
      return [super readValueOfType:type];
  }
}
@end

@interface DashBoardApiCodecWriter : FlutterStandardWriter
@end
@implementation DashBoardApiCodecWriter
- (void)writeValue:(id)value {
  if ([value isKindOfClass:[DashBoardData class]]) {
    [self writeByte:128];
    [self writeValue:[value toList]];
  } else if ([value isKindOfClass:[UpdatedTimeData class]]) {
    [self writeByte:129];
    [self writeValue:[value toList]];
  } else {
    [super writeValue:value];
  }
}
@end

@interface DashBoardApiCodecReaderWriter : FlutterStandardReaderWriter
@end
@implementation DashBoardApiCodecReaderWriter
- (FlutterStandardWriter *)writerWithData:(NSMutableData *)data {
  return [[DashBoardApiCodecWriter alloc] initWithData:data];
}
- (FlutterStandardReader *)readerWithData:(NSData *)data {
  return [[DashBoardApiCodecReader alloc] initWithData:data];
}
@end

NSObject<FlutterMessageCodec> *DashBoardApiGetCodec(void) {
  static FlutterStandardMessageCodec *sSharedObject = nil;
  static dispatch_once_t sPred = 0;
  dispatch_once(&sPred, ^{
    DashBoardApiCodecReaderWriter *readerWriter = [[DashBoardApiCodecReaderWriter alloc] init];
    sSharedObject = [FlutterStandardMessageCodec codecWithReaderWriter:readerWriter];
  });
  return sSharedObject;
}

void DashBoardApiSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<DashBoardApi> *api) {
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.registration_client.DashBoardApi.getDashBoardDetails"
        binaryMessenger:binaryMessenger
        codec:DashBoardApiGetCodec()];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getDashBoardDetailsWithCompletion:)], @"DashBoardApi api (%@) doesn't respond to @selector(getDashBoardDetailsWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getDashBoardDetailsWithCompletion:^(NSArray<DashBoardData *> *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    } else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.registration_client.DashBoardApi.getPacketUploadedDetails"
        binaryMessenger:binaryMessenger
        codec:DashBoardApiGetCodec()];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getPacketUploadedDetailsWithCompletion:)], @"DashBoardApi api (%@) doesn't respond to @selector(getPacketUploadedDetailsWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getPacketUploadedDetailsWithCompletion:^(NSNumber *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    } else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.registration_client.DashBoardApi.getPacketUploadedPendingDetails"
        binaryMessenger:binaryMessenger
        codec:DashBoardApiGetCodec()];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getPacketUploadedPendingDetailsWithCompletion:)], @"DashBoardApi api (%@) doesn't respond to @selector(getPacketUploadedPendingDetailsWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getPacketUploadedPendingDetailsWithCompletion:^(NSNumber *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    } else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.registration_client.DashBoardApi.getCreatedPacketDetails"
        binaryMessenger:binaryMessenger
        codec:DashBoardApiGetCodec()];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getCreatedPacketDetailsWithCompletion:)], @"DashBoardApi api (%@) doesn't respond to @selector(getCreatedPacketDetailsWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getCreatedPacketDetailsWithCompletion:^(NSNumber *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    } else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.registration_client.DashBoardApi.getSyncedPacketDetails"
        binaryMessenger:binaryMessenger
        codec:DashBoardApiGetCodec()];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getSyncedPacketDetailsWithCompletion:)], @"DashBoardApi api (%@) doesn't respond to @selector(getSyncedPacketDetailsWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getSyncedPacketDetailsWithCompletion:^(NSNumber *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    } else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.registration_client.DashBoardApi.getUpdatedTime"
        binaryMessenger:binaryMessenger
        codec:DashBoardApiGetCodec()];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getUpdatedTimeWithCompletion:)], @"DashBoardApi api (%@) doesn't respond to @selector(getUpdatedTimeWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getUpdatedTimeWithCompletion:^(UpdatedTimeData *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    } else {
      [channel setMessageHandler:nil];
    }
  }
}
