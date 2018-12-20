#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>

@interface MultiFileTransfers : CDVPlugin {}
-(void) testPlugin:(CDVInvokedUrlCommand*) command;
-(void) upload:(CDVInvokedUrlCommand*) command;
@end

@interface FileInfo : NSObject {}
@property NSString* fileKey;
@property NSString* fileName;
@property NSString* mimeType;
@property NSString* encodedData;
@end
