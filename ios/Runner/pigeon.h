// Autogenerated from Pigeon (v10.1.6), do not edit directly.
// See also: https://pub.dev/packages/pigeon

#import <Foundation/Foundation.h>

@protocol FlutterBinaryMessenger;
@protocol FlutterMessageCodec;
@class FlutterError;
@class FlutterStandardTypedData;

NS_ASSUME_NONNULL_BEGIN

@class TransliterationOptions;

@interface TransliterationOptions : NSObject
/// `init` unavailable to enforce nonnull fields, see the `make` class method.
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)makeWithInput:(NSString *)input
    sourceLanguage:(NSString *)sourceLanguage
    targetLanguage:(NSString *)targetLanguage;
@property(nonatomic, copy) NSString * input;
@property(nonatomic, copy) NSString * sourceLanguage;
@property(nonatomic, copy) NSString * targetLanguage;
@end

/// The codec used by TransliterationApi.
NSObject<FlutterMessageCodec> *TransliterationApiGetCodec(void);

@protocol TransliterationApi
- (void)transliterateOptions:(TransliterationOptions *)options completion:(void (^)(NSString *_Nullable, FlutterError *_Nullable))completion;
@end

extern void TransliterationApiSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<TransliterationApi> *_Nullable api);

NS_ASSUME_NONNULL_END
