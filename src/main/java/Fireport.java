import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Fireport {

  private static final String URL = "https://fireport.firebaseio.com/";

  public static void main(String[]args) throws IOException {
    String ip = null;
    int port = 0;
    boolean isServer = true;
    if (args.length == 3) {
      isServer = "server".equalsIgnoreCase(args[0]);
      ip = args[1];
      port = Integer.valueOf(args[2]);
    } else {
      System.out.println("fireport {server,client} {ip} {port}");
      System.out.println("sample: fireport server localhost 1234");
      System.out.println("sample: fireport client localhost 1234");
      System.exit(0);
    }

    Socket socket;
    if (isServer) {
      System.out.println("Connecting....");
      socket = new Socket(ip, port);
    } else {
      System.out.println("Waiting for connection....");
      socket = new ServerSocket(port).accept();
    }
    System.out.println("Connected!");

    final BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
    final OutputStream outputStream = socket.getOutputStream();
    Firebase output = new Firebase(URL + ip + "/output");
    Firebase input = new Firebase(URL + ip + "/input");

    connect(isServer ? input : output, isServer ? output : input, inputStream, outputStream);
  }

  public static void connect(Firebase input,
                             Firebase output,
                             final BufferedInputStream inputStream,
                             final OutputStream outputStream) throws IOException {
    // Read data and react to changes
    input.addValueEventListener(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot snap) {
        try {
          if (snap.getValue() != null) {
            System.out.println("Received:" + snap.getValue());
            byte [] val = DatatypeConverter.parseBase64Binary(snap.getValue().toString());
            outputStream.write(val);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });

    byte [] buf = new byte[100*1024];
    while (true) {
      int size = inputStream.read(buf);
      if (size  > 0) {
        String out = DatatypeConverter.printBase64Binary(Arrays.copyOf(buf, size));
        System.out.println("Sending:" + out);
        output.setValue(out);
      } else if (size < 0) {
        System.out.println("Reached EOF!");
        System.exit(0);
      }
    }
  }
}
