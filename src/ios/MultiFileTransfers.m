#import "MultiFileTransfers.h"

@interface MultiFileTransfers ()
-(NSString*)uploadFile:(NSString*) url:(NSArray*) fileInfos:(NSDictionary*) param;
@end

@implementation MultiFileTransfers
-(void) pluginInitialize
{
}

-(void) testPlugin:(CDVInvokedUrlCommand*) command
{
    NSLog(@"Testing plugin....");
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Success!!!"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

-(NSString*) uploadFile:(NSString *)url :(NSArray *)fileInfos :(NSDictionary *)params
{
    NSLog(@"Uploading %lu files to %@", (unsigned long)fileInfos.count, url);
    
    NSString *BoundaryConstant = @"----------V2ymHFg03ehbqgZCaKO6jy";

    NSURL* requestURL = [NSURL URLWithString:url];

    NSMutableURLRequest* request = [[NSMutableURLRequest alloc] init];
    [request setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    [request setHTTPMethod:@"POST"];
    
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@", BoundaryConstant];
    [request setValue:contentType forHTTPHeaderField: @"Content-Type"];
    
    NSMutableData* body = [NSMutableData data];
    // add params (all params are strings)
    for (NSString *name in params) {
        NSLog(@"Appending %@ with value %@", name, [params objectForKey:name]);
        [body appendData:[[NSString stringWithFormat:@"--%@\r\n", BoundaryConstant] dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"\r\n\r\n", name] dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"%@\r\n", [params objectForKey:name]] dataUsingEncoding:NSUTF8StringEncoding]];
    }
    
    for (FileInfo *fileInfo in fileInfos) {
        NSData *data = [[NSData alloc]initWithBase64EncodedString:fileInfo.encodedData options:NSDataBase64DecodingIgnoreUnknownCharacters];
        UIImage* photo = [UIImage imageWithData:data];
        NSData* imageData = UIImageJPEGRepresentation(photo, 1.0);
        if (imageData) {
            NSLog(@"Appending image file %@ with key %@ and mimetype %@", fileInfo.fileName, fileInfo.fileKey, fileInfo.mimeType);
            [body appendData:[[NSString stringWithFormat:@"--%@\r\n", BoundaryConstant] dataUsingEncoding:NSUTF8StringEncoding]];
            [body appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\n", fileInfo.fileKey, fileInfo.fileName] dataUsingEncoding:NSUTF8StringEncoding]];
            [body appendData:[[NSString stringWithFormat:@"Content-Type: %@\r\n\r\n",fileInfo.mimeType] dataUsingEncoding:NSUTF8StringEncoding]];
            [body appendData:imageData];
            [body appendData:[[NSString stringWithFormat:@"\r\n"] dataUsingEncoding:NSUTF8StringEncoding]];
            //[body appendData:[[NSString stringWithFormat:@"--%@--\r\n", BoundaryConstant] dataUsingEncoding:NSUTF8StringEncoding]];
        }
    }
    [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", BoundaryConstant] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setHTTPBody:body];
    NSLog(@"Content-Length is %lu", (unsigned long) [body length]);
    NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[body length]];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];

    [request setURL:requestURL];
    
    NSError* error = nil;
    NSHTTPURLResponse* responseBody;
    NSData* response = [NSURLConnection sendSynchronousRequest:request returningResponse:&responseBody error:&error];
    
    NSLog(@"Status code = %d", [responseBody statusCode]);
    
    NSString* responseJson = [[NSString alloc] initWithData:response encoding:NSUTF8StringEncoding];
    if(!response || [responseBody statusCode] != 200) {
        NSLog(@"Error processing request: %@", error);
        NSLog(@"Error processing to URL %@", url);
        NSLog(@"Received response: %@", responseJson);
        return @"{status:500, message=\"Failed to upload. Ticket server is down.\"}";
    }
    
    NSLog(@"Received: %@", responseJson);
    return responseJson;
}

-(void) upload:(CDVInvokedUrlCommand *)command
{
    @try {
        NSArray* files = [command argumentAtIndex:0 withDefault:nil];
        NSString* url = [command argumentAtIndex:1 withDefault:nil];
        NSDictionary* params = [command argumentAtIndex:2 withDefault:nil];
        NSMutableArray* fileInfos = [[NSMutableArray alloc] init];
        
        for(NSDictionary* file in files) {
            FileInfo* fileInfo = [[FileInfo alloc] init];
            fileInfo.fileKey = [file objectForKey:@"fileKey"];
            fileInfo.fileName = [file objectForKey:@"fileName"];
            fileInfo.mimeType = [file objectForKey:@"mimeType"];
            fileInfo.encodedData = [file objectForKey:@"encodedData"];
            [fileInfos addObject:fileInfo];
        }
        
        [self.commandDelegate runInBackground:^{
            NSString* response = [self uploadFile:url :fileInfos :params];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: response];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }];
    }
    @catch (NSException *exception) {
        NSLog(@"Exception: %@, Reason: %@, StackSymbol: %@", exception.name, exception.reason, [exception callStackSymbols]);
    }
    @finally {
        
    }
}
@end

@implementation FileInfo

@synthesize fileKey;

@synthesize fileName;

@synthesize mimeType;

@synthesize encodedData;

@end
