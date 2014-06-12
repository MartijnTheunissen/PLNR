package be.uhasselt.oo2.mvc;

import java.util.Observable;
import java.util.Observer;

/**
 * Abstract View class
 * @author jvermeulen
 */
public abstract class AbstractView implements View, Observer {

    private Observable mModel;
    private Controller mController;
    
    /**
     * Empty constructor so that the model and controller can be set later.
     */
    public AbstractView() {        
    }
    
    public AbstractView(Observable model, Controller controller) {
        // Set the model.
        setModel(model);
        // If a controller was supplied, use it. Otherwise let the first call to 
        // getController() create the default controller.
        if (controller != null) {
            setController(controller);
        }
    }
    
    
    @Override
    public void setController(Controller controller) {
        mController = controller;
        // Tell the controller this object is its view.
        getController().setView(this);
    }

    @Override
    public Controller getController() {
        // If a controller hasn't been defined yet...
        if (mController == null) {
            // ...make one. Note that defaultController is normally overriden by 
            // the AbstractView subclass so that it returns the appropriate 
            // controller for the view.
            setController(defaultController(getModel()));
        }
        return mController;
    }

    @Override
    public void setModel(Observable model) {
        mModel = model;
    }

    @Override
    public Observable getModel() {
        return mModel;
    }

    @Override
    public Controller defaultController(Observable model) {
        return null;
    }

    /**
     * A do-nothing implementation of the Observer interface's update method.
     * Subclasses of AbstractView will provide a concrete implementation for 
     * this method.
     */
    @Override
    public void update(Observable o, Object arg) {    
    }
    
}
