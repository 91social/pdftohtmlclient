package com.datasahi.pdftohtml.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;

public class PdfToHtmlClient {
  private static final Logger logger = Logger.getLogger(PdfToHtmlClient.class.getName());

  

 private final  ConvertPdfToHtmlGrpc.ConvertPdfToHtmlBlockingStub blockingStub;

  public PdfToHtmlClient(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.
   
    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    blockingStub = ConvertPdfToHtmlGrpc.newBlockingStub(channel);
  }

 
  public void convertRequest(String source, String destination, String password) {
    logger.info("Will try to connect to " + source + " ... "+ destination);
    
    Request request = Request.newBuilder().setSource(source).setDestination(destination).setPassword(password).build();
    
    Reply response;
    try {
      response = blockingStub.pdfToHtml(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Operation: " + response.getStatus());
  }

  
  public static void main(String[] args) throws Exception {


    String source , destination, password;
    if(args.length!=3 && args.length!=2)
    {
      System.err.println("please enter source, destination and password");
      System.exit(1);
    }
    // Access a service running on the local machine on port 50051
    String target = "localhost:50051";

    if (args.length > 0) {
      if ("--help".equals(args[0])) {
        System.err.println("Usage: [name [target]]");
        System.err.println("");
        System.err.println("  target  The server to connect to. Defaults to " + target);
        System.exit(1);
      }
    }

    
      source = args[0];
      destination = args[1];
      if(args.length==3)
      password = args[2];
      else
      password = "xxxx";

    // Create a communication channel to the server, known as a Channel. Channels are thread-safe
    // and reusable. It is common to create channels at the beginning of your application and reuse
    // them until the application shuts down.
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build();
    try {
      PdfToHtmlClient client = new PdfToHtmlClient(channel);
      client.convertRequest(source, destination, password);
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
