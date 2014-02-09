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
    String connectionName = null;
    if (args.length == 4 || args.length == 3) {
      connectionName = args[0];
      isServer = "server".equalsIgnoreCase(args[1]);
      port = Integer.valueOf(args[2]);
      ip = isServer ? null : (args.length == 4 ? args[3] : "localhost");
    } else {
      System.out.println("fireport {connection name} {server,client} {port} {ip}");
      System.out.println("ip is optional and only applicable for client. Default value is localhost.");
      System.out.println("sample: fireport vncwork server 1234");
      System.out.println("sample: fireport vncwork client localhost 1234");
      System.exit(0);
    }

    Socket socket;
    if (isServer) {
      System.out.println("Waiting for connection....");
      socket = new ServerSocket(port).accept();
    } else {
      System.out.println("Connecting....");
      socket = new Socket(ip, port);
    }
    System.out.println("Connected!");

    final BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
    final OutputStream outputStream = socket.getOutputStream();
    Firebase output = new Firebase(URL + connectionName + "/output");
    Firebase input  = new Firebase(URL + connectionName + "/input");

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
