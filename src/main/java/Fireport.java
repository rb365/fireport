import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * User: vassili
 * Date: 2/2/14
 * Time: 2:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class Fireport {
  public static void main(String[]args) {
    // Create a reference to a Firebase location
    Firebase ref = new Firebase("https://fireport.firebaseio.com/");

    // Write data to Firebase
    ref.setValue("Testing firebase!");

    // Read data and react to changes
    ref.addValueEventListener(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot snap) {
        System.out.println(snap.getName() + " -> " + snap.getValue());
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {}
    });

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {}
  }
}
