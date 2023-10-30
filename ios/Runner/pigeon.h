// Autogenerated from Pigeon (v10.1.6), do not edit directly.
// See also: https://pub.dev/packages/pigeon

#import <Foundation/Foundation.h>

@protocol FlutterBinaryMessenger;
@protocol FlutterMessageCodec;
@class FlutterError;
@class FlutterStandardTypedData;

NS_ASSUME_NONNULL_BEGIN


/// The codec used by CommonDetailsApi.
NSObject<FlutterMessageCodec> *CommonDetailsApiGetCodec(void);

@protocol CommonDetailsApi
- (void)getTemplateContentTemplateName:(NSString *)templateName langCode:(NSString *)langCode completion:(void (^)(NSString *_Nullable, FlutterError *_Nullable))completion;
- (void)getPreviewTemplateContentTemplateTypeCode:(NSString *)templateTypeCode langCode:(NSString *)langCode completion:(void (^)(NSString *_Nullable, FlutterError *_Nullable))completion;
- (void)getDocumentTypesCategoryCode:(NSString *)categoryCode applicantType:(NSString *)applicantType langCode:(NSString *)langCode completion:(void (^)(NSArray<NSString *> *_Nullable, FlutterError *_Nullable))completion;
- (void)getFieldValuesFieldName:(NSString *)fieldName langCode:(NSString *)langCode completion:(void (^)(NSArray<NSString *> *_Nullable, FlutterError *_Nullable))completion;
- (void)saveVersionToGlobalParamId:(NSString *)id value:(NSString *)value completion:(void (^)(NSString *_Nullable, FlutterError *_Nullable))completion;
- (void)getVersionFromGlobalParamId:(NSString *)id completion:(void (^)(NSString *_Nullable, FlutterError *_Nullable))completion;
@end

extern void CommonDetailsApiSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<CommonDetailsApi> *_Nullable api);

NS_ASSUME_NONNULL_END
