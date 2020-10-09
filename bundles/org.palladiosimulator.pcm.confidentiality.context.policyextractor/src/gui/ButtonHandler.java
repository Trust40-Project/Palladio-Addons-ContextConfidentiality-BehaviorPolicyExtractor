package gui;

import java.util.Objects;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import main.MainHandler;
import preferences.PreferenceHandler;
import util.Logger;

/**
 * Connection between GUI and MainHandler
 * 
 * Referenced in plugin.xml
 * 
 * @author Thomas Lieb
 *
 */
public class ButtonHandler extends AbstractHandler {

    /**
     * Executed when GUI button is pressed
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Objects.requireNonNull(event);

        // Logger settings
        Logger.setActive(true);
        Logger.setDetailed(false);

        Logger.info("ButtonHandler-Start");

        // Needed here to ensure default values are set if PreferencesPage has never been opened
        PreferenceHandler.setDefault();

        // TODO move in mainhandler
        String path = PreferenceHandler.getProjectPath();
        Logger.info(path);
        new MainHandler().execute(path);

        Logger.info("ButtonHandler-End");

        return null;
    }
}
