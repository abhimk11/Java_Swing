package gui.inter;

import gui.inter.event.FormEvent;

import java.util.EventListener;

public interface FormListner extends EventListener {
    public void formEventOccurred(FormEvent e);
}
