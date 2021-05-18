package org.bff.javampd.monitor;

import com.google.inject.Singleton;
import org.bff.javampd.server.ErrorEvent;
import org.bff.javampd.server.ErrorListener;
import org.bff.javampd.server.Status;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class MPDErrorMonitor implements ErrorMonitor {
    private String error;
    private List<ErrorListener> errorListeners;

    MPDErrorMonitor() {
        this.errorListeners = new ArrayList<>();
    }

    @Override
    public synchronized void addErrorListener(ErrorListener el) {
        errorListeners.add(el);
    }

    @Override
    public synchronized void removeErrorListener(ErrorListener el) {
        errorListeners.remove(el);
    }

    /**
     * Sends the appropriate {@link ErrorListener} to all registered
     * {@link ErrorListener}s.
     *
     * @param message the event message
     */
    protected void fireMPDErrorEvent(String message) {
        ErrorEvent ee = new ErrorEvent(this, message);

        for (ErrorListener el : errorListeners) {
            el.errorEventReceived(ee);
        }
    }

    @Override
    public void processResponseStatus(String line) {
        var status = Status.lookup(line);
        if (status == Status.ERROR) {
            error = line.substring(Status.ERROR.getStatusPrefix().length()).trim();
        }
    }

    @Override
    public void reset() {
        error = null;
    }

    @Override
    public void checkStatus() {
        if (error != null) {
            fireMPDErrorEvent(error);
            error = null;
        }
    }
}
