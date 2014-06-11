package be.uhasselt.oo2.mvc;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author jvermeulen
 */
public interface View extends Observer {
      
    void setController(Controller controller);
    Controller getController();
    
    void setModel(Observable model);
    Observable getModel();
    
    Controller defaultController(Observable model);
}
