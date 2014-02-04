import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class FireportServer {
  public static void main(String[]args) throws IOException {
    String ip = "localhost";
    int port = 5900;
    if (args.length == 2) {
      ip = args[0];
      port = Integer.valueOf(args[1]);
    }

    System.out.println("Connecting....");
    Socket clientSocket = new Socket(ip, port);
    final BufferedInputStream outToClient = new BufferedInputStream(clientSocket.getInputStream());
    final OutputStream inFromClient = clientSocket.getOutputStream();
    System.out.println("Connected!");

    // Create a reference to a Firebase location
    Firebase output = new Firebase("https://fireport.firebaseio.com/" + ip + "/output");
    Firebase input = new Firebase("https://fireport.firebaseio.com/" + ip + "/input");

    // Read data and react to changes
    input.addValueEventListener(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot snap) {
        try {
          if(snap.getValue() != null) {
            System.out.println("Received:" + snap.getValue());
            byte [] val = DatatypeConverter.parseBase64Binary(snap.getValue().toString());
            inFromClient.write(val);
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
    while(true) {
      int size = outToClient.read(buf);
      if (size  > 0) {
        byte [] res = new byte[size];
        System.arraycopy(buf, 0, res, 0, size);
        String out = DatatypeConverter.printBase64Binary(res);
        System.out.println("Sending:" + out);
        output.setValue(out);
      }
    }
  }
}
