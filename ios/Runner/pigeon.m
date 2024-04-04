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

@interface AuthResponse ()
+ (AuthResponse *)fromList:(NSArray *)list;
+ (nullable AuthResponse *)nullableFromList:(NSArray *)list;
- (NSArray *)toList;
@end

@implementation AuthResponse
+ (instancetype)makeWithResponse:(NSString *)response
    userId:(NSString *)userId
    username:(NSString *)username
    userEmail:(NSString *)userEmail
    isOfficer:(NSNumber *)isOfficer
    isDefault:(NSNumber *)isDefault
    isSupervisor:(NSNumber *)isSupervisor
    isOperator:(NSNumber *)isOperator
    errorCode:(nullable NSString *)errorCode {
  AuthResponse* pigeonResult = [[AuthResponse alloc] init];
  pigeonResult.response = response;
  pigeonResult.userId = userId;
  pigeonResult.username = username;
  pigeonResult.userEmail = userEmail;
  pigeonResult.isOfficer = isOfficer;
  pigeonResult.isDefault = isDefault;
  pigeonResult.isSupervisor = isSupervisor;
  pigeonResult.isOperator = isOperator;
  pigeonResult.errorCode = errorCode;
  return pigeonResult;
}
+ (AuthResponse *)fromList:(NSArray *)list {
  AuthResponse *pigeonResult = [[AuthResponse alloc] init];
  pigeonResult.response = GetNullableObjectAtIndex(list, 0);
  NSAssert(pigeonResult.response != nil, @"");
  pigeonResult.userId = GetNullableObjectAtIndex(list, 1);
  NSAssert(pigeonResult.userId != nil, @"");
  pigeonResult.username = GetNullableObjectAtIndex(list, 2);
  NSAssert(pigeonResult.username != nil, @"");
  pigeonResult.userEmail = GetNullableObjectAtIndex(list, 3);
  NSAssert(pigeonResult.userEmail != nil, @"");
  pigeonResult.isOfficer = GetNullableObjectAtIndex(list, 4);
  NSAssert(pigeonResult.isOfficer != nil, @"");
  pigeonResult.isDefault = GetNullableObjectAtIndex(list, 5);
  NSAssert(pigeonResult.isDefault != nil, @"");
  pigeonResult.isSupervisor = GetNullableObjectAtIndex(list, 6);
  NSAssert(pigeonResult.isSupervisor != nil, @"");
  pigeonResult.isOperator = GetNullableObjectAtIndex(list, 7);
  NSAssert(pigeonResult.isOperator != nil, @"");
  pigeonResult.errorCode = GetNullableObjectAtIndex(list, 8);
  return pigeonResult;
}
+ (nullable AuthResponse *)nullableFromList:(NSArray *)list {
  return (list) ? [AuthResponse fromList:list] : nil;
}
- (NSArray *)toList {
  return @[
    (self.response ?: [NSNull null]),
    (self.userId ?: [NSNull null]),
    (self.username ?: [NSNull null]),
    (self.userEmail ?: [NSNull null]),
    (self.isOfficer ?: [NSNull null]),
    (self.isDefault ?: [NSNull null]),
    (self.isSupervisor ?: [NSNull null]),
    (self.isOperator ?: [NSNull null]),
    (self.errorCode ?: [NSNull null]),
  ];
}
@end

@interface AuthResponseApiCodecReader : FlutterStandardReader
@end
@implementation AuthResponseApiCodecReader
- (nullable id)readValueOfType:(UInt8)type {
  switch (type) {
    case 128: 
      return [AuthResponse fromList:[self readValue]];
    default:
      return [super readValueOfType:type];
  }
}
@end

@interface AuthResponseApiCodecWriter : FlutterStandardWriter
@end
@implementation AuthResponseApiCodecWriter
- (void)writeValue:(id)value {
  if ([value isKindOfClass:[AuthResponse class]]) {
    [self writeByte:128];
    [self writeValue:[value toList]];
  } else {
    [super writeValue:value];
  }
}
@end

@interface AuthResponseApiCodecReaderWriter : FlutterStandardReaderWriter
@end
@implementation AuthResponseApiCodecReaderWriter
- (FlutterStandardWriter *)writerWithData:(NSMutableData *)data {
  return [[AuthResponseApiCodecWriter alloc] initWithData:data];
}
- (FlutterStandardReader *)readerWithData:(NSData *)data {
  return [[AuthResponseApiCodecReader alloc] initWithData:data];
}
@end

NSObject<FlutterMessageCodec> *AuthResponseApiGetCodec(void) {
  static FlutterStandardMessageCodec *sSharedObject = nil;
  static dispatch_once_t sPred = 0;
  dispatch_once(&sPred, ^{
    AuthResponseApiCodecReaderWriter *readerWriter = [[AuthResponseApiCodecReaderWriter alloc] init];
    sSharedObject = [FlutterStandardMessageCodec codecWithReaderWriter:readerWriter];
  });
  return sSharedObject;
}

void AuthResponseApiSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<AuthResponseApi> *api) {
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.registration_client.AuthResponseApi.login"
        binaryMessenger:binaryMessenger
        codec:AuthResponseApiGetCodec()];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(loginUsername:password:isConnected:completion:)], @"AuthResponseApi api (%@) doesn't respond to @selector(loginUsername:password:isConnected:completion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        NSArray *args = message;
        NSString *arg_username = GetNullableObjectAtIndex(args, 0);
        NSString *arg_password = GetNullableObjectAtIndex(args, 1);
        NSNumber *arg_isConnected = GetNullableObjectAtIndex(args, 2);
        [api loginUsername:arg_username password:arg_password isConnected:arg_isConnected completion:^(AuthResponse *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    } else {
      [channel setMessageHandler:nil];
    }
  }
}
